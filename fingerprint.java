package com.example.womenperonalsecurity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class fingerprint extends AppCompatActivity {
    private BiometricPrompt biometricPrompt;
    private Executor executor;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(fingerprint.this, "Authentication Error: " + errString, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(fingerprint.this, login.class);
                startActivity(intent);
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(fingerprint.this, "Fingerprint Authentication Succeeded!", Toast.LENGTH_SHORT).show();
                // Proceed to next activity or operation
                Intent intent = new Intent(fingerprint.this, home.class);
                startActivity(intent);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(fingerprint.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(fingerprint.this, login.class);
                startActivity(intent);
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Fingerprint Authentication")
                .setSubtitle("Place your finger on the sensor")
                .setNegativeButtonText("Cancel")
                .build();
        biometricPrompt.authenticate(promptInfo);
//PIN code authentication button
    }
}

