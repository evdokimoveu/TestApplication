package com.evdokimoveu.testapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evdokimoveu.testapplication.model.Film;
import com.evdokimoveu.testapplication.model.Rewiews;
import com.evdokimoveu.testapplication.model.Trailer;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FilmDetailActivity extends AppCompatActivity {

    private Film film;
    private TextView filmYear;
    private TextView filmTitle;
    private TextView filmRuntime;
    private TextView filmVoteAverage;
    private TextView filmOverview;
    private Button markAsFavorite;
    private ImageView filmPoster;
    private String runtime;
    private ArrayList<Trailer> trailers;
    private ArrayList<Rewiews> reviews;
    private ArrayList<View> customTrailersView;
    private DBFavoriteFilms dbFavoriteFilms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbFavoriteFilms = new DBFavoriteFilms(this, DBFavoriteFilms.DATA_BASE_NAME, null, 1);

        trailers = new ArrayList<>();
        reviews = new ArrayList<>();

        film = (Film)getIntent().getExtras().get("film");
        if(film != null){
            String id = film.getId();
            new JSONParser(TMDBConfig.URL + TMDBConfig.MOVE+id+TMDBConfig.TRAILER+"?api_key="+TMDBConfig.KEY, JSONParser.GET_TRAILER_BY_ID, id).execute();
            new JSONParser(TMDBConfig.URL + TMDBConfig.MOVE+id+TMDBConfig.REWIEWS+"?api_key="+TMDBConfig.KEY, JSONParser.GET_REWIEWS_BY_ID, id).execute();
            new JSONParser(TMDBConfig.URL + TMDBConfig.MOVE+id+"?api_key="+TMDBConfig.KEY, JSONParser.GET_RUNTIME_BY_ID, id).execute();
        }

        filmTitle = (TextView) findViewById(R.id.film_title);
        filmYear = (TextView) findViewById(R.id.text_year);
        filmRuntime = (TextView)findViewById(R.id.text_runtime);
        filmVoteAverage = (TextView)findViewById(R.id.text_vote_average);
        markAsFavorite = (Button)findViewById(R.id.button_mark_as_favorite);
        filmPoster = (ImageView) findViewById(R.id.image_film_poster);
        filmOverview = (TextView)findViewById(R.id.film_overview);

        if(filmTitle != null){
            filmTitle.setText(film.getTitle());
        }

        if(filmYear != null){
            filmYear.setText(film.getDate().split("-")[0]);
        }
        if(filmVoteAverage != null){
            String str = film.getVoteAverage()+"/10";
            filmVoteAverage.setText(str);
        }
        if(markAsFavorite != null){
            markAsFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SQLiteDatabase sqLiteDatabase = dbFavoriteFilms.getWritableDatabase();
                    ContentValues content = new ContentValues();
                    content.put(DBFavoriteFilms.DB_ID_FIELD, film.getId());
                    content.put(DBFavoriteFilms.DB_TITLE_FIELD, film.getTitle());
                    content.put(DBFavoriteFilms.DB_POSTER_FIELD, film.getPosterPath());
                    content.put(DBFavoriteFilms.DB_OVERVIEW_FIELD, film.getOverview());
                    content.put(DBFavoriteFilms.DB_VOTE_AVERAGE_FIELD, film.getVoteAverage());
                    content.put(DBFavoriteFilms.DB_DATE_FIELD, film.getDate());
                    try {
                        sqLiteDatabase.insert(DBFavoriteFilms.TABLE_FILM, null, content);
                        Toast.makeText(FilmDetailActivity.this, "This film add in your favorite.", Toast.LENGTH_SHORT).show();
                    } catch (SQLiteConstraintException ex) {
                        Toast.makeText(FilmDetailActivity.this, "This film already is your favorite.", Toast.LENGTH_SHORT).show();
                    } finally {
                        sqLiteDatabase.close();
                    }

                }
            });
        }
        if(filmPoster != null){
            Picasso.with(this).load(TMDBConfig.IMG_URL + getResources().getString(R.string.img_size) + film.getPosterPath()).into(filmPoster);
        }
        if(filmOverview != null){
            filmOverview.setText(film.getOverview());
        }
    }

    private class JSONParser extends AsyncTask<Void, Void, String> {

        public static final int GET_TRAILER_BY_ID = 1;
        public static final int GET_RUNTIME_BY_ID = 2;
        public static final int GET_REWIEWS_BY_ID = 3;

        private String resultJson = "";
        private String urlRequest;
        private int requestType;
        private String id;

        public JSONParser(String urlRequest, int requestType, String id) {
            this.urlRequest = urlRequest;
            this.requestType = requestType;
            this.id = id;
        }

        @Override
        protected String doInBackground(Void... params) {
            StringBuffer buffer = new StringBuffer();

            try {
                URL url = new URL(urlRequest);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                resultJson = buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);

            try {
                if(requestType == GET_RUNTIME_BY_ID){
                    JSONObject object = new JSONObject(strJson);
                    runtime = object.getString("runtime");
                    if(filmRuntime != null){
                        String str = runtime + " min";
                        filmRuntime.setText(str);
                    }
                }
                else if(requestType == GET_TRAILER_BY_ID){
                    JSONObject object = new JSONObject(strJson);
                    JSONArray array = object.getJSONArray("results");
                    for(int i = 0; i < array.length(); i++){
                        JSONObject filmObject = array.getJSONObject(i);
                        Trailer trailer = new Trailer(
                                filmObject.getString("key"),
                                filmObject.getString("name"),
                                id);
                        trailers.add(trailer);
                    }
                    createTrailersButton();
                }
                else if(requestType == GET_REWIEWS_BY_ID){
                    JSONObject object = new JSONObject(strJson);
                    JSONArray array = object.getJSONArray("results");
                    for(int i = 0; i < array.length(); i++){
                        JSONObject filmObject = array.getJSONObject(i);
                        Rewiews review = new Rewiews(
                                filmObject.getString("author"),
                                filmObject.getString("content"),
                                id);
                        reviews.add(review);
                    }
                    createReviews();
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        private void createTrailersButton(){
            customTrailersView = new ArrayList<>();

            final LinearLayout layOutTrailer = (LinearLayout) findViewById(R.id.layout_trailer);

            layOutTrailer.removeAllViews();
            for(int i = 0; i < trailers.size(); i++){
                View newView = getLayoutInflater().inflate(R.layout.trailer_custom_layout, null);
                ImageButton playTrailer = (ImageButton) newView.findViewById(R.id.play_trailer_image_button);
                TextView titleTrailer = (TextView) newView.findViewById(R.id.text_trailer_title);
                final String trailerKey = trailers.get(i).getKey();
                playTrailer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(TMDBConfig.YOUTUBE_PATH + trailerKey)));
                    }
                });
                titleTrailer.setText(trailers.get(i).getName());
                customTrailersView.add(newView);
                layOutTrailer.addView(newView);
            }
        }
        private void createReviews(){
            final LinearLayout layOutReviews = (LinearLayout) findViewById(R.id.layout_rewiews);
            layOutReviews.removeAllViews();

            for(int i = 0; i < reviews.size(); i++){
                View newView = getLayoutInflater().inflate(R.layout.rewiews_custom_layout, null);
                TextView authorReviews = (TextView) newView.findViewById(R.id.text_rewiews_author);
                TextView contentReviews = (TextView) newView.findViewById(R.id.text_rewiews_content);
                authorReviews.setText(reviews.get(i).getAuthor());
                contentReviews.setText(reviews.get(i).getContent());
                layOutReviews.addView(newView);
            }
        }

    }
}
