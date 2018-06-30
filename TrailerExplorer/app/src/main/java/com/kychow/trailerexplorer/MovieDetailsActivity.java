package com.kychow.trailerexplorer;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kychow.trailerexplorer.models.Config;
import com.kychow.trailerexplorer.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.kychow.trailerexplorer.MainActivity.API_BASE_URL;
import static com.kychow.trailerexplorer.MainActivity.API_KEY_PARAM;

public class MovieDetailsActivity extends AppCompatActivity {

    Movie movie;
    Config config;
    AsyncHttpClient client;
    String key;

    // view objects
    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tvOverview) TextView tvOverview;
    @BindView(R.id.rbVoteAverage) RatingBar rbVoteAverage;
    @BindView(R.id.tvImage) ImageView tvImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        client = new AsyncHttpClient();

        // unwrap passed in movie from intent
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        config = (Config) Parcels.unwrap(getIntent().getParcelableExtra("image_info"));
//        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", config.getImageUrl());

        // set title & overview
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());

        // convert 0.1 vote average to 0.05: divide by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);

        boolean isPortrait = getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        // url for poster image
        String imageUrl = null;

        // if im portrait mode, load poster image
        if (isPortrait) {
            imageUrl = config.getImageUrl(config.getPosterSize(), movie.getPosterPath());
        } else {
            // load backdrop image
            imageUrl = config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath());
        }

        // get correct placeholder & imageView for current orientation
        int placeholderId = isPortrait ? R.drawable.flicks_movie_placeholder : R.drawable.flicks_backdrop_placeholder;
        //ImageView imageView = isPortrait ? holder.ivPosterImage : holder.ivBackdropImage;

        // load image using glide
        GlideApp.with(getApplicationContext())
                .load(imageUrl)
                .transform(new RoundedCornersTransformation(15, 0))
                .placeholder(placeholderId)
                .error(placeholderId)
                .into(tvImage);
    }

    private void getTrailerId(Movie movie) {
        String url = API_BASE_URL + "/movie/" + movie.getId() + "/videos";
        // set request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key)); // API key: always required
        // execute GET request - expects JSON object
        client.get(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    // iterate through result set and create Movie objects
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject object = results.getJSONObject(i);
                        if (object != null) {
                            key = object.getString("key");
                            Intent intent = new Intent (getBaseContext(), MovieTrailerActivity.class);
                            intent.putExtra("key-string", key);
                            startActivity(intent);
                        }
                    }
                    Log.i("MovieDetailsActivity", String.format("Loaded %s movies", results.length()));
                } catch (JSONException e) {
                    logError("Failed to parse now playing movies", e, true);
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                logError("Failed to get data from now_playing endpoint", throwable, true);
            }
        });
    }

    // handle errors, log & alert user
    private void logError(String message, Throwable error, boolean alertUser) {
        // always log error
        Log.e("MovieDetailsActivity", message, error);
        // alert user to avoid silent errors
        if (alertUser) {
            // show long toast with error message
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.tvImage)
    public void onClick(View v) {
        // TODO specify onclick attribute in xml file
        getTrailerId(movie);
        //Movie movie = movies.get(position);

    }
}
