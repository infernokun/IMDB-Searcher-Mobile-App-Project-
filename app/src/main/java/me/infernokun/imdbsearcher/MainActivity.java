package me.infernokun.imdbsearcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
{
    private EditText mMovieEditText;
    private Button mSubmitButton;
    private TextView mTestTitle;

    private LinearLayout movieTable;

    public static String mainURL = "https://www.omdbapi.com/?apikey=883fc37e";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMovieEditText = findViewById(R.id.movieEditText);
        mSubmitButton = findViewById(R.id.submitButton);
        movieTable = findViewById(R.id.movieTable);
        mTestTitle = findViewById(R.id.test_title);

        mMovieEditText.setHint("Enter Movie Title Here");

        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        mSubmitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, getUserInput(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                executeSearch(response, getUserInput());
                                System.out.println("Working");
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        System.out.println("Main Error: " + error);
                    }
                });
                queue.add(stringRequest);
            }
        });
    }

    public void executeSearch(String response, String text) {
        try
        {
            Intent intent = new Intent(MainActivity.this ,SearchResults.class);

            // converts response to JSONObject
            JSONObject o = new JSONObject(response);

            // The array starts at Search
            JSONArray search = o.getJSONArray("Search");

            // Array of TextViews for the titles
            TextView[] movieText = new TextView[search.length()];

            // create hashmap of
            ArrayList<HashMap<Integer, String>> movieList = new ArrayList<>();

            // Remove TextViews from screen after each search
            movieTable.removeAllViews();


            // for loop to add text views and dada
            for (int i = 0; i < search.length(); i++) {

                // get each array element as a json obj
                JSONObject movie = (JSONObject) search.get(i);
                String n = movie.getString("Title");

                // create a hashmap of the int and title
                HashMap<Integer, String> thisMovie = new HashMap<>();
                thisMovie.put(i, n);
                movieList.add(thisMovie);

                // make new textview for the movie title
                movieText[i] = new TextView(MainActivity.this);

                movieText[i].setText(n);
                movieText[i].setTextSize(20);

            }

            // add the arraylist too the next activity
            intent.putExtra("MovieTitles", movieList);
            intent.putExtra("Search", text);

            // start new activity
            startActivity(intent);
        }catch (Exception ex) {
            Toast.makeText(MainActivity.this, "None in database", Toast.LENGTH_SHORT).show();
        }
    }

    public String getUserInput() {

        String text = mMovieEditText.getText().toString();

        // multiple results
        final String url2 = mainURL + "&s=" + text;

        return url2;
    }

}