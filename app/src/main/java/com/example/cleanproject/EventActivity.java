package com.example.cleanproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EventActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ListView listView;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        mAuth = FirebaseAuth.getInstance();
        listView = findViewById(R.id.listViewEvents);
        String currentUserId = mAuth.getCurrentUser().getUid();

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Navigate back to MainActivity
            Intent intent = new Intent(EventActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Finish EventActivity
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cleanUpLocations")
                .whereArrayContains("joinuserID", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> eventTitles = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            if (title != null) {
                                eventTitles.add(title);
                            }
                        }
                        if (eventTitles.isEmpty()) {
                            Toast.makeText(EventActivity.this, "No events found", Toast.LENGTH_SHORT).show();
                        } else {
                            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventTitles);
                            listView.setAdapter(adapter);
                        }
                    } else {
                        Toast.makeText(EventActivity.this, "Error loading events", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}