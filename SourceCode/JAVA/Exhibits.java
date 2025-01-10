package com.example.culturalcompass;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;

public class Exhibits extends AppCompatActivity{
    private EditText edd1,edd2,edd4,edd5;
    private Button btnnn;

    @SuppressLint("MissingInflatedId")
    @Override

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibits);


        edd1 = findViewById(R.id.info);
        edd2= findViewById(R.id.namee);
        edd4=findViewById(R.id.latitudee);
        edd5=findViewById(R.id.longitudee);
        btnnn=findViewById(R.id.sumbitt);

        Button goToImageActivity = findViewById(R.id.image);
        goToImageActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Κάνουμε τη μετάβαση στην δραστηριότητα MuseumsLocation
                Intent intent = new Intent(Exhibits.this, Image.class);
                startActivity(intent);}});

        Button goToLocationButton = findViewById(R.id.buttongotolocation);
        goToLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Κάνουμε τη μετάβαση στην δραστηριότητα MuseumsLocation
                Intent intent = new Intent(Exhibits.this, ExhibitsLocation.class);
                startActivity(intent);}});


        btnnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = edd1.getText().toString();
                String namee = edd2.getText().toString();
                String latitudee = edd4.getText().toString();
                String longitudee = edd5.getText().toString();

                if (info.equals(null)) {
                    edd1.setError("Please enter Country");
                    edd1.requestFocus();
                }

                if (namee.equals(null)) {
                    edd2.setError("Please enter Name");
                    edd2.requestFocus();
                }


                if (latitudee.equals(null)) {
                    edd4.setError("Please enter Latitude");
                    edd4.requestFocus();
                }

                if (longitudee.equals(null)) {
                    edd5.setError("Please enter Longitude");
                    edd5.requestFocus();
                }

                if (!info.equals(null)  && !namee.equals(null)  &&  !latitudee.equals(null)  &&  !longitudee.equals(null)) {
                    sumbitUsersToDatabase(info,namee,longitudee,latitudee);
                }
            }
        });
    }

    private void sumbitUsersToDatabase(final String info, final String name, final String longitude, final String latitude) {
        String url ="http://192.168.139.1/CulturalCompass/add_data2.php";


        RequestQueue requestQueue = Volley.newRequestQueue(this);


        StringRequest stringRequest= new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response",response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error",""+error);

            }
        }) {
            protected HashMap<String,String> getParams() throws AuthFailureError{
                HashMap<String,String> map = new HashMap<>();
                map.put("name",name);
                map.put("info",info);
                map.put("latitude",latitude);
                map.put("longitude",longitude);
                return map;

            }
        };
        requestQueue.add(stringRequest);

    }

}