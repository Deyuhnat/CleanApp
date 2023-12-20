package com.example.cleanproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MarkerListActivity extends AppCompatActivity implements MarkerAdapter.MarkerAdapterListener, MarkerAdapter.MarkerDeleteListener {

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
        adapter.setMarkerAdapterListener(this);
        adapter.setMarkerDeleteListener(this);

        fetchDataFromFirestore();
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(MarkerListActivity.this, MapsActivity.class);
            startActivity(intent);
            finish();
        });

        Button createButton = findViewById(R.id.createButton);
        String userRole = getUserRole();

        if ("Cleaner Owners".equals(userRole)) {
            createButton.setVisibility(View.VISIBLE);
            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Add your logic for what happens when the button is clicked
                    showCreateMarkerDialog();
                }
            });
        } else {
            createButton.setVisibility(View.GONE);
        }

        ArrayList<String> joinUserIDs = getIntent().getStringArrayListExtra("joinUserIDs");
        if (joinUserIDs != null) {
        } else {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchDataFromFirestore();
    }

    private void fetchDataFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cleanUpLocations").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<MarkerItem> markerItems = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String documentId = document.getId();
                    String title = document.getString("title");
                    String description = document.getString("description");
                    double latitude = document.getDouble("latitude");
                    double longitude = document.getDouble("longitude");
                    List<String> joinUserIDs = (List<String>) document.get("joinuserID");

                    MarkerItem marker = new MarkerItem(title, description, latitude, longitude, joinUserIDs, documentId);
                    marker.setDocumentId(documentId);
                    marker.setJoinuserID(joinUserIDs); // Set joinUserIDs
                    markerItems.add(marker);
                }
                adapter = new MarkerAdapter(markerItems);
                adapter.setMarkerAdapterListener(MarkerListActivity.this);
                adapter.setMarkerDeleteListener(this);
                recyclerView.setAdapter(adapter);
            } else {
                Log.e("MarkerListActivity", "Error fetching markers", task.getException());
            }
        });
    }

    @Override
    public void onNavigateButtonClicked(MarkerItem markerItem) {
        try {
            Intent intent = new Intent(MarkerListActivity.this, MarkerDetailActivity.class);
            intent.putStringArrayListExtra("joinUserIDs", new ArrayList<>(markerItem.getJoinuserID()));
            startActivity(intent);
        } catch (Exception e) {
            Log.e("MarkerListActivity", "Error navigating to MarkerDetailActivity", e);
        }
    }

    @Override
    public void onDeleteButtonClicked(MarkerItem markerItem) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String documentId = markerItem.getDocumentId();

        db.collection("cleanUpLocations").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Marker deleted", Toast.LENGTH_SHORT).show();
                    fetchDataFromFirestore();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error deleting marker", Toast.LENGTH_SHORT).show());
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
        MarkerItem newMarker = new MarkerItem(title, description, latitude, longitude, new ArrayList<>(), "");
        db.collection("cleanUpLocations").add(newMarker)
                .addOnSuccessListener(documentReference -> {
                    newMarker.setDocumentId(documentReference.getId());
                    Log.d("MarkerListActivity", "Marker successfully added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("MarkerListActivity", "Error adding marker", e);
                });
    }

    private String getUserRole() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("UserRole", "defaultRole");
    }
}