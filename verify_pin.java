package com.example.womenperonalsecurity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class verify_pin extends AppCompatActivity {

    private EditText pinEditText;
    private Button okButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_pin); // Ensure this matches your layout file name

        // Initialize views
        pinEditText = findViewById(R.id.pin);
        okButton = findViewById(R.id.ok);

        // Set up the OK button click listener
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleOkButtonClick();
            }
        });
    }

    private void handleOkButtonClick() {
        String pin = pinEditText.getText().toString().trim();

        if (pin.isEmpty()) {
            Toast.makeText(this, "Please enter your security PIN", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "PIN entered: " + pin, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(verify_pin.this, register.class);
            startActivity(intent);
            finish();
        }
    }
}
