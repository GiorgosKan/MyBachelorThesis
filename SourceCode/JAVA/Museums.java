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

public class Museums extends AppCompatActivity{
private EditText ed1,ed2,ed3,ed4,ed5;
private Button btn;

@SuppressLint("MissingInflatedId")
@Override

    protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_museums);


            ed1 = findViewById(R.id.country);
            ed2= findViewById(R.id.name);
            ed3= findViewById(R.id.city);
            ed4=findViewById(R.id.latitude);
            ed5=findViewById(R.id.longitude);
            btn=findViewById(R.id.sumbit);

    Button goToExhibitsButton = findViewById(R.id.gotoexhibits);
    goToExhibitsButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Κάνουμε τη μετάβαση στην δραστηριότητα Exhibits
            Intent intentt = new Intent(Museums.this, Exhibits.class);
            startActivity(intentt);
        }
    });

    Button goToLocationButton = findViewById(R.id.button_go_to_location);
    goToLocationButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Κάνουμε τη μετάβαση στην δραστηριότητα MuseumsLocation
            Intent intent = new Intent(Museums.this, MuseumsLocation.class);
            startActivity(intent);}});


            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String country = ed1.getText().toString();
                    String name = ed2.getText().toString();
                    String city = ed3.getText().toString();
                    String latitude = ed4.getText().toString();
                    String longitude = ed5.getText().toString();

                    if (country.equals(null)) {
                        ed1.setError("Please enter Country");
                        ed1.requestFocus();
                    }

                    if (name.equals(null)) {
                        ed2.setError("Please enter Name");
                        ed2.requestFocus();
                    }
                    if (city.equals(null)) {
                        ed3.setError("Please enter City");
                        ed3.requestFocus();
                    }

                    if (latitude.equals(null)) {
                        ed4.setError("Please enter Latitude");
                        ed4.requestFocus();
                    }

                    if (longitude.equals(null)) {
                        ed5.setError("Please enter Longitude");
                        ed5.requestFocus();
                    }

                    if (!country.equals(null)  && !name.equals(null)  && !city.equals(null) && !latitude.equals(null)  &&  !longitude.equals(null)) {
                        sumbitUsersToDatabase(country,name,city,longitude,latitude);
                    }
                }
            });
}

    private void sumbitUsersToDatabase(final String country, final String name, final String city, final String longitude, final String latitude) {
    String url ="http://192.168.139.1/CulturalCompass/add_data.php";


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
                map.put("city",city);
                map.put("country",country);
                map.put("latitude",latitude);
                map.put("longitude",longitude);
                return map;

            }
        };
        requestQueue.add(stringRequest);

    }

}