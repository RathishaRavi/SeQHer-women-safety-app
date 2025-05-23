package com.example.womenperonalsecurity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class welcome extends AppCompatActivity {

    private CheckBox checkBoxAgree;
    private Button btnstart;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        checkBoxAgree = findViewById(R.id.checkBoxAgree);
        btnstart = findViewById(R.id.btnRegister);

        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxAgree.isChecked()) {
                    // Navigate to the Registration Page
                    Intent intent = new Intent(welcome.this, login.class);
                    startActivity(intent);
                } else {
                    // Show warning if checkbox is not checked
                    Toast.makeText(welcome.this, "Please agree to the instructions before registering.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
