package com.moujib.controle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Employe> employes;
    private ImageView imageView;
    private TextView id,nom ,prenom;
    private EmployeAdapter employeAdapter;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
   recyclerView=findViewById(R.id.parent1);
   nom=findViewById(R.id.Nom);
   prenom=findViewById(R.id.prenom);
   id=findViewById(R.id.id1);
        loadImage();


    }


    public void loadImage() {

        RequestQueue queue = Volley.newRequestQueue(this);

        // Define the URL for the images API
        String url = "http://192.168.1.7:8086/api/employes";

        // Create a request to get the list of images
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Check if there are any images in the response

                            employes =new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject employeObject = response.getJSONObject(i);
                                Long id = employeObject.getLong("id");
                                String nom=employeObject.getString("nom");
                                String prenom=employeObject.getString("prenom");

                                // Assurez-vous que "content" est une chaîne encodée en Base64 dans la base de données
                                String base64ImageData = employeObject.getString("photo");

                                // Convertissez la chaîne Base64 en tableau de bytes
                                byte[] imageData = Base64.decode(base64ImageData, Base64.DEFAULT);

                                Employe employe = new Employe(id, nom,prenom,imageData);
                                employes.add(employe);
                            }



                            if (response.length() > 0) {
                                employeAdapter = new EmployeAdapter(MainActivity.this,employes);
                                recyclerView.setAdapter( employeAdapter );
                                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));


                            } else {
                                Toast.makeText(MainActivity.this, "No images found.", Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("Erreur", e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors here
                        Toast.makeText(MainActivity.this, "Error loading images: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the request to the RequestQueue
        queue.add(jsonArrayRequest);
    }
}