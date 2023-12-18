package com.example.cleanproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.SearchView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.cleanproject.databinding.ActivityMapsBinding;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirestoreKt;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private List<Marker> allMarkers = new ArrayList<>();
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private String userRole;

    private String cleanSiteId;

    private BitmapDescriptor customIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userRole = getIntent().getStringExtra("userRole");

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchCleanupSites(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterMarkersByDescription(newText);
                return false;
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
            } else if (id == R.id.list) {
                Intent intent = new Intent(MapsActivity.this, MarkerListActivity.class);
                startActivity(intent);
            } else if (id == R.id.profile) {
                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            return true;
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            userRole = documentSnapshot.getString("role");
            initializeMap();
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    private void initializeMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        customIcon = BitmapDescriptorFactory.fromResource(R.drawable.blue_icon);
        mMap = googleMap;
        setupCustomInfoWindow();
        fetchMarkers();

        // Fetch location data and check role
        if ("Clean Up Location Owners".equals(userRole)) {
            setupMapForVolunteer();
        } else if ("Volunteer".equals(userRole)) {
            setupMapForVolunteer();
        }

        //Center the map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(10.779783, 106.696806)));

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setPadding(0,0,0,250);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cleanUpLocations").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    double lat = document.getDouble("latitude");
                    double lng = document.getDouble("longitude");
                    String title = document.getString("title");
                    String description = document.getString("description");

                    LatLng location = new LatLng(lat, lng);
                    mMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(title)
                            .snippet(description)
                            .icon(customIcon));  // Adding the custom icon here
                }
            } else {
                Log.w("MapsActivity", "Error getting documents.", task.getException());
            }
        });

        // Custom Info Window Adapter for detailed information

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
                TextView title = infoWindow.findViewById(R.id.title);
                TextView snippet = infoWindow.findViewById(R.id.snippet);
                Button joinButton = infoWindow.findViewById(R.id.joinButton);

                title.setText(marker.getTitle());
                snippet.setText(marker.getSnippet());
//                joinButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        FirebaseFirestore db = FirebaseFirestore.getInstance();
//                        FirebaseAuth user = FirebaseAuth.getInstance();
//                        db.collection("cleanUpLocations").document("dRKVzUhCc5nKSocVnCvX").update("joinuserID", FieldValue.arrayUnion(user.getUid()));
//                    }
//                });
                infoWindow.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        FirebaseAuth user = FirebaseAuth.getInstance();
                        db.collection("cleanUpLocations").document("dRKVzUhCc5nKSocVnCvX").update("joinuserID", FieldValue.arrayUnion(user.getUid()));
                    }
                });

                cleanSiteId = (String) marker.getTag();
//                joinButton.setOnClickListener(v -> joinCleanupSite(cleanSiteId));
                return infoWindow;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // This method is not used in this example
                return null;
            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseAuth user = FirebaseAuth.getInstance();
                db.collection("cleanUpLocations").document("dRKVzUhCc5nKSocVnCvX").update("joinuserID", FieldValue.arrayUnion(user.getUid()));
            }
        });
    }

    private void fetchMarkers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cleanUpLocations").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                BitmapDescriptor customIcon = BitmapDescriptorFactory.fromResource(R.drawable.blue_icon);
                for (QueryDocumentSnapshot document : task.getResult()) {
                    double lat = document.getDouble("latitude");
                    double lng = document.getDouble("longitude");
                    String title = document.getString("title");
                    String description = document.getString("description");

                    LatLng location = new LatLng(lat, lng);
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title(title)
                            .snippet(description)
                            .icon(customIcon));
                    marker.setTag(document.getId());
                }
            } else {
                Log.w("MapsActivity", "Error getting documents.", task.getException());
            }
        });
    }

    private void setupCustomInfoWindow() {
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
                TextView title = infoWindow.findViewById(R.id.title);
                TextView snippet = infoWindow.findViewById(R.id.snippet);
                title.setText(marker.getTitle());
                snippet.setText(marker.getSnippet());
                return infoWindow;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
    }

    private void setupMapForCleanerOwner() {
//        mMap.setOnMapLongClickListener(latLng -> {
//            showAddCleanupSiteDialog(latLng);
//        });
    }

    private void setupMapForVolunteer() {
        mMap.setOnInfoWindowClickListener(marker -> {
            showCleanupSiteDetails(marker);
            if(marker != null)
              Log.d("marker reference", marker.getTag().toString());
        });
    }

    private void showAddCleanupSiteDialog(LatLng latLng) {
        String siteName = "New Site Name"; // Replace with actual input from dialog
        String siteDescription = "Site Description"; // Replace with actual input

    }

    private void showCleanupSiteDetails(Marker marker) {
        cleanSiteId = (String) marker.getTag();
        Log.d("textmarker", cleanSiteId);

    }

    private void filterMarkersByDescription(String text) {
        for (Marker marker : allMarkers) {
            if (marker.getSnippet().toLowerCase().contains(text.toLowerCase())) {
                marker.setVisible(true);
            } else {
                marker.setVisible(false);
            }
        }
    }

    private void joinCleanupSite(String cleanSiteId) {
        if (cleanSiteId != null && !cleanSiteId.isEmpty()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String userId = auth.getCurrentUser().getUid();

            db.collection("cleanUpLocations").document(cleanSiteId)
                    .update("joinuserID", FieldValue.arrayUnion(userId))
                    .addOnSuccessListener(aVoid -> Log.d("MapsActivity", "User successfully joined"))
                    .addOnFailureListener(e -> Log.w("MapsActivity", "Error joining user", e));
        }
    }

    private void searchCleanupSites(String query) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cleanUpLocations")
                .whereEqualTo("title", query)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            double lat = document.getDouble("latitude");
                            double lng = document.getDouble("longitude");
                            LatLng siteLocation = new LatLng(lat, lng);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(siteLocation, 13)); // Focus on searched site
                            break; // Remove this if you want to show all matching sites
                        }
                    } else {
                        Toast.makeText(MapsActivity.this, "No matching site found", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}