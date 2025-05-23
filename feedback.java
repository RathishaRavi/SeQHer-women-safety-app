package com.example.womenperonalsecurity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.womenperonalsecurity.R;


public class feedback extends AppCompatActivity {

    private EditText feedbackEditText;
    private RatingBar ratingBar;
    private Button submitButton;
    private ImageView image_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback); // Ensure this matches your layout file name
        image_back = findViewById(R.id.back);

        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(feedback.this, home.class);
                startActivity(intent);
            }
        });

        feedbackEditText = findViewById(R.id.feedback_edit_text);
        ratingBar = findViewById(R.id.rating_bar);
        submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleFeedbackSubmission();
            }
        });
    }
    private void sendFeedbackEmail(String feedback, float rating) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"seqher.sr@gmail.com"}); // Replace with owner's email
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "User Feedback");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Rating: " + rating + "\nFeedback: " + feedback);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email using"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleFeedbackSubmission() {
        String feedback = feedbackEditText.getText().toString().trim();
        float rating = ratingBar.getRating();

        if (feedback.isEmpty()) {
            Toast.makeText(this, "Please enter your feedback", Toast.LENGTH_SHORT).show();
        } else if (rating == 0) {
            Toast.makeText(feedback.this, "Please provide a rating",Toast.LENGTH_SHORT).show();
        } else {
            sendFeedbackEmail(feedback, rating);
            Toast.makeText(this, "Feedback submitted: " + feedback + "\nRating: " + rating, Toast.LENGTH_SHORT).show();
            feedbackEditText.setText(""); // Clear the input field after submission
            ratingBar.setRating(0);
        }
    }

}