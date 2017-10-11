package com.derek.speakselection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ShareTextActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final CharSequence selectedText = getIntent().getCharSequenceExtra(Intent.EXTRA_TEXT);

        Intent speakingService = new Intent(this, SpeakingService.class);
        speakingService.putExtra("text", selectedText);
        this.startService(speakingService);

        finish();
    }
}
