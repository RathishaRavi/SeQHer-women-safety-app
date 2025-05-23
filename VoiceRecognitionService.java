package com.example.womenperonalsecurity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Locale;

public class VoiceRecognitionService extends Service {
    private SpeechRecognizer speechRecognizer;
    private static final String CHANNEL_ID = "VoiceServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundService();
        initializeSpeechRecognizer();
    }

    private void startForegroundService() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Delete the existing channel first to ensure new settings are applied
            if (manager != null) {
                NotificationChannel existingChannel = manager.getNotificationChannel(CHANNEL_ID);
                if (existingChannel != null) {
                    manager.deleteNotificationChannel(CHANNEL_ID);
                }

                // Create a new channel with no sound or vibration
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID, "Voice Recognition Service",
                        NotificationManager.IMPORTANCE_LOW // Low importance prevents sound
                );
                channel.setSound(null, null); // Disable sound
                channel.enableVibration(false); // Disable vibration

                manager.createNotificationChannel(channel);
            }
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SOS Voice Detection Active")
                .setContentText("Listening for 'SOS' or 'HELP'...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_LOW) // Low priority to prevent sounds
                .setDefaults(0) // Ensures no default sound/vibration
                .setSilent(true) // Explicitly make it silent
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
    }


    private void initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && matches.size() > 0) {
                    String command = matches.get(0).toLowerCase();
                    if (command.contains("sos") || command.contains("help")) {
                        launchSOSAlert();
                    }
                }
                startListening();
            }

            @Override public void onReadyForSpeech(Bundle params) {}
            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}
            @Override public void onError(int error) { startListening(); }
            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onEvent(int eventType, Bundle params) {}

        });
        startListening();
    }

    private void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizer.startListening(intent);
    }

    private void launchSOSAlert() {
        Intent sosIntent = new Intent(this, sosalert.class);
        sosIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(sosIntent);
        Toast.makeText(this, "SOS Alert Triggered by Voice!", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startListening();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
