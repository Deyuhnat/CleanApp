package com.example.cleanproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button button;
    TextView emailTextView, nameTextView, phoneTextView, roleTextView;


    EditText editName, editPhone;
    Button editProfileButton;
    Button saveButton;
    FirebaseUser user;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        user = auth.getCurrentUser();

        button = findViewById(R.id.logout);
        emailTextView = findViewById(R.id.email);
        nameTextView = findViewById(R.id.name);
        roleTextView = findViewById(R.id.role);
        phoneTextView = findViewById(R.id.phone);
        user = auth.getCurrentUser();
        if(user == null){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
        if(user != null) {
            fetchUserProfile();
        }

        else {
            emailTextView.setText(user.getEmail());

        }

        editName = findViewById(R.id.editName);
        editPhone = findViewById(R.id.editPhone);
        editProfileButton = findViewById(R.id.editProfileButton);
        saveButton = findViewById(R.id.saveButton);

        //Map button
        Button mapButton = findViewById(R.id.button_map);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("UserRole", roleTextView.getText().toString());
                startActivity(intent);
            }
        });

        // Setup Edit Profile Button
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make EditTexts visible for editing
                editName.setVisibility(View.VISIBLE);
                editPhone.setVisibility(View.VISIBLE);

                // Make Save Button visible
                saveButton.setVisibility(View.VISIBLE);

                // Hide the display TextViews
                nameTextView.setVisibility(View.GONE);
                phoneTextView.setVisibility(View.GONE);

                // Optionally, populate EditTexts with current data
                editName.setText(nameTextView.getText().toString());
                editPhone.setText(phoneTextView.getText().toString());
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                editName.setVisibility(View.VISIBLE);
                editPhone.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fetch data from EditTexts
                String newName = editName.getText().toString();
                String newPhone = editPhone.getText().toString();

                // Firestore update logic
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (currentUser != null) {
                    db.collection("users").document(currentUser.getUid())
                            .update("name", newName, "phone", newPhone)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Update TextViews with new data
                                    nameTextView.setText("Name: " + newName);
                                    phoneTextView.setText("Phone: " + newPhone);

                                    // Hide EditTexts and show TextViews after update
                                    editName.setVisibility(View.GONE);
                                    editPhone.setVisibility(View.GONE);
                                    nameTextView.setVisibility(View.VISIBLE);
                                    phoneTextView.setVisibility(View.VISIBLE);

                                    Toast.makeText(MainActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

    }




    private void fetchUserProfile() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            User userProfile = documentSnapshot.toObject(User.class);
                            // Update TextViews with user data
                            if(userProfile != null) {
                                emailTextView.setText(userProfile.getEmail());
                                nameTextView.setText(userProfile.getName());
                                roleTextView.setText(userProfile.getRole());
                                phoneTextView.setText(userProfile.getPhone());
                            }
                        } else {
                            // Handle case where user data does not exist
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error
                    }
                });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }
}