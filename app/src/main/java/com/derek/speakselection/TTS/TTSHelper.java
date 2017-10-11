package com.derek.speakselection.TTS;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import com.derek.speakselection.Utils.Callback;

import java.util.Locale;

public class TTSHelper implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    private boolean ready = false;
    private String engine;
    private Locale locale;
    private WaitingJob waiting;
    private TTSSpeakListener speakListener;
    private Callback onInitListener;
    private Bundle params = new Bundle();
    private int id = 0;

    private UtteranceProgressListener utteranceProgressListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String s) {
            Log.d("TTSHelper", "Utterance start");
        }

        @Override
        public void onDone(String s) {
            Log.d("TTSHelper", "Utterance done");
            speakListener.onDone();
        }

        @Override
        public void onStop(String s, boolean interrupted) {
            Log.d("TTSHelper", "Utterance stopped");
            speakListener.onDone();
        }

        @Override
        public void onError(String s) {
            Log.d("TTSHelper", "Utterance error");
            speakListener.onDone();
        }
    };

    public TTSHelper(Context ctx, String engine, Locale locale) {
        this.engine = engine;
        this.locale = locale;
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");

        if(engine.length() < 1) {
            tts = new TextToSpeech(ctx, this);
        }else{
            tts = new TextToSpeech(ctx, this, engine);
        }
    }

    public TTSHelper(Context ctx, String engine) {
        this(ctx, engine, Locale.US);
    }


    @Override
    public void onInit(int status) {
        Log.d("TTSHelper", "onInit called!");
        if (status == TextToSpeech.SUCCESS) {
            // init TTSHelper engine
            tts.setLanguage(locale);
            tts.setOnUtteranceProgressListener(utteranceProgressListener);

            Log.d("TTSHelper", "TTSHelper ready");

            if (onInitListener != null) {
                onInitListener.call(this);
            }

            ready = true;
            if (waiting != null) {
                speak(waiting.text, waiting.listener);
                waiting = null;
            }
        } else {
            Log.d("TTSHelper", "TTSHelper cannot be initialized");
        }
    }

    public void speak(String text, TTSSpeakListener listener) {
        if (ready) {
            speakListener = listener;
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, String.valueOf(id++));
        } else {
            waiting = new WaitingJob(text, listener);
        }
    }

    public void setOnInitListener(Callback cb) {
        if (ready) {
            cb.call(this);
        }
        onInitListener = cb;
    }

    public TextToSpeech getTTS() {
        return tts;
    }

    public void destroy() {
        tts.shutdown();
    }

    private class WaitingJob {
        String text;
        TTSSpeakListener listener;

        WaitingJob(String text, TTSSpeakListener listener) {
            this.text = text;
            this.listener = listener;
        }
    }
}
