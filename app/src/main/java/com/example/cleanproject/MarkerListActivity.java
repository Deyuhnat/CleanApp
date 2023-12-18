package com.example.cleanproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import android.widget.Button;

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

        fetchDataFromFirestore();
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Navigate back to MapsActivity
            Intent intent = new Intent(MarkerListActivity.this, MapsActivity.class);
            startActivity(intent);
            finish();  // Close this activity
        });
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
                // Handle the error
            }
        });
    }
}