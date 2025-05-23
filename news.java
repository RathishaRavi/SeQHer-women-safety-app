package com.example.womenperonalsecurity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class news extends AppCompatActivity {
    private ImageView image_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        image_back = findViewById(R.id.back);

        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(news.this, home.class);
                startActivity(intent);
            }
        });
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        CardView newsCard = findViewById(R.id.womensnews);

        // Setting the header text programmatically (Optional)
        TextView headerText = findViewById(R.id.textView2);
        headerText.setText("Stay Safe | Stay Fierce");
    }
}
