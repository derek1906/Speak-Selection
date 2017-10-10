package com.derek.speakselection;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.derek.speakselection.Options.OptionsInfo;
import com.derek.speakselection.Options.OptionsInfoAdapter;
import com.derek.speakselection.TTS.TTSHelper;
import com.derek.speakselection.Utils.Callback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        final List<OptionsInfoAdapter> futureOptionsInfoAdapter = new ArrayList<>();
        List<OptionsInfo> options = new ArrayList<>();
        options.add(new OptionsInfo("Language", preferences.getString("locale", "en_US")) {
            @Override
            public void action(final OptionsInfo option) {
                TTSHelper ttsHelper = new TTSHelper(getApplication());
                ttsHelper.setOnInitListener(new Callback() {
                    @Override
                    public Object call(Object obj) {
                        TextToSpeech tts = ((TTSHelper) obj).getTTS();
                        final List<String> languages = new ArrayList<>();
                        for (Locale locale : tts.getAvailableLanguages()) {
                            languages.add(locale.toString());
                        }
                        java.util.Collections.sort(languages);
                        String selectedLanguage = preferences.getString("locale", "en_US");

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Select language");
                        builder.setNegativeButton("Cancel", null);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ListView listview = ((AlertDialog) dialog).getListView();
                                String newSelectedLanguage = languages.get(listview.getCheckedItemPosition());
                                preferences.edit()
                                        .putString("locale", newSelectedLanguage)
                                        .apply();

                                dialog.dismiss();
                                option.value = newSelectedLanguage;

                                if(futureOptionsInfoAdapter.size() > 0) {
                                    futureOptionsInfoAdapter.get(0).notifyDataSetChanged();
                                }
                            }
                        });
                        builder.setSingleChoiceItems(languages.toArray(new String[0]), languages.indexOf(selectedLanguage), null);
                        AlertDialog dialog = builder.create();
                        dialog.show();

                        return null;
                    }
                });
            }
        });

        ListView list = (ListView) findViewById(R.id.options);
        OptionsInfoAdapter adapter = new OptionsInfoAdapter(this, options);
        futureOptionsInfoAdapter.add(adapter);
        list.setAdapter(adapter);
    }
}
