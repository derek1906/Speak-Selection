package com.derek.speakselection;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.derek.speakselection.TTS.TTSHelper;
import com.derek.speakselection.TTS.TTSSpeakListener;

import java.util.Locale;

public class SpeakingService extends Service {
    TTSHelper ttsHelper;

    public SpeakingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("SpeakingService", "Starting service...");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Locale locale = new Locale(preferences.getString("locale", "en_US"));

        ttsHelper = new TTSHelper(this, locale);

        String text = intent.getStringExtra("text");
        final Service self = this;

        ttsHelper.speak(text, new TTSSpeakListener() {
            @Override
            public void onDone() {
                Log.d("TTSHelper", "Stopping service...");
                self.stopSelf();
            }
        });


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("TTSHelper", "Destroying service...");
        ttsHelper.destroy();
        super.onDestroy();
    }
}
