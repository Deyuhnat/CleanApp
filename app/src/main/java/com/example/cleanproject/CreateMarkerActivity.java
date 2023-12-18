package com.example.cleanproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
public class CreateMarkerActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextDescription, editTextLatitude, editTextLongitude;
    private Button buttonAddLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_marker);

        // Initialize UI elements
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextLatitude = findViewById(R.id.editTextLatitude);
        editTextLongitude = findViewById(R.id.editTextLongitude);
        buttonAddLocation = findViewById(R.id.buttonAddLocation);

        // Set click listener for the Add Location button
        buttonAddLocation.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString();
            String description = editTextDescription.getText().toString();
            double latitude;
            double longitude;

            try {
                latitude = Double.parseDouble(editTextLatitude.getText().toString());
                longitude = Double.parseDouble(editTextLongitude.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid latitude or longitude", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add location to Firestore
            addLocationToFirestore(title, description, latitude, longitude);
        });
    }

    private void addLocationToFirestore(String title, String description, double latitude, double longitude) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a Map or a POJO for the location details
        // Add the location to Firestore in the "cleanUpLocations" collection
        // On success, navigate back to MarkerListActivity
        db.collection("cleanUpLocations")
                .add(new MarkerItem(title, description, latitude, longitude))
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Location added successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close this activity and return to MarkerListActivity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error adding location", Toast.LENGTH_SHORT).show();
                });
    }

}