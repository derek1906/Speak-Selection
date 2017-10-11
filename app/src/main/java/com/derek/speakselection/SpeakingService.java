package com.derek.speakselection;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.derek.speakselection.TTS.TTSHelper;
import com.derek.speakselection.TTS.TTSSpeakListener;

import java.util.Locale;

public class SpeakingService extends Service {
    static final String KEY_STOPPLAYBACK = "com.derek.SpeakSelection.SpeakingService.stopPlayback";
    TTSHelper ttsHelper;
    BroadcastReceiver broadcastReceiver;

    public SpeakingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("SpeakingService", "Starting service...");

        // Check volume level
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        int volumeLevel = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        if(volumeLevel == 0) {
            Toast.makeText(this, "Increase volume to listen to selection.", Toast.LENGTH_LONG).show();
        }

        // Get locale to be used
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String engine = preferences.getString("engine", "");
        Locale locale = new Locale(preferences.getString("locale", "en_US"));

        // Initialize TTS helper
        ttsHelper = new TTSHelper(this, engine , locale);

        // Get selected string
        String text = intent.getStringExtra("text");

        // Create intents for notification actions
        Intent stopPlaybackIntent = new Intent(KEY_STOPPLAYBACK);
        PendingIntent stopPlaybackPendingIntent = PendingIntent.getBroadcast(this, 0, stopPlaybackIntent, 0);
        IntentFilter filter = new IntentFilter();
        filter.addAction(KEY_STOPPLAYBACK);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()){
                    case KEY_STOPPLAYBACK:
                        Log.d("TTS", "Stopping playback...");
                        ttsHelper.getTTS().stop();
                        break;
                }
            }
        };
        registerReceiver(broadcastReceiver, filter);

        // Create notification
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_volume_up_white_24dp)
                .setContentTitle("Speak Selection")
                .setContentText("Playing...")
                .addAction(R.drawable.ic_stop_black_24dp, "Stop", stopPlaybackPendingIntent)
                .build();
        final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);

        // Read selected text
        ttsHelper.speak(text, new TTSSpeakListener() {
            @Override
            public void onDone() {
                Log.d("TTSHelper", "Stopping service...");
                // Clear notification
                notificationManager.cancel(0);
                // Stop the service
                SpeakingService.this.stopSelf();
            }
        });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("TTSHelper", "Destroying service...");
        // Destroy TTS helper
        ttsHelper.destroy();
        // Unregister broadcast receiver
        unregisterReceiver(broadcastReceiver);

        super.onDestroy();
    }
}
