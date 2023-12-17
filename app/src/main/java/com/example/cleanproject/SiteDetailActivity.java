package com.example.cleanproject;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SiteDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_detail);

        TextView titleTextView = findViewById(R.id.siteTitle);
        TextView descriptionTextView = findViewById(R.id.siteDescription);

        // Get data from intent
        String siteTitle = getIntent().getStringExtra("siteTitle");
        String siteDescription = getIntent().getStringExtra("siteDescription");

        // Set data to TextViews
        titleTextView.setText(siteTitle);
        descriptionTextView.setText(siteDescription);
    }
}