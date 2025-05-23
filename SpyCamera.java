package com.example.womenperonalsecurity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class SpyCamera extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor magneticSensor;
    private TextView statusText;
    private TextToSpeech textToSpeech;
    private static final float MAGNETIC_THRESHOLD = 60.0f; // Threshold for hidden camera detection
    private boolean isAlertActive = false; // Prevents repeated alerts
    private ImageView image_back;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spycamera); // Ensure this layout exists

        statusText = findViewById(R.id.statusText);
        image_back = findViewById(R.id.back);

        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SpyCamera.this, home.class);
                startActivity(intent);
            }
        });
        // Initialize Sensor Manager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }

        // Check if magnetometer is available
        if (magneticSensor == null) {
            Toast.makeText(this, "Magnetic sensor not available!", Toast.LENGTH_LONG).show();
            finish(); // Close activity if sensor is unavailable
        }

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US); // Set language to US English
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (magneticSensor != null) {
            sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (magneticSensor != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float magneticStrength = (float) Math.sqrt(
                    event.values[0] * event.values[0] +
                            event.values[1] * event.values[1] +
                            event.values[2] * event.values[2]);

            // Update UI
            statusText.setText("Magnetic Field: " + magneticStrength + " µT");

            // If the magnetic field is unusually high, alert the user
            if (magneticStrength > MAGNETIC_THRESHOLD && !isAlertActive) {
                isAlertActive = true; // Activate alert
                Toast.makeText(this, "⚠️ Possible Hidden Camera Detected!", Toast.LENGTH_LONG).show();
                statusText.setText("🚨 Suspicious Magnetic Activity Detected!");

                // Speak "Camera detected"
                speakWarning();

                // Reset the alert after 5 seconds (to detect new threats)
                resetAlert();
            }
        }
    }

    private void speakWarning() {
        textToSpeech.speak("Camera detected", TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void resetAlert() {
        new android.os.Handler().postDelayed(() -> isAlertActive = false, 5000); // Reset alert after 5 sec
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this use case
    }
}

