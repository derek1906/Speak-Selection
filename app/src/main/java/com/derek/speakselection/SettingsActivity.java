package com.derek.speakselection;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.derek.speakselection.Options.OptionsInfo;
import com.derek.speakselection.Options.OptionsInfoAccessor;
import com.derek.speakselection.Options.OptionsInfoAdapter;
import com.derek.speakselection.TTS.TTSHelper;
import com.derek.speakselection.Utils.Callback;
import com.derek.speakselection.Utils.DependenciesDescription;
import com.derek.speakselection.Utils.DependencyLoader;
import com.derek.speakselection.Utils.DependencySuccessCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    DependencyLoader dependencyLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDependencies();
    }

    private void initDependencies(){
        final HashMap<String, DependenciesDescription> dependencies = new HashMap<>();

        // Text To Speech engine
        dependencies.put("TTS", new DependenciesDescription(new String[]{"Preferences"}) {
            @Override
            public void init(DependencyLoader loader, final DependencySuccessCallback success) {
                SharedPreferences preferences = (SharedPreferences) loader.get("Preferences");
                final TTSHelper ttsHelper = new TTSHelper(SettingsActivity.this, preferences.getString("engine", ""));
                ttsHelper.setOnInitListener(new Callback() {
                    @Override
                    public Object call(Object obj) {
                        TextToSpeech tts = ttsHelper.getTTS();
                        success.call(tts);

                        return null;
                    }
                });
            }
        });

        // Preference Manager
        dependencies.put("Preferences", new DependenciesDescription() {
            @Override
            public void init(DependencyLoader loader, DependencySuccessCallback success) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                success.call(preferences);
            }
        });

        // Create DependencyLoader
        dependencyLoader = new DependencyLoader(dependencies, new Callback() {
            @Override
            public Object call(Object obj) {
                initList();
                return null;
            }
        });
    }

    private void initList() {
        final TextToSpeech tts = (TextToSpeech) dependencyLoader.get("TTS");
        final SharedPreferences preferences = (SharedPreferences) dependencyLoader.get("Preferences");

        final List<OptionsInfoAdapter> futureOptionsInfoAdapter = new ArrayList<>();
        List<OptionsInfo> options = new ArrayList<>();

        Callback updateTTSEngine = new Callback() {
            @Override
            public Object call(Object obj) {
                initDependencies();
                return null;
            }
        };

        options.add(new OptionsInfo("TTS Engine", this,
                new OptionsInfoAccessor<List<String>, Object>() {
                    @Override
                    public List<String> get() {
                        List<String> engineNames = new ArrayList<String>();
                        for(TextToSpeech.EngineInfo engine : tts.getEngines()){
                            engineNames.add(engine.name);
                        }
                        return engineNames;
                    }

                    @Override
                    public void set(Object value) {
                    }
                },
                new OptionsInfoAccessor<String, String>() {
                    @Override
                    public String get() {
                        return preferences.getString("engine", tts.getDefaultEngine());
                    }

                    @Override
                    public void set(String value) {
                        preferences.edit().putString("engine", value).apply();
                    }
                },
                updateTTSEngine
        ));

        options.add(new OptionsInfo("Language", this,
                new OptionsInfoAccessor<List<String>, Object>() {
                    @Override
                    public List<String> get() {
                        final List<String> languages = new ArrayList<>();
                        for (Locale locale : tts.getAvailableLanguages()) {
                            languages.add(locale.toString());
                        }
                        java.util.Collections.sort(languages);
                        return languages;
                    }

                    @Override
                    public void set(Object value) {
                    }
                },
                new OptionsInfoAccessor<String, String>() {
                    @Override
                    public String get() {
                        Voice defaultVoice = tts.getDefaultVoice();
                        if(defaultVoice != null) {
                            return preferences.getString("locale", defaultVoice.getLocale().toString());
                        }else{
                            return preferences.getString("locale", "en");
                        }
                    }

                    @Override
                    public void set(String value) {
                        preferences.edit().putString("locale", value).apply();
                    }
                },
                updateTTSEngine
        ));

        ListView list = (ListView) findViewById(R.id.options);
        OptionsInfoAdapter adapter = new OptionsInfoAdapter(this, options);
        futureOptionsInfoAdapter.add(adapter);
        list.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((TextToSpeech) dependencyLoader.get("TTS")).shutdown();
    }
}
