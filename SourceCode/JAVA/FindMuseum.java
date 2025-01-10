package com.example.culturalcompass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.culturalcompass.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class FindMuseum extends AppCompatActivity {

    private FusedLocationProviderClient sFusedLocationClient;

    private LocationCallback sLocationCallback;
    private LocationRequest sLocationRequest;
    private GoogleApiClient sGoogleApiClient;
    private boolean apiconnectionstatus = false;
    private long interval = 10000;
    private long fastestInterval = 5000;
    private int priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
    private int numberOfUpdates;
    private double Latitude = 0.0, Longitude = 0.0;
    private static final String TAG = "ExhibitsLocation";
    Button metagetGPS;
    ProgressBar progressBar;
    Button scan;
    TextInputEditText longitude, latitude;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findmuseum);

        metagetGPS = findViewById(R.id.metagetGPS);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        scan = findViewById(R.id.scan);
        progressBar = findViewById(R.id.progress);

        metagetGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (apiconnectionstatus) {
                    locationSettingsRequest();
                }
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String latitudeStr, longitudeStr;
                latitudeStr = String.valueOf(latitude.getText());
                longitudeStr = String.valueOf(longitude.getText());

                if (!latitudeStr.equals("") && !longitudeStr.equals("")) {
                    progressBar.setVisibility(View.VISIBLE);

                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String[] field = new String[2];
                            field[0] = "Latitude";
                            field[1] = "Longitude";
                            String[] data = new String[2];
                            data[0] = latitudeStr;
                            data[1] = longitudeStr;

                            PutData putData = new PutData("http://192.168.139.1/CulturalCompass/findmuseum.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    progressBar.setVisibility(View.GONE);
                                    String result = putData.getResult();
                                    if (result.startsWith("Museum Found")) {
                                        String[] parts = result.split(";");
                                        String name = parts[1];
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), FindExhibit.class);
                                        intent.putExtra("name", name);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "All fields required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Function to connect googleapiclient
     */
    private void connectGoogleClient() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(this);
        if (resultCode == ConnectionResult.SUCCESS) {
            sGoogleApiClient.connect();
        } else {
            int REQUEST_GOOGLE_PLAY_SERVICE = 988;
            googleAPI.getErrorDialog(this, resultCode, REQUEST_GOOGLE_PLAY_SERVICE);
        }
    }

    /**
     * Function to start FusedLocation updates
     */
    public void requestLocationUpdate() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            latitude.setText(getString(R.string.loading));
            longitude.setText(getString(R.string.loading));
            sFusedLocationClient.requestLocationUpdates(sLocationRequest, sLocationCallback, Looper.myLooper());
        }
    }

    /**
     * Build GoogleApiClient and connect
     */
    private synchronized void buildGoogleApiClient() {
        sFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        sGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                        // Creating a location request
                        sLocationRequest = new LocationRequest();
                        sLocationRequest.setPriority(priority);
                        sLocationRequest.setSmallestDisplacement(0);
                        sLocationRequest.setNumUpdates(1);

                        // FusedLocation callback
                        sLocationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(final LocationResult locationResult) {
                                super.onLocationResult(locationResult);

                                Latitude = locationResult.getLastLocation().getLatitude();
                                Longitude = locationResult.getLastLocation().getLongitude();

                                if (Latitude == 0.0 && Longitude == 0.0) {
                                    requestLocationUpdate();
                                } else {
                                    // Update Textview
                                    latitude.setText(Double.toString(Latitude));
                                    longitude.setText(Double.toString(Longitude));
                                }
                            }
                        };

                        // Simple api status check
                        apiconnectionstatus = true;
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        connectGoogleClient();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(LocationServices.API)
                .build();

        // Connect googleapiclient after build
        connectGoogleClient();
    }

    /**
     * Function to request Location permission and enable GPS Dialog
     */
    private void locationSettingsRequest() {
        SettingsClient mSettingsClient = LocationServices.getSettingsClient(this);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(sLocationRequest);
        builder.setAlwaysShow(true);
        LocationSettingsRequest mLocationSettingsRequest = builder.build();

        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(locationSettingsResponse -> {
                    // Start FusedLocation if GPS is enabled
                    requestLocationUpdate();
                })
                .addOnFailureListener(e -> {
                    // Show enable GPS Dialog and handle dialog buttons
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                int REQUEST_CHECK_SETTINGS = 214;
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(FindMuseum.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sie) {
                                showLog("Unable to Execute Request");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            showLog("Location Settings are Inadequate, and Cannot be fixed here. Fix in Settings");
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        showLog("Canceled No Thanks");
                    }
                });
    }

    private void showLog(String message) {
        Log.e(TAG, "" + message);
    }

    @Override
    public void onResume() {
        super.onResume();
        buildGoogleApiClient();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sFusedLocationClient.removeLocationUpdates(sLocationCallback);
    }

    // Handle results of enable GPS Dialog
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 214) {
            switch (resultCode) {
                case Activity.RESULT_OK: {
                    // User enabled GPS start fusedlocation
                    requestLocationUpdate();
                    break;
                }
                case Activity.RESULT_CANCELED: {
                    // The user was asked to change settings, but chose not to
                    Toast.makeText(getApplication(), "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }
}


