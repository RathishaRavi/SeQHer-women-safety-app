package com.example.womenperonalsecurity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Audiorecording extends AppCompatActivity implements SensorEventListener {

    private static final int REQUEST_PERMISSION_CODE = 100;
    private SensorManager sensorManager;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private boolean isRecording = false;
    private long lastShakeTime = 0;
    private static final int SHAKE_THRESHOLD = 15;
    private String audioFilePath;
    private File audioDirectory;
    private String userCredential; // Stores the user's email or PIN

    private Button btnStopRecording, btnPlayRecording;
    private TextView tvStatus;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> recordings;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audiorecording);

        tvStatus = findViewById(R.id.tvStatus);
        btnStopRecording = findViewById(R.id.btnStopRecording);
        listView = findViewById(R.id.listView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        audioDirectory = new File(getExternalFilesDir(null), "Recordings");

        if (!audioDirectory.exists()) {
            audioDirectory.mkdirs();
        }
        // Retrieve user credentials (email or PIN) from Intent
        userCredential = getIntent().getStringExtra("USER_CREDENTIAL");

        // Request permissions
        if (!checkPermissions()) {
            requestPermissions();
        }

        btnStopRecording.setOnClickListener(v -> stopRecording());
        loadRecordings();
        listView.setOnItemClickListener((parent, view, position, id) -> playRecording(recordings.get(position)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
        else {
            Toast.makeText(this, "Accelerometer not found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double acceleration = Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;
            long currentTime = System.currentTimeMillis();

            if (acceleration > SHAKE_THRESHOLD) {
                if ((currentTime - lastShakeTime) > 1000) {
                    lastShakeTime = currentTime;
                    Toast.makeText(this, "Shake detected! Recording...", Toast.LENGTH_SHORT).show();
                    startRecording();
                }
            }
        }
    }

    private void startRecording() {
        if (!checkPermissions()) {
            requestPermissions();
            return;
        }

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(200);
        }
        File audioFile = new File(audioDirectory, "recording_" + System.currentTimeMillis() + ".mp3");
        audioFilePath = audioFile.getAbsolutePath(); // Correctly setting the file path

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(audioFilePath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            tvStatus.setText("Recording...");
            btnStopRecording.setVisibility(View.VISIBLE);
            saveRecording(audioFilePath);
            Toast.makeText(this, "Recording Started...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("AudioRecord", "Recording Failed: " + e.getMessage());
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = false;
                tvStatus.setText("Recording Saved!");
                btnStopRecording.setVisibility(View.GONE);
                btnPlayRecording.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Recording Saved: " + audioFilePath, Toast.LENGTH_LONG).show();
                // Refresh list
                loadRecordings();
            } catch (RuntimeException e) {
                Log.e("AudioRecord", "Stop failed: " + e.getMessage());
            }
        }
    }

    private void playRecording(String fileName) {
        File file = new File(audioDirectory, fileName);
        if (!file.exists()) {
            Toast.makeText(this, "File not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            tvStatus.setText("Playing Audio...");
            mediaPlayer.setOnCompletionListener(mp -> {
                tvStatus.setText("Playback Finished!");
                mediaPlayer.release();
                mediaPlayer = null;
            });
        } catch (IOException e) {
            Log.e("AudioRecord", "Playback Failed: " + e.getMessage());
        }
    }
    private void loadRecordings() {
        if (!audioDirectory.exists()) {
            audioDirectory.mkdirs();
        }
        SharedPreferences sharedPreferences = getSharedPreferences("AudioPrefs", MODE_PRIVATE);
        String savedRecordings = sharedPreferences.getString(userCredential, "");

        File[] files = audioDirectory.listFiles();
        recordings = new ArrayList<>();
        if (!savedRecordings.isEmpty()) {
            String[] savedFiles = savedRecordings.split(","); // Renamed variable
            for (String file : savedFiles) {
                recordings.add(file);
            }
        }
        if (files != null) {
            for (File file : files) {
                recordings.add(file.getName());
                Log.d("AudioRecord", "File found: " + file.getName());
            }
        }

        adapter = new ArrayAdapter<String>(this, R.layout.list_item_audio, R.id.tvFileName, recordings) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Button btnPlay = view.findViewById(R.id.btnPlay);
                Button btnDelete=view.findViewById(R.id.btnDelete);
                Button btnShare = view.findViewById(R.id.btnShare);

                btnPlay.setOnClickListener(v -> playRecording(recordings.get(position)));
                btnDelete.setOnClickListener(v -> deleteRecording(position));
                btnShare.setOnClickListener(v -> shareRecording(recordings.get(position)));
                return view;
            }
        };

        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged(); // Ensure ListView updates properly
    }
    private void saveRecording(String filePath) {
        SharedPreferences sharedPreferences = getSharedPreferences("AudioPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String existingRecordings = sharedPreferences.getString(userCredential, "");

        existingRecordings = existingRecordings.isEmpty() ? filePath : existingRecordings + "," + filePath;
        editor.putString(userCredential, existingRecordings);
        editor.apply();
    }
    private void deleteRecording(int position) {
        File file = new File(audioDirectory, recordings.get(position));
        if (file.exists() && file.delete()) {
            Toast.makeText(this, "Recording Deleted", Toast.LENGTH_SHORT).show();
            loadRecordings();
        } else {
            Toast.makeText(this, "Failed to Delete", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
    private void shareRecording(String fileName) {
        File file = new File(audioDirectory, fileName);
        if (!file.exists()) {
            Toast.makeText(this, "File not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("audio/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // Grant permission to receiving apps
        List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        startActivity(Intent.createChooser(shareIntent, "Share Recording"));
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, REQUEST_PERMISSION_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions Granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissions Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
