package com.evdokimoveu.testapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.evdokimoveu.testapplication.model.Film;

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

public class MainActivity extends AppCompatActivity {

    private ArrayList<Film> films;
    private GridView gridView;
    private String sort = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        films = new ArrayList<>();
        if(savedInstanceState != null){
            sort = savedInstanceState.getString("sort");
            if(sort.equals("favorite")){
                loadFilmsFromDB();
            }
            else if(sort.equals("top_rated")){
                new JSONParser(TMDBConfig.URL + TMDBConfig.SORT_ORDER_BY_TOP_RATED+"?api_key="+TMDBConfig.KEY).execute();
            }
            else {
                new JSONParser(TMDBConfig.URL + TMDBConfig.SORT_ORDER_BY_POPULAR+"?api_key="+TMDBConfig.KEY).execute();
                sort = "popular";
            }
        }
        else{
            new JSONParser(TMDBConfig.URL + TMDBConfig.SORT_ORDER_BY_POPULAR+"?api_key="+TMDBConfig.KEY).execute();
            sort = "popular";
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("sort", sort);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_popular) {
            new JSONParser(TMDBConfig.URL + TMDBConfig.SORT_ORDER_BY_POPULAR+"?api_key="+TMDBConfig.KEY).execute();
            sort = "popular";
            return true;
        }
        else if (id == R.id.action_top_rated) {
            new JSONParser(TMDBConfig.URL + TMDBConfig.SORT_ORDER_BY_TOP_RATED+"?api_key="+TMDBConfig.KEY).execute();
            sort = "top_rated";
            return true;
        }
        else if (id == R.id.action_favorite) {
            loadFilmsFromDB();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class JSONParser extends AsyncTask<String, String, String>{

        private String resultJson = "";
        private String urlRequest;

        public JSONParser(String urlRequest) {
            this.urlRequest = urlRequest;
        }

        @Override
        protected String doInBackground(String... params) {
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
            films.clear();
            try {
                JSONObject object = new JSONObject(strJson);
                JSONArray array = object.getJSONArray("results");
                for(int i = 0; i < array.length(); i++){
                    JSONObject filmObject = array.getJSONObject(i);
                    Film film = new Film.Builder()
                            .id(filmObject.getString("id"))
                            .date(filmObject.getString("release_date"))
                            .posterPath(filmObject.getString("poster_path"))
                            .title(filmObject.getString("original_title"))
                            .overview(filmObject.getString("overview"))
                            .voteAverage(filmObject.getString("vote_average"))
                            .build();
                    films.add(film);
                }
                gridView = (GridView) findViewById(R.id.grid_photo_view);
                if(gridView != null){
                    final FilmListAdapter adapter = new FilmListAdapter(MainActivity.this, films);
                    gridView.setAdapter(adapter);
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            onPosterClick((Film)adapter.getItem(position));
                        }
                    });
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void onPosterClick(Film film) {
        Intent intent = new Intent(this, FilmDetailActivity.class);
        intent.putExtra("film", film);
        startActivity(intent);
    }

    private void loadFilmsFromDB(){
        films.clear();
        DBFavoriteFilms dbFavoriteFilms = new DBFavoriteFilms(this, DBFavoriteFilms.DATA_BASE_NAME, null, 1);
        SQLiteDatabase sqLiteDatabase = dbFavoriteFilms.getReadableDatabase();
        Cursor cursor= sqLiteDatabase.query(dbFavoriteFilms.TABLE_FILM, new String[]{
                DBFavoriteFilms.DB_TITLE_FIELD,
                DBFavoriteFilms.DB_OVERVIEW_FIELD,
                DBFavoriteFilms.DB_VOTE_AVERAGE_FIELD,
                DBFavoriteFilms.DB_POSTER_FIELD,
                DBFavoriteFilms.DB_DATE_FIELD,
                DBFavoriteFilms.DB_ID_FIELD,
        }, null, null, null, null, null);
        while(cursor.moveToNext()){
            String title = cursor.getString(cursor.getColumnIndex(DBFavoriteFilms.DB_TITLE_FIELD));
            String overview = cursor.getString(cursor.getColumnIndex(DBFavoriteFilms.DB_OVERVIEW_FIELD));
            String voteAverage = cursor.getString(cursor.getColumnIndex(DBFavoriteFilms.DB_VOTE_AVERAGE_FIELD));
            String poster = cursor.getString(cursor.getColumnIndex(DBFavoriteFilms.DB_POSTER_FIELD));
            String date = cursor.getString(cursor.getColumnIndex(DBFavoriteFilms.DB_DATE_FIELD));
            String idFilm = cursor.getString(cursor.getColumnIndex(DBFavoriteFilms.DB_ID_FIELD));

            Film film = new Film.Builder()
                    .id(idFilm)
                    .date(date)
                    .posterPath(poster)
                    .title(title)
                    .overview(overview)
                    .voteAverage(voteAverage)
                    .build();;
            films.add(film);
        }
        cursor.close();
        sqLiteDatabase.close();

        gridView = (GridView) findViewById(R.id.grid_photo_view);
        if(gridView != null){
            final FilmListAdapter adapter = new FilmListAdapter(MainActivity.this, films);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onPosterClick((Film)adapter.getItem(position));
                }
            });
        }
        sort = "favorite";
    }
}
