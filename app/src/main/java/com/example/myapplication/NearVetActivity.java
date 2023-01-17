package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Vet;
import com.example.myapplication.ui.VetAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

public class NearVetActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private final List<Vet> vetList = new ArrayList<>();
    private VetAdapter adapter;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    public ActivityResultLauncher<String[]> locationPermissionRequest;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_vet);
        String API_KEY = getString(R.string.API_KEY);
        Places.initialize(getApplicationContext(), API_KEY);
        PlacesClient placesClient = Places.createClient(this);
        RecyclerView recyclerView = findViewById(R.id.recyckerview_vet);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VetAdapter(vetList, this);
        recyclerView.setAdapter(adapter);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        permissionRequestinit();
        requestpermission();
        getLastknownLocation();
    }

    public void permissionRequestinit() {
        locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                fineLocationGranted = result.getOrDefault(
                                        Manifest.permission.ACCESS_FINE_LOCATION, false);
                            }
                            Boolean coarseLocationGranted = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                coarseLocationGranted = result.getOrDefault(
                                        Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            }
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                                Toast.makeText(this, "Location Access : Granted", Toast.LENGTH_SHORT).show();
                                locationPermissionGranted = true;
                                getLastknownLocation();

                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                            } else {
                                // No location access granted.
                                Toast.makeText(NearVetActivity.this, "Location Access : Denied", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
    }

    public  void requestpermission()
    {
        if (ContextCompat.checkSelfPermission(NearVetActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {

            locationPermissionGranted = true;


        }
        else if (ActivityCompat.shouldShowRequestPermissionRationale(NearVetActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION))
        {
            permissionDeniedAlert();
        }
        else {
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    public void permissionDeniedAlert()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(NearVetActivity.this);

        dialog.setTitle("Requires Location Permission");
        dialog.setMessage("This app need your location permission to continue");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                locationPermissionRequest.launch(new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION});
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(NearVetActivity.this, "Sorry,the app wont run until permission is given. Consent is very important you know.", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    @SuppressLint("MissingPermission")
    public void getLastknownLocation()
    {
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    // Use the Places API to search for nearby places
                    searchNearbyPlaces(latitude,longitude);
                }
            }
        });

    }


    private void searchNearbyPlaces(double latitude, double longitude) {
        String apiKey = String.valueOf(R.string.API_KEY);
        PlacesClient placesClient = Places.createClient(this);
        String query = "veterinary clinic";

        final double HEADING_NORTH_EAST = 45;
        final double HEADING_SOUTH_WEST = 215;
        final double diagonalBoundsSize = 1000; // 1km

        LatLng centre = new LatLng(85, -180);

        LatLng northEast = SphericalUtil.computeOffset(centre, diagonalBoundsSize / 2, HEADING_NORTH_EAST);
        LatLng southWest = SphericalUtil.computeOffset(centre, diagonalBoundsSize / 2, HEADING_SOUTH_WEST);
        LatLngBounds bounds = new LatLngBounds(southWest, northEast);
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setLocationBias(RectangularBounds.newInstance(bounds))
                .setCountries("MY")
                .setSessionToken(token)
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                String placeId = prediction.getPlaceId();
                String name = prediction.getPrimaryText(null).toString();
                String address = prediction.getSecondaryText(null).toString();
                Log.i( "placeID", placeId);
                Log.i("name", name);
                //Add the places to your list
                Vet vet = new Vet(name, address, placeId);
                vetList.add(vet);
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Places API", "Failed to retrieve autocomplete predictions: " + e.getMessage());
            }
        });
    }


}