package com.example.womenperonalsecurity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class emergencycall extends AppCompatActivity {
    private ArrayList<String> contacts;
    private String userEmail, userPin;
    private ArrayAdapter<String> adapter;
    private ListView listViewContacts;
    private EditText editTextName, editTextNumber;
    private ImageView image_back;
    private SharedPreferences sharedPreferences;
    private ExecutorService executorService = Executors.newFixedThreadPool(3);

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergencycall);

        editTextName = findViewById(R.id.editTextName);
        editTextNumber = findViewById(R.id.editTextNumber);
        Button buttonAdd = findViewById(R.id.buttonAdd);
        listViewContacts = findViewById(R.id.listViewContacts);
        image_back = findViewById(R.id.back);

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        userEmail = sharedPreferences.getString("loggedemail", "");
        userPin = sharedPreferences.getString("loggedpin", "");

        if ((userEmail == null || userEmail.isEmpty()) && (userPin == null || userPin.isEmpty())) {
            Toast.makeText(this, "No valid login found! Please log in.", Toast.LENGTH_SHORT).show();
            finish();
        }

        contacts = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contacts);
        listViewContacts.setAdapter(adapter);

        image_back.setOnClickListener(v -> {
            Intent intent = new Intent(emergencycall.this, home.class);
            startActivity(intent);
        });

        loadContactsFromSharedPreferences();

        buttonAdd.setOnClickListener(v -> addContact());
        listViewContacts.setOnItemClickListener((parent, view, position, id) -> callContact(position));
        listViewContacts.setOnItemLongClickListener((parent, view, position, id) -> {
            showEditDeleteDialog(position);
            return true;
        });
    }

    private void addContact() {
        String name = editTextName.getText().toString().trim();
        String number = editTextNumber.getText().toString().trim();

        if (name.isEmpty() || number.isEmpty() || number.length() != 10 || !number.matches("\\d+")) {
            Toast.makeText(this, "Enter a valid name and 10-digit number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contacts.size() >= 5) {
            Toast.makeText(this, "Maximum of 5 contacts can be added", Toast.LENGTH_SHORT).show();
            return;
        }

        String newContact = name + " - " + number;
        if (contacts.contains(newContact)) {
            Toast.makeText(this, "Contact already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        contacts.add(newContact);
        adapter.notifyDataSetChanged();
        saveContactsToSharedPreferences();

        editTextName.setText("");
        editTextNumber.setText("");
    }

    private void callContact(int position) {
        String contactDetails = contacts.get(position);
        String number = contactDetails.split(" - ")[1];
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        startActivity(callIntent);
    }

    private void showEditDeleteDialog(final int position) {
        final String[] options = {"Edit", "Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an option")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        editContact(position);
                    } else if (which == 1) {
                        deleteContact(position);
                    }
                });
        builder.show();
    }

    private void editContact(final int position) {
        String[] contactDetails = contacts.get(position).split(" - ");
        final String currentName = contactDetails[0];
        final String currentNumber = contactDetails[1];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Contact");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText inputName = new EditText(this);
        inputName.setText(currentName);
        layout.addView(inputName);

        final EditText inputNumber = new EditText(this);
        inputNumber.setText(currentNumber);
        layout.addView(inputNumber);

        builder.setView(layout);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = inputName.getText().toString().trim();
            String newNumber = inputNumber.getText().toString().trim();
            if (newName.isEmpty() || newNumber.isEmpty() || newNumber.length() != 10 || !newNumber.matches("\\d+")) {
                Toast.makeText(this, "Enter a valid name and 10-digit number", Toast.LENGTH_SHORT).show();
                return;
            }
            contacts.set(position, newName + " - " + newNumber);
            adapter.notifyDataSetChanged();
            saveContactsToSharedPreferences();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void deleteContact(final int position) {
        contacts.remove(position);
        adapter.notifyDataSetChanged();
        saveContactsToSharedPreferences();
    }

    private void saveContactsToSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> contactSet = new HashSet<>(contacts);
        editor.putStringSet("contacts_" + userEmail, contactSet);
        editor.apply();
    }

    private void loadContactsFromSharedPreferences() {
        Set<String> contactSet = sharedPreferences.getStringSet("contacts_" + userEmail, new HashSet<>());
        contacts.clear();
        contacts.addAll(contactSet);
        adapter.notifyDataSetChanged();
    }
}