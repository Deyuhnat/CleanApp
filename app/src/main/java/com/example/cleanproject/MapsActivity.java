package com.example.cleanproject;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private String userRole;

    private BitmapDescriptor customIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userRole = getIntent().getStringExtra("userRole");

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        Button backButton = (Button) findViewById(R.id.backButton);
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Finish the current activity
//                finish();
//            }
//        });

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
            setupMapForCleanerOwner();
        } else if ("Volunteer".equals(userRole)) {
            setupMapForVolunteer();
        }

        //Center the map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(10.779783, 106.696806)));

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

        LatLng notreDame = new LatLng(10.779783, 106.696806);
        mMap.addMarker(new MarkerOptions()
                .position(notreDame)
                .title("Notre-Dame Cathedral Cleanup")
                .snippet("Join us to clean this historic landmark!")
                .icon(customIcon));

        LatLng benThanh = new LatLng(10.772089, 106.698404);
        mMap.addMarker(new MarkerOptions()
                .position(benThanh)
                .title("Ben Thanh Market Cleanup Site")
                .icon(customIcon));

        LatLng warRemnants = new LatLng(10.779785, 106.692209);
        mMap.addMarker(new MarkerOptions()
                .position(warRemnants)
                .title("War Remnants Museum Cleanup Site")
                .icon(customIcon));

        LatLng independencePalace = new LatLng(10.777321, 106.695804);
        mMap.addMarker(new MarkerOptions()
                .position(independencePalace)
                .title("Independence Palace Cleanup Site")
                .icon(customIcon));

        LatLng fineArtsMuseum = new LatLng(10.764888, 106.698279);
        mMap.addMarker(new MarkerOptions()
                .position(fineArtsMuseum)
                .title("Ho Chi Minh City Museum of Fine Arts Cleanup Site")
                .icon(customIcon));

        // Custom Info Window Adapter for detailed information

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
                // This method is not used in this example
                return null;
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
                    mMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(title)
                            .snippet(description)
                            .icon(customIcon)); // Set custom icon here for each marker
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
        mMap.setOnMapLongClickListener(latLng -> {
            showAddCleanupSiteDialog(latLng);
        });
    }

    private void setupMapForVolunteer() {
        mMap.setOnInfoWindowClickListener(marker -> {
            showCleanupSiteDetails(marker);
        });
    }

    private void showAddCleanupSiteDialog(LatLng latLng) {
        String siteName = "New Site Name"; // Replace with actual input from dialog
        String siteDescription = "Site Description"; // Replace with actual input

        BitmapDescriptor customIcon = BitmapDescriptorFactory.fromResource(R.drawable.blue_icon);
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(siteName)
                .snippet(siteDescription)
                .icon(customIcon));

        // Save the new site information to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> newSite = new HashMap<>();
        newSite.put("latitude", latLng.latitude);
        newSite.put("longitude", latLng.longitude);
        newSite.put("title", siteName);
        newSite.put("description", siteDescription);

        db.collection("cleanUpLocations").add(newSite)
                .addOnSuccessListener(documentReference -> Log.d("MapsActivity", "Site added to Firestore"))
                .addOnFailureListener(e -> Log.w("MapsActivity", "Error adding site to Firestore", e));
    }

    private void showCleanupSiteDetails(Marker marker) {
        Intent intent = new Intent(MapsActivity.this, SiteDetailActivity.class);
        intent.putExtra("Title", marker.getTitle());
        intent.putExtra("Description", marker.getSnippet());
        startActivity(intent);
    }

}