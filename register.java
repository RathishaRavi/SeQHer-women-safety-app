package com.example.womenperonalsecurity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;


public class register extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword,ageEditText, addressEditText, stateEditText, districtEditText;
    private Spinner countryspinner;
    private Button buttonRegister;
    private TextView textViewLogin;
    public static String  selectedCountry;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextUsername = findViewById(R.id.editTextText);
        editTextEmail = findViewById(R.id.editTextTextEmailAddress2);
        editTextPassword = findViewById(R.id.editTextTextPassword2);
        editTextConfirmPassword = findViewById(R.id.editTextTextPassword3);
        buttonRegister = findViewById(R.id.button2);
        textViewLogin = findViewById(R.id.textView6);
        ageEditText = findViewById(R.id.et_age);
        addressEditText = findViewById(R.id.et_address);
        stateEditText = findViewById(R.id.et_state);
        countryspinner=findViewById(R.id.et_country);
        districtEditText = findViewById(R.id.et_district);

        ArrayList<String> countryList = new ArrayList<>(Arrays.asList(
                "Select Country", "Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Argentina",
                "Armenia", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh",
                "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bhutan", "Bolivia", "Bosnia and Herzegovina",
                "Botswana", "Brazil", "Brunei", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon",
                "Canada", "Cape Verde", "Central African Republic", "Chad", "Chile", "China", "Colombia",
                "Comoros", "Congo", "Costa Rica", "Croatia", "Cuba", "Cyprus", "Czech Republic", "Denmark",
                "Djibouti", "Dominica", "Dominican Republic", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea",
                "Eritrea", "Estonia", "Eswatini", "Ethiopia", "Fiji", "Finland", "France", "Gabon", "Gambia", "Georgia",
                "Germany", "Ghana", "Greece", "Grenada", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti",
                "Honduras", "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy",
                "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Kuwait", "Kyrgyzstan", "Laos",
                "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg",
                "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Mauritania", "Mauritius", "Mexico",
                "Moldova", "Monaco", "Mongolia", "Montenegro", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru",
                "Nepal", "Netherlands", "New Zealand", "Nicaragua", "Niger", "Nigeria", "North Korea", "North Macedonia",
                "Norway", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines",
                "Poland", "Portugal", "Qatar", "Romania", "Russia", "Rwanda", "Saint Kitts and Nevis", "Saint Lucia",
                "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Saudi Arabia", "Senegal", "Serbia",
                "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia",
                "South Africa", "South Korea", "South Sudan", "Spain", "Sri Lanka", "Sudan", "Suriname", "Sweden",
                "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Togo", "Tonga", "Trinidad and Tobago",
                "Tunisia", "Turkey", "Turkmenistan", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom",
                "United States", "Uruguay", "Uzbekistan", "Vanuatu", "Vatican City", "Venezuela", "Vietnam", "Yemen",
                "Zambia", "Zimbabwe"
        ));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, countryList);
        countryspinner.setAdapter(adapter);

        countryspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCountry = countryList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCountry = "Select Country";
            }

        });
        textViewLogin.setOnClickListener(v -> {
            Intent intenttext = new Intent(register.this, login.class);
            startActivity(intenttext);
        });


        buttonRegister.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String confirmPassword = editTextConfirmPassword.getText().toString().trim();
            String age = ageEditText.getText().toString();
            String address = addressEditText.getText().toString();
            String state = stateEditText.getText().toString();
            String district = districtEditText.getText().toString();

            // Validation Checks
            if (username.isEmpty()) {
                Toast.makeText(register.this,"Please enter your name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (email.isEmpty()) {
                Toast.makeText(register.this,"Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isValidEmail(email)) {
                Toast.makeText(register.this,"Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.isEmpty()) {
                Toast.makeText(register.this,"Please enter your password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (confirmPassword.isEmpty()) {
                Toast.makeText(register.this,"Please confirm your password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(register.this,"Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            if (age.isEmpty()) {
                Toast.makeText(register.this,"Please enter your age", Toast.LENGTH_SHORT).show();
                return;
            }
            if (address.isEmpty()) {
                Toast.makeText(register.this,"Please enter your address", Toast.LENGTH_SHORT).show();
                return;
            }
            if (state.isEmpty()) {
                Toast.makeText(register.this,"Please enter your state", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedCountry.equals("Select Country")) {
                Toast.makeText(register.this,"Please select your country", Toast.LENGTH_SHORT).show();
                return;
            }
            if (district.isEmpty()) {
                Toast.makeText(register.this,"Please enter your district", Toast.LENGTH_SHORT).show();
                return;
            }
            SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
            String existingEmail = sharedPreferences.getString("loggedemail", "");
            String existingPin = sharedPreferences.getString("loggedpin", "");

            if (email.equals(existingEmail)) {
                Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
                return;
            }

            showPinDialog(username, email, password, age, address, state, district,selectedCountry);
        });
    }

    private void showPinDialog(String username, String email, String password, String age, String address, String state, String district,String country) {
        Log.d("DEBUG", "Showing PIN dialog");
        final Dialog dialog = new Dialog(register.this);
        dialog.setContentView(R.layout.activity_set_pin);
        dialog.setCancelable(false); // Prevent dialog from closing accidentally
        EditText setPin = dialog.findViewById(R.id.set_pin);
        Button set = dialog.findViewById(R.id.set_btn);

        set.setOnClickListener(v -> {
            String pin = setPin.getText().toString().trim();
            if (pin.isEmpty() || pin.length() != 4 || !pin.matches("\\d+")) {
                Toast.makeText(register.this, "PIN must be 4-digit numbers", Toast.LENGTH_SHORT).show();
                return;
            }
            SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
            if (pin.equals(sharedPreferences.getString("loggedpin", ""))) {
                Toast.makeText(register.this, "PIN already in use. Choose another.", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("loggedemail", email);
            editor.putString("loggedpass", password);
            editor.putString("loggedpin", pin);
            editor.apply();

            Toast.makeText(register.this, "Registered Successfully", Toast.LENGTH_LONG).show();
            dialog.dismiss();
            Intent intent = new Intent(register.this, welcome.class);
            intent.putExtra("userEmail", email);
            startActivity(intent);
            finish();
        });
        dialog.show();
    }

    private boolean isValidEmail(String email) {
        return email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");
    }
}