package me.infernokun.imdbsearcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchResults extends AppCompatActivity {

    private EditText mMovieEditText;
    private Button mSubmitButton, mBackButton;

    private LinearLayout movieTable;

    String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        mMovieEditText = findViewById(R.id.movieEditText);
        mSubmitButton = findViewById(R.id.submitButton);
        movieTable = findViewById(R.id.movieTable);
        mBackButton = findViewById(R.id.backButton);

        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(SearchResults.this);

        final ArrayList<HashMap<Integer, String>> movieList = (ArrayList<HashMap<Integer, String>>) getIntent().getSerializableExtra("MovieTitles");

        System.out.println(movieList.size());

        // back button for testing
        backButtonClicked(mBackButton);

        createImageButtons(movieList);
    }


    // gets user input
    public String getUserInput() {

        String text = mMovieEditText.getText().toString();

        final String url = MainActivity.mainURL + "&t=" + text;

        // multiple results
        final String url2 = MainActivity.mainURL + "&s=" + text;
        //System.out.println("url: " + url);

        return url2;
    }

    // load the img
    public Bitmap loadIMG(String url) throws java.io.IOException {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("User-agent", "Mozilla/4.0");

        connection.connect();
        InputStream input = connection.getInputStream();
        return BitmapFactory.decodeStream(input);
    }

    // testing back button click
    public void backButtonClicked(Button b) {
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchResults.this, MainActivity.class);

                startActivity(intent);
            }
        });
    }

    public void createImageButtons(ArrayList<HashMap<Integer, String>> list) {

        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(SearchResults.this);

        int count = 0;

        // loop through the movieList array
        for (final HashMap<Integer, String> m : list) {

            // get the index key which gives the title
            final String thisURL = MainActivity.mainURL + "&t=" + m.get(count);
            final String title = m.get(count);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, thisURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                // new json obj for the single title
                                JSONObject obj = new JSONObject(response);

                                // get the poster and title
                                final String poster = obj.getString("Poster");
                                final String title = obj.getString("Title");
                                final String year = obj.getString("Released");
                                final String plot = obj.getString("Plot");
                                final String runtime = obj.getString("Runtime");
                                final String writer = obj.getString("Writer");
                                final String director = obj.getString("Director");
                                final String rating = obj.getString("imdbRating");
                                final String rated = obj.getString("Rated");
                                final String id = obj.getString("imdbID");


                                System.out.println(title);

                                // make new imagebutton and textview

                                final ImageButton iB = new ImageButton(SearchResults.this);
                                final TextView tV = new TextView(SearchResults.this);

                                // add to the view
                                movieTable.addView(iB);
                                movieTable.addView(tV);

                                // get movie image from the json
                                iB.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                                if(poster == "N/A") {
                                    iB.setImageResource(R.drawable.noposter);
                                } else {
                                    Drawable d = new BitmapDrawable(getResources(), loadIMG(poster));
                                    iB.setImageDrawable(d);
                                }


                                iB.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Intent i = new Intent(getApplicationContext(), ResultPage.class);

                                        i.putExtra("Title", title);
                                        i.putExtra("Poster", poster);
                                        i.putExtra("Year", year);
                                        i.putExtra("Plot", plot);
                                        i.putExtra("ID", id);
                                        i.putExtra("Runtime", runtime);
                                        i.putExtra("Writer", writer);
                                        i.putExtra("Director", director);
                                        i.putExtra("Rating", rating);
                                        i.putExtra("Rated", rated);
                                        i.putExtra("ID", id);

                                        startActivity(i);
                                    }
                                });

                                tV.setText(title);
                                tV.setTextSize(20);
                                tV.setGravity(Gravity.CENTER);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SearchResults.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });
            count++;
            queue.add(stringRequest);
        }
    }

    public void executeSearch(String response, LinearLayout l) {
        try {
            // converts response to JSONObject
            JSONObject o = new JSONObject(response);

            // The array starts at Search
            JSONArray search = o.getJSONArray("Search");

            // Array of TextViews for the titles
            TextView[] movieText = new TextView[search.length()];

            // create hashmap of
            ArrayList<HashMap<Integer, String>> movieList = new ArrayList<>();

            // Remove TextViews from screen after each search
            l.removeAllViews();

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
                movieText[i] = new TextView(SearchResults.this);


                // add the movie title to the view
                //movieTable.addView(movieText[i]);

                movieText[i].setText(n);
                movieText[i].setTextSize(20);

            }

            createImageButtons(movieList);
            System.out.println(response);
        } catch (Exception ex) {
            Toast.makeText(SearchResults.this, "None in database", Toast.LENGTH_SHORT).show();
        }
    }
}