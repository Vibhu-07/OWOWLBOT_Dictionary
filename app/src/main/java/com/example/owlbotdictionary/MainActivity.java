/*
 * Author: VIBHU SIKKA
 * Student no: 041059069
 * Android final assignment
 * */

/**
 * This activity includes a search bar and retrieves the definition of the word from API
 * It also includes, each and every UI components as per instructions
 * @author Vibhu Sikka
 */

package com.example.owlbotdictionary;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This activity includes a search bar and retrieves the definition of the word from API
 * It also includes, each and every UI components as per instructions
 * @author Vibhu Sikka
 */
public class MainActivity extends AppCompatActivity {

    // Define variables for UI elements
    private EditText searchEditText;
    private Button searchButton;
    private ProgressBar progressBar;
    private TextView authorTextView;
    private Button page1Button;
    private Button page2Button;
    private Button page3Button;
    private ImageButton info;

    private TextView history;

    // Define variables for search results and adapter
    private List<String> searchList;

    // Define variables for shared preferences
    private SharedPreferences SharedPreferences;
    private static final String SHARED_PREFERENCES_KEY = "SearchSharedPreferences";
    private static final String SEARCH_RESULTS_KEY = "SearchResults";

    /**
     * Method to show progress bar
     */
    private void showProgressBar() { progressBar.setVisibility(View.VISIBLE); }

    /**
     * Method to show hide bar
     */
    private void hideProgressBar() { progressBar.setVisibility(View.GONE); }

    /**
     *  Method to show Alert Dialog Box
     */
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("HELP");
        builder.setMessage(R.string.alert);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Method to load search results
     */
    private void loadSearchResults() {
        history.setText(SharedPreferences.getString(SEARCH_RESULTS_KEY,""));
    }

    /**
     * Other way to make the API request for the word searched
     * @param word
     */
    public void queryData(final String word){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "https://owlbot.info/api/v2/dictionary/" + word +"?format=json";

        JsonArrayRequest jsonarrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(response.length()==0){
                            Toast.makeText(MainActivity.this, "Enter Something", Toast.LENGTH_SHORT).show();
                        }

                        for( int i=0 ; i<response.length();i++){
                            String meaningOfWord = null;
                            try {
                                JSONObject currentJsonObj = response.getJSONObject(i);
                                meaningOfWord = currentJsonObj.getString("definition"); //Get meaning from REST response
                                searchEditText.setText(meaningOfWord);
                                Log.e("myapp",meaningOfWord); //Debug
                                Log.e("my", response.toString());//Debug

                            } catch (JSONException e) {
                                Toast.makeText(MainActivity.this, "WRONG", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "WRONG", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonarrayRequest);
    }


    /**
     * To make the API request for the word searched
     * @param word
     */
    public void makeApiRequest(String word) {
        // Build the API request URL with the user's input and API Key
        String url = "https://owlbot.info/api/v4/dictionary/" + word;
        String apiKey = "1d364663b8263daf221675b422bed40e3c820f6b";

        // Volley request queue
        RequestQueue queue = Volley.newRequestQueue(this);

        // Volley string request object
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    // Parse the JSON response and extract the definition
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String definition = jsonObject.getString("pronunciation");
                        Log.e("myapp", definition);

                        // Update the UI with the definition
                        searchEditText.setText(definition);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Something went wrong !!", Toast.LENGTH_SHORT).show();
                        Log.e("myapp", "Something went wrong !!");
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(MainActivity.this, "Something went wrong !!", Toast.LENGTH_SHORT).show();
                    Log.e("myapp", "Something went wrong !!");
                }
        ) {
            // Add the API key to the request headers
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Token " + apiKey);
                return headers;
            }
        };

        // Add the request to the queue
        queue.add(request);
    }

    /**
     * OnCreate method that prepares the basis UI components on start
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        searchEditText = findViewById(R.id.search_edit_text);
        searchButton = findViewById(R.id.search_button);
        progressBar = findViewById(R.id.progress_bar);
        authorTextView = findViewById(R.id.author);
        page1Button = findViewById(R.id.Pg1);
        page2Button = findViewById(R.id.Pg2);
        page3Button = findViewById(R.id.Pg3);
        info = findViewById(R.id.info);
        history = findViewById(R.id.history);

        // Initialize search results list
        searchList = new ArrayList<>();

        // Initialize shared preferences
        SharedPreferences = getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);

        // Load search results from shared preferences
        loadSearchResults();

        // Set click listeners for search button and page buttons
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the search term
                String word = searchEditText.getText().toString();
                // When search button is clicked
                Search(word);
                // Create a snackbar
                Snackbar snackbar = Snackbar.make(v, "Search Button Clicked!", Snackbar.LENGTH_SHORT);
                // Action when snackbar is clicked
                snackbar.setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
                // Show the Snackbar
                snackbar.show();
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }
        });

        page1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When page 1 button is clicked
                Toast.makeText(MainActivity.this, "page1", Toast.LENGTH_SHORT).show();
//                Intent goToProfile = new Intent(VibhuActivity.this, SearchResultsActivity.class);
//                startActivity(goToProfile);
            }
        });

        page2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When page 2 button is clicked
                Toast.makeText(MainActivity.this, "page2", Toast.LENGTH_SHORT).show();
//                Intent goToProfile = new Intent(VibhuActivity.this, NamanActivity.class);
//                startActivity(goToProfile);
            }
        });

        page3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When page 3 button is clicked
                Toast.makeText(MainActivity.this, "page3", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Method that works when search btn is clicked
     * It calls API request method
     * And Prepares the other things that go through while searching
     * @param word
     */
    // Method called for Searching work
    private void Search(String word) {
        // Hide the keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);

        // Add the search term to the beginning of the search history list
        if (!searchList.contains(word)) {
            searchList.add(0, word);

            // Trim the search history list to the maximum size
            while (searchList.size() > 5) {
                searchList.remove(searchList.size() - 1);
            }
        }

        // Save the updated search history to shared preferences
        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE).edit();
        editor.putString(SEARCH_RESULTS_KEY, TextUtils.join(",", searchList));
        editor.apply();
        history.setText(SharedPreferences.getString(SEARCH_RESULTS_KEY,""));

        // Check if the search term is empty
        if (!TextUtils.isEmpty(word)) {
            // If search term is not empty
            // Show the progress bar
            showProgressBar();
            // Simulate search operation by delaying for 3 seconds
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Hide progress bar
                    hideProgressBar();
                }
            }, 2000); // Delay for 2 seconds

            // Go to another layout to get the API response for meaning
            Intent goToProfile = new Intent(MainActivity.this, MeaningActivity.class);
            goToProfile.putExtra("WORD", searchEditText.getText().toString());
            startActivity(goToProfile);
        } else {
            // If search term is empty
            Toast.makeText(MainActivity.this, "Please enter a word", Toast.LENGTH_SHORT).show();
        }
    }// end search
}
