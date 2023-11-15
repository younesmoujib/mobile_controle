package com.moujib.controle;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AddEmploye extends AppCompatActivity {

    private EditText nom,prenom,dateNaissance ;
    private ImageView imageView;
    private Spinner spinner;
    private Bitmap selectedBitmap;
    private Button btnSelect ,btnEnvoyer;

    RequestQueue requestQueue;


    private List<Service> services;
    private ArrayList<String>  serviceList =new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employe);


        nom=findViewById(R.id.nom);
        prenom=findViewById(R.id.prenom1);
        dateNaissance=findViewById(R.id.date);
        spinner=findViewById(R.id.spinner1);
        imageView =findViewById(R.id.imageView);


        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, serviceList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        getSeriveceFromAPI();

        btnSelect=findViewById(R.id.btn2);
        btnEnvoyer=findViewById(R.id.btn);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhoto();
            }
        });

        btnEnvoyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Envoyerdata();
            }
        });


        // Vérifiez la permission en temps d'exécution pour les versions Android 6.0 et supérieures
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }



    }
    public void choosePhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,CONTEXT_INCLUDE_CODE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CONTEXT_INCLUDE_CODE && resultCode == RESULT_OK && data != null) {
            // L'utilisateur a choisi une image depuis la galerie
            imageView.setVisibility(View.VISIBLE);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                selectedBitmap = bitmap;
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    public void Envoyerdata() {
        if (selectedBitmap != null) {
            // Convert the bitmap to a byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            final String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            // Define the URL of your API endpoint
            String url = "http://192.168.1.7:8086/api/employes";

            // Create a JSON object with the image data
            JSONObject jsonParams = new JSONObject();
            try {
                jsonParams.put("nom", nom.getText().toString());
                jsonParams.put("photo", encodedImage);
                jsonParams.put("prenom",prenom.getText().toString());
                jsonParams.put("dateNaissance",dateNaissance.getText().toString());
                JSONObject ServiceObject = new JSONObject();
                Long selectedFiliereId = services.get(spinner.getSelectedItemPosition()).getId();
                ServiceObject.put("id",selectedFiliereId );


                jsonParams.put("specialite", ServiceObject);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Create the JsonObjectRequest
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonParams,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("ErreurVolley", response.toString());

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle errors

                            Log.d("ErreurVolley", error.toString());
                        }
                    });

            // Create a request queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            // Add the request to the queue
            requestQueue.add(request);
        } else {
            Toast.makeText(this, "Please choose an image first", Toast.LENGTH_SHORT).show();
        }
    }

    // Ajoutez la méthode onRequestPermissionsResult si vous demandez la permission en temps d'exécution
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // La permission a été accordée
        } else {
            // La permission a été refusée
            Toast.makeText(this, "Permission refusée pour accéder au stockage externe", Toast.LENGTH_SHORT).show();
        }
    }
    private void getSeriveceFromAPI() {
        String Url = "http://192.168.1.7:8086/api/services";

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            services = new ArrayList<>(); // Initialiser la liste de filières

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject filiereObject = response.getJSONObject(i);
                                Long id = filiereObject.getLong("id");
                                String nom = filiereObject.getString("nom");
                                Service filiere = new Service(id, nom);
                                services.add(filiere);
                                serviceList.add(nom);
                            }

                            spinnerAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Erreur", error.toString());
                    }
                });

        requestQueue.add(request);
    }





}