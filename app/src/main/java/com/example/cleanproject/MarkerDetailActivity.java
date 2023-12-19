package com.example.cleanproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.app.AlertDialog;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;


public class MarkerDetailActivity extends AppCompatActivity {

    private ListView joinUserListView;
    private ArrayAdapter<String> adapter;
    private List<String> userNames = new ArrayList<>();
    private Map<String, User> userMap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_detail);

        joinUserListView = findViewById(R.id.joinuser_list_view);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userNames);
        joinUserListView.setAdapter(adapter);

        ArrayList<String> joinUserIDs = getIntent().getStringArrayListExtra("joinUserIDs");
        if (joinUserIDs != null) {
            fetchUserNames(joinUserIDs);
        }

        joinUserListView.setOnItemClickListener((parent, view, position, id) -> {
            String userName = adapter.getItem(position);
            Log.d("MarkerDetailActivity", "List item clicked: " + userName);
            User user = userMap.get(userName);
            if (user != null) {
                showUserDetails(user);
            } else {
                Log.d("MarkerDetailActivity", "User not found in map: " + userName);
            }
        });

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(MarkerDetailActivity.this, MarkerListActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void fetchUserDetails(List<String> joinUserIDs) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for (String userID : joinUserIDs) {
            db.collection("users").document(userID).get().addOnSuccessListener(documentSnapshot -> {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    String userName = user.getName();
                    userMap.put(userName, user);
                    adapter.add(userName);
                    adapter.notifyDataSetChanged();
                    Log.d("MarkerDetailActivity", "User added: " + userName);
                }
            }).addOnFailureListener(e -> {
                Log.e("MarkerDetailActivity", "Error fetching user details", e);
            });
        }
    }

    private void showUserDetails(User user) {
        new AlertDialog.Builder(this)
                .setTitle(user.getName())
                .setMessage("Email: " + user.getEmail() + "\nPhone: " + user.getPhone() + "\nRole: " + user.getRole())
                .setPositiveButton("OK", null)
                .show();
    }

    private void fetchUserNames(List<String> joinUserIDs) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for (String userID : joinUserIDs) {
            db.collection("users").document(userID).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String userName = documentSnapshot.getString("name"); // Assuming the field for name is 'name'
                    Log.d("MarkerDetailActivity", "Retrieved user name: " + userName);
                    if (userName != null) {
                        userNames.add(userName);
                        adapter.notifyDataSetChanged();
                    }
                }
            }).addOnFailureListener(e -> {
                Log.e("MarkerDetailActivity", "Error fetching user details", e);
            });
        }
    }
}