package com.example.cleanproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class MarkerListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MarkerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_list);

        recyclerView = findViewById(R.id.recycler_view_markers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MarkerAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        fetchDataFromFirestore();
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Navigate back to MapsActivity
            Intent intent = new Intent(MarkerListActivity.this, MapsActivity.class);
            startActivity(intent);
            finish();  // Close this activity
        });

        Button createButton = findViewById(R.id.createButton);
        createButton.setOnClickListener(v -> {
            showCreateMarkerDialog();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the data every time the activity resumes
        fetchDataFromFirestore();
    }

    private void fetchDataFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cleanUpLocations").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<MarkerItem> markerItems = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String title = document.getString("title");
                    String description = document.getString("description");
                    double latitude = document.getDouble("latitude");
                    double longitude = document.getDouble("longitude");
                    markerItems.add(new MarkerItem(title, description, latitude, longitude));
                }
                adapter = new MarkerAdapter(markerItems);
                recyclerView.setAdapter(adapter);
            } else {
                Log.e("MarkerListActivity", "Error fetching markers", task.getException());
            }
        });
    }

    private void showCreateMarkerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_create_marker, null);
        builder.setView(dialogView);

        EditText titleInput = dialogView.findViewById(R.id.titleInput);
        EditText descriptionInput = dialogView.findViewById(R.id.descriptionInput);
        EditText latitudeInput = dialogView.findViewById(R.id.latitudeInput);
        EditText longitudeInput = dialogView.findViewById(R.id.longitudeInput);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String title = titleInput.getText().toString();
            String description = descriptionInput.getText().toString();
            double latitude = Double.parseDouble(latitudeInput.getText().toString());
            double longitude = Double.parseDouble(longitudeInput.getText().toString());
            addMarkerToFirestore(title, description, latitude, longitude);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addMarkerToFirestore(String title, String description, double latitude, double longitude) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        MarkerItem newMarker = new MarkerItem(title, description, latitude, longitude);
        db.collection("cleanUpLocations").add(newMarker)
                .addOnSuccessListener(documentReference -> {
                    // Handle success
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }


}