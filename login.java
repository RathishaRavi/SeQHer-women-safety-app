package com.example.womenperonalsecurity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class login extends AppCompatActivity {

    private ExecutorService executorService = Executors.newFixedThreadPool(3);

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView register = findViewById(R.id.reg);
        Button loginMail = findViewById(R.id.login_mail);
        Button pinButton = findViewById(R.id.pin_btn);

        loginMail.setOnClickListener(v -> showEmailLoginDialog());
        pinButton.setOnClickListener(v -> showPinLoginDialog());
        register.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, register.class);
            startActivity(intent);
        });
    }

    private void showEmailLoginDialog() {
        final Dialog dialog = new Dialog(login.this);
        dialog.setContentView(R.layout.activity_login_email);

        EditText mail = dialog.findViewById(R.id.EmailAddress);
        EditText passw = dialog.findViewById(R.id.Password);
        Button ok = dialog.findViewById(R.id.button);
        TextView reg = dialog.findViewById(R.id.reg);

        ok.setOnClickListener(v -> {
            String email = mail.getText().toString().trim();
            String password = passw.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(login.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            executorService.execute(() -> authenticateEmailUser(email, password, dialog));
        });

        reg.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, register.class);
            startActivity(intent);
        });

        dialog.show();
    }

    private void authenticateEmailUser(String email, String password, Dialog dialog) {
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        String storedEmail = sharedPreferences.getString("loggedemail", null);
        String storedPassword = sharedPreferences.getString("loggedpass", null);

        if (storedEmail != null && storedEmail.equals(email) && storedPassword != null && storedPassword.equals(password)) {
            runOnUiThread(() -> loginSuccess(email, dialog));
        } else {
            runOnUiThread(() -> Toast.makeText(login.this, "Invalid email or password", Toast.LENGTH_LONG).show());
        }
    }

    private void showPinLoginDialog() {
        Dialog dialog = new Dialog(login.this);
        dialog.setContentView(R.layout.activity_verify_pin);

        Button okBtn = dialog.findViewById(R.id.ok);
        EditText enterPin = dialog.findViewById(R.id.pin);

        okBtn.setOnClickListener(v -> {
            String enteredPin = enterPin.getText().toString().trim();

            if (enteredPin.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter your Security PIN", Toast.LENGTH_SHORT).show();
                return;
            }
            executorService.execute(() -> authenticatePinUser(enteredPin, dialog));
        });
        dialog.show();
    }

    private void authenticatePinUser(String enteredPin, Dialog dialog) {
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        String storedPin = sharedPreferences.getString("loggedpin", null);
        String storedEmail = sharedPreferences.getString("loggedemail", null);

        if (storedPin != null && storedPin.equals(enteredPin)) {
            runOnUiThread(() -> loginSuccess(storedEmail, dialog));
        } else {
            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "INVALID PIN", Toast.LENGTH_LONG).show());
        }
    }

    private void loginSuccess(String email, Dialog dialog) {
        runOnUiThread(() -> {
            Toast.makeText(login.this, "LOGIN SUCCESSFUL", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(login.this, home.class);
            intent.putExtra("userEmail", email);
            startActivity(intent);
            finish();
            if (dialog != null) dialog.dismiss();
        });
    }
}