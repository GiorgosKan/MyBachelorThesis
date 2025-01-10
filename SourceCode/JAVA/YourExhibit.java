package com.example.culturalcompass;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;

public class YourExhibit extends AppCompatActivity {

    private TextView exhibitNameTextView;
    private TextView exhibitInfoTextView;
    private ImageView exhibitImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yourexhibit);

        exhibitNameTextView = findViewById(R.id.exhibitNameTextView);
        exhibitInfoTextView = findViewById(R.id.exhibitInfoTextView);
        exhibitImageView = findViewById(R.id.exhibitImageView);

        // Ανάκτηση των δεδομένων από το Intent
        String exhibitName = getIntent().getStringExtra("exhibitName");
        String exhibitInfo = getIntent().getStringExtra("exhibitInfo");
        String exhibitImagePath = getIntent().getStringExtra("exhibitImagePath");

        // Εμφάνιση των δεδομένων
        exhibitNameTextView.setText(exhibitName);
        exhibitInfoTextView.setText(exhibitInfo);
        loadExhibitImageView(exhibitImagePath);
    }

    private void loadExhibitImageView(String exhibitImagePath) {
        Glide.with(this)
                .load(exhibitImagePath)
                //.placeholder(R.drawable.placeholderr)
                //.error(R.drawable.error)
                .into(exhibitImageView);

    }
}