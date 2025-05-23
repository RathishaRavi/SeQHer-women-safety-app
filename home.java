package com.example.womenperonalsecurity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;
import android.view.KeyEvent;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class home extends AppCompatActivity {


    private CardView emergencySOSCard;
    private CardView emergencyCallCard;
    private CardView selfDefenseCard;
    private CardView addContactsCard;
    private CardView liveLocationCard;
    private CardView feedbackCard;
    private CardView newsCard;
    private CardView sirenCard;
    private CardView guideCard;
    private CardView hiddencameraCard;
    private CardView aboutUsCard;
    private CardView policestation;
    private CardView audio;

    private MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;
    private long lastVolumePressTime = 0;
    private static final long DOUBLE_PRESS_INTERVAL = 500; // 500ms for double press detection
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final int GPS_REQUEST_CODE = 101;
    private FusedLocationProviderClient fusedLocationClient;
    private String email, pin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // Ensure this matches your layout file name

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                moveTaskToBack(true);
                finishAffinity();
            }
        });
        mediaPlayer = MediaPlayer.create(this, R.raw.siren_sound);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize CardViews
        emergencySOSCard = findViewById(R.id.EmergencySOS);
        emergencyCallCard = findViewById(R.id.EmergencyCall);
        liveLocationCard = findViewById(R.id.LiveLocation);
        sirenCard = findViewById(R.id.Siren);
        addContactsCard = findViewById(R.id.AddContacts);
        selfDefenseCard = findViewById(R.id.SelfDefense);
        aboutUsCard = findViewById(R.id.aboutus);
        feedbackCard = findViewById(R.id.Feedback);
        newsCard = findViewById(R.id.womensnews);
        guideCard = findViewById(R.id.guide);
        hiddencameraCard = findViewById(R.id.Hiddencamera);
        policestation =findViewById(R.id.NearbyPolice);
        audio=findViewById(R.id.Audiorecording);

        policestation.setOnClickListener(v -> findNearbyPoliceStations());

        SharedPreferences sharedPrefs = getSharedPreferences("user", MODE_PRIVATE);
        email = sharedPrefs.getString("loggedemail", "");
        pin = sharedPrefs.getString("loggedpin", "");

        emergencyCallCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(home.this, emergencycall.class);
                intent.putExtra("user_email", email);
                intent.putExtra("user_pin", pin);
                startActivity(intent);

            }
        });

        emergencySOSCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle About Us action
                Toast.makeText(home.this, "SOS Button clicked", Toast.LENGTH_SHORT).show();
                Intent intentsos = new Intent(home.this, sosalert.class);
                startActivity(intentsos);
            }
        });

        hiddencameraCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(home.this, "Spy Camera Detection clicked", Toast.LENGTH_SHORT).show();
                Intent intentcamera = new Intent(home.this, SpyCamera.class);
                startActivity(intentcamera);
            }
        });
        guideCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle About Us action
                Toast.makeText(home.this, "Guide Instructions clicked", Toast.LENGTH_SHORT).show();
                Intent guide = new Intent(home.this, Instructions.class);
                startActivity(guide);
            }
        });
        aboutUsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle About Us action
                Toast.makeText(home.this, "About Us clicked", Toast.LENGTH_SHORT).show();
                Intent about = new Intent(home.this, aboutus.class);
                startActivity(about);
            }
        });
        selfDefenseCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Self Defense action
                Toast.makeText(home.this, "Self Defense clicked", Toast.LENGTH_SHORT).show();
                String url = "https://youtu.be/jAh0cU1J5zk?si=flaaR10PE_oHGSxR";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        addContactsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this, contact.class);
                intent.putExtra("user_email", email);
                startActivity(intent);
            }
        });

        liveLocationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Live Location action
                Toast.makeText(home.this, "Get Live Location", Toast.LENGTH_SHORT).show();
                Intent intentlocation = new Intent(home.this, livelocation.class);
                startActivity(intentlocation);
            }
        });

        feedbackCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Feedback action
                Toast.makeText(home.this, "Feedback clicked", Toast.LENGTH_SHORT).show();
                Intent intentfeedback = new Intent(home.this, feedback.class);
                startActivity(intentfeedback);
            }
        });
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Feedback action
                Toast.makeText(home.this, "Audio Recording clicked", Toast.LENGTH_SHORT).show();
                Intent intentaudio = new Intent(home.this, Audiorecording.class);
                startActivity(intentaudio);
            }
        });
        newsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle News action
                Toast.makeText(home.this, "Women's News clicked", Toast.LENGTH_SHORT).show();
                Intent inews = new Intent(home.this, news.class);
                startActivity(inews);
            }
        });

        sirenCard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // Handle Siren action
                Toast.makeText(home.this, "Siren Alert clicked", Toast.LENGTH_SHORT).show();
                playSiren();
            }
        });
        sirenCard.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick (View v){
                // Handle long press to stop the siren
                stopSiren();
                return true;
            }
        });
    }
    private void findNearbyPoliceStations() {
        if (!isGPSEnabled()) {
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_REQUEST_CODE);
        } else {
            if (ContextCompat.checkSelfPermission(home.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
            } else {
                fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            double currentLat = location.getLatitude();
                            double currentLng = location.getLongitude();
                            Toast.makeText(home.this, "Searching for nearby police stations", Toast.LENGTH_SHORT).show();
                            openGoogleMaps(currentLat, currentLng);
                        } else {
                            Toast.makeText(home.this, "Failed to get location. Using default search.", Toast.LENGTH_SHORT).show();
                            openGoogleMaps(0, 0); // Opens Google Maps search for police stations
                        }
                    }
                });
            }
        }
    }
    private void playSiren() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            Toast.makeText(this, "Siren Alert Activated!", Toast.LENGTH_SHORT).show();
        }
    }
    private void stopSiren() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            Toast.makeText(this, "Siren Alert Deactivated!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastVolumePressTime < DOUBLE_PRESS_INTERVAL) {
                playSiren();  // 🚨 Play siren if double press detected
            }
            else {
                // Check for long press (press and hold for more than 1 second)
                if (event.getEventTime() - event.getDownTime() > 1000) {  // 1000ms = 1 second
                    stopSiren();  // 🚨 Stop siren if long press detected
                }
            }
            lastVolumePressTime = currentTime;
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_REQUEST_CODE) {
            if (isGPSEnabled()) {
                policestation.performClick(); // Retry fetching location
            } else {
                Toast.makeText(this, "GPS is still disabled. Please enable it.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    private void openGoogleMaps(double latitude, double longitude) {
        String uri = "geo:" + latitude + "," + longitude + "?q=police station&sortby=distance";
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // Open Google Maps in a browser
            String webUri = "https://www.google.com/maps/search/?api=1&query=police+station";
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUri));
            startActivity(webIntent);
        }
    }


    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                findNearbyPoliceStations();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }

    }

}