package com.example.womenperonalsecurity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Instructions extends AppCompatActivity {

    private RadioGroup locationRadioGroup;
    private ImageView image_back;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        locationRadioGroup = findViewById(R.id.locationRadioGroup);
        image_back = findViewById(R.id.back);

        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Instructions.this, home.class);
                startActivity(intent);
            }
        });

        locationRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                showSafetyDialog(checkedId);
            }
        });
    }

    private void showSafetyDialog(int checkedId) {
        String steps = "";

        if (checkedId == R.id.radio_Room) {
            steps = "If you're trapped in a room:\n" +
                    "1. Lock the door if possible\n" +
                    "2. Barricade with furniture\n" +
                    "3. Look for windows to escape\n" +
                    "4. Call emergency services\n" +
                    "5. Find objects for self-defense";
        } else if (checkedId == R.id.radio_Bathroom) {
            steps = "If you're in a bathroom:\n" +
                    "1. Lock the door immediately\n" +
                    "2. Turn on water to create noise\n" +
                    "3. Check for windows or vents\n" +
                    "4. Use toiletries as weapons if needed\n" +
                    "5. Send emergency text/SOS Alert ";
        } else if (checkedId == R.id.radio_Public) {
            steps = "If you're in a public place:\n" +
                    "1. Move toward crowds\n" +
                    "2. Seek help from security Personnel\n" +
                    "3. Make noise for attention or make siren alarm\n" +
                    "4. Head to well-lit areas\n" +
                    "5. Call emergency contacts";
        } else if (checkedId == R.id.radio_Parking) {
            steps = "If you're in a parking lot:\n" +
                    "1. Stay in well-lit areas\n" +
                    "2. Have keys ready as a weapon\n" +
                    "3. Check car before entering\n" +
                    "4. Run to populated areas\n" +
                    "5. Call for help immediately";
        } else if (checkedId == R.id.radio_Balcony) {
            steps = "If you're on a balcony:\n" +
                    "1. Scream for help\n" +
                    "2. Signal to neighbors\n" +
                    "3. Look for a safe escape route\n" +
                    "4. Use phone to call for help\n" +
                    "5. Stay visible to others\n"+
                    "6. Avoid leaning over the railing.";
        } else if (checkedId == R.id.radio_Street) {
            steps = "If you're on a street:\n" +
                    "1. Be aware of surroundings\n" +
                    "2. Stay in lit areas\n" +
                    "3. Pretend to call someone\n" +
                    "4. Avoid dark alleys\n" +
                    "5. Trust your instincts";
        } else if (checkedId == R.id.radio_Vehicle) {
            steps = "If you're in a vehicle:\n" +
                    "1. Keep doors locked\n" +
                    "2. Drive to the police station\n" + "3. Honk horn repeatedly\n" +
                    "4. Call emergency services\n" +
                    "5. Don't stop for strangers\n"+
                    "6. Send live location to your contacts";
        } else {
            steps = "Select a location to see safety steps";
        }

        // Show the dialog
        showDialog(steps);
    }

    private void showDialog(String message) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_instructionsteps);
        dialog.setCancelable(true);

        TextView safetyText = dialog.findViewById(R.id.safety_text);
        Button closeButton = dialog.findViewById(R.id.close_btn);

        safetyText.setText(message);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
