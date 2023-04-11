/*
 * Author: VIBHU SIKKA
 * Student no: 041059069
 * Android final assignment
 * Class to send API Request and recieve the meaning of the word
 * */

package com.example.owlbotdictionary;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MeaningActivity extends AppCompatActivity {
    private static final String API_KEY = "1d364663b8263daf221675b422bed40e3c820f6b";
    private static String API_URL;
    private String word;
    private TextView wordTextView;
    private TextView definitionTextView;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meaning_layout);
        Intent fromMain = getIntent();
        word = fromMain.getStringExtra("WORD");
        wordTextView = findViewById(R.id.wordTextView);
        back = findViewById(R.id.back);
        definitionTextView = findViewById(R.id.definitionTextView);
        wordTextView.setText(word);
        API_URL = "https://owlbot.info/api/v4/dictionary/" + word;

        // click listener for back button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToProfile = new Intent(MeaningActivity.this, MainActivity.class);
                startActivity(goToProfile);
            }
        });

        // call the AsyncTask to fetch the word of the day
        new GetWordOfTheDayTask().execute();
    }

    private class GetWordOfTheDayTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(API_URL + "?random=true"); // add "?random=true" to API URL to get a random word
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", "Token " + API_KEY);

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                connection.disconnect();

                return response.toString();
            } catch (IOException e) {
                Log.e("GetWordOfTheDayTask", "IOException", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (!jsonObject.has("word")) {
                        // If the JSON response does not contain a "word" property, log an error and show a Toast message
                        Log.e("GetWordOfTheDayTask", "API response did not contain a word");
                        Toast.makeText(MeaningActivity.this, "Failed to get word", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONArray definitionsArray = jsonObject.getJSONArray("definitions");

                    if (definitionsArray.length() == 0) {
                        // If the JSON response contains an empty array of definitions, log an error and show a Toast message
                        Log.e("GetWordOfTheDayTask", "API response did not contain any definitions");
                        Toast.makeText(MeaningActivity.this, "Failed to get word", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String word = jsonObject.getString("word");
                    String definition = definitionsArray.getJSONObject(0).getString("definition");

                    wordTextView.setText(word);
                    definitionTextView.setText(definition);
                } catch (JSONException e) {
                    // If an error occurs while parsing the JSON response, log an error and show a Toast message
                    Log.e("GetWordOfTheDayTask", "Error parsing JSON response", e);
                    Toast.makeText(MeaningActivity.this, "Failed to get word", Toast.LENGTH_SHORT).show();
                }
            } else {
                // If the API response is null, log an error and show a Toast message
                Log.e("GetWordOfTheDayTask", "API response was null");
                Toast.makeText(MeaningActivity.this, "Failed to get word", Toast.LENGTH_SHORT).show();
                return;
            }
        }

    }
}