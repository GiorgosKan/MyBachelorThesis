package com.example.culturalcompass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FindExhibit extends AppCompatActivity {

    private TextView welcomeTextView;
    private Button scanButton;
    private static final String CHANNEL_ID = "museum_channel_id";
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findexhibit);

        welcomeTextView = findViewById(R.id.welcomeTextView);
        scanButton = findViewById(R.id.scanButton);


        String name = getIntent().getStringExtra("name");
        welcomeTextView.setText("Welcome to the " + name);

        createNotificationChannel();
        sendNotification(name);

        scanButton.setOnClickListener(view -> new IntentIntegrator(FindExhibit.this).initiateScan());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                // Λήψη του ID από το QR code και αποστολή του στον server
                String exhibitId = result.getContents();
                Log.d("FindExhibit", "Scanned QR code with ID: " + exhibitId);
                fetchExhibitDetails(exhibitId);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void fetchExhibitDetails(String exhibitId) {
        String url = "http://192.168.139.1/CulturalCompass/scanexhibits.php?exhibitId=" + exhibitId; // Αλλαγή εδώ για χρήση GET

        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("FindExhibit", "Server response: " + response); // Καταγραφή της απάντησης του server
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            // Επεξεργασία των δεδομένων
                            if (jsonResponse.has("error")) {
                                String errorMessage = jsonResponse.getString("error");
                                Toast.makeText(FindExhibit.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                                Log.e("FindExhibit", "Server error: " + errorMessage);
                            } else {
                                String exhibitName = jsonResponse.getString("name");
                                String exhibitInfo = jsonResponse.getString("information");
                                String exhibitImagePath = jsonResponse.getString("path");

                                Log.d("FindExhibit", "Exhibit details fetched: " + exhibitName + ", " + exhibitInfo + ", " + exhibitImagePath);

                                Intent intent = new Intent(FindExhibit.this, YourExhibit.class);
                                // Προσθήκη των δεδομένων στο Intent
                                intent.putExtra("exhibitName", exhibitName);
                                intent.putExtra("exhibitInfo", exhibitInfo);
                                intent.putExtra("exhibitImagePath", exhibitImagePath); // Αλλαγή εδώ για την πλήρη διαδρομή της εικόνας
                                // Εκκίνηση της επόμενης δραστηριότητας
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("FindExhibit", "JSON parsing error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(FindExhibit.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("FindExhibit", "Volley error: " + error.getMessage());
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(getRequest);
    }



    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Museum Channel";
            String description = "Channel for museum notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(String museumName) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification) // βεβαιώσου ότι έχεις ένα εικονίδιο με αυτό το όνομα
                .setContentTitle("You are at a Museum")
                .setContentText("Welcome to the " + museumName)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Σε περίπτωση που δεν έχει δοθεί άδεια, επιστρέφουμε χωρίς να στείλουμε ειδοποίηση
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}