package com.example.womenperonalsecurity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(MainActivity.this, fingerprint.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
        Intent serviceIntent = new Intent(this, VoiceRecognitionService.class);
        startService(serviceIntent);
    }
}