package me.infernokun.imdbsearcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ResultPage extends AppCompatActivity {

    private ImageView mPosterView;
    private TextView mTitleView, mLinkView, mPlotView, mYearView, mRuntimeView, mWriterView, mDirectorView, mRatingView, mRatedView;
    private Button mBackButton_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_page);

        setImage();

        mLinkView = findViewById(R.id.linkView);
        mPlotView = findViewById(R.id.plotView);
        mTitleView = findViewById(R.id.titleView);
        mYearView = findViewById(R.id.yearView);
        mRuntimeView = findViewById(R.id.runtimeView);
        mWriterView = findViewById(R.id.writerView);
        mDirectorView = findViewById(R.id.directorView);
        mRatingView = findViewById(R.id.ratingView);
        mRatedView = findViewById(R.id.ratedView);
        mBackButton_result = findViewById(R.id.backButton_result);

        backButtonClicked(mBackButton_result);

        mYearView.setText("Release Date: " + getIntent().getStringExtra("Year"));
        mLinkView.setText("https://www.imdb.com/title/" + getIntent().getStringExtra("ID") + "/");
        mTitleView.setText(getIntent().getStringExtra("Title"));
        mPlotView.setText(getIntent().getStringExtra("Plot"));
        mRuntimeView.setText("Runtime: " + getIntent().getStringExtra("Runtime"));
        mWriterView.setText("Writer: " + getIntent().getStringExtra("Writer"));
        mDirectorView.setText("Director: " + getIntent().getStringExtra("Director"));
        mRatingView.setText("IMDB Rating: " + getIntent().getStringExtra("Rating"));
        mRatedView.setText("Rated: " + getIntent().getStringExtra("Rated"));
    }

    public void backButtonClicked(Button b) {
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultPage.this, MainActivity.class);

                startActivity(intent);
            }
        });
    }

    public void setImage() {
        mPosterView = findViewById(R.id.posterView);

        String poster = getIntent().getStringExtra("Poster");

        try {
            Drawable d = new BitmapDrawable(getResources(), loadIMG(poster));
            mPosterView.setImageDrawable(d);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Bitmap loadIMG(String url) throws IOException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("User-agent", "Mozilla/4.0");

        connection.connect();
        InputStream input = connection.getInputStream();
        return BitmapFactory.decodeStream(input);
    }
}