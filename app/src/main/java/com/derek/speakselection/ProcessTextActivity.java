package com.derek.speakselection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ProcessTextActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process_text_activity_main);

        final CharSequence selectedText = getIntent().getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);

        Intent speakingService = new Intent(this, SpeakingService.class);
        speakingService.putExtra("text", selectedText);
        this.startService(speakingService);

        finish();
    }
}
