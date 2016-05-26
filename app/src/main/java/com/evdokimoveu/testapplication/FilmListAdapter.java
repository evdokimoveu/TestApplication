package com.evdokimoveu.testapplication;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.evdokimoveu.testapplication.model.Film;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FilmListAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<Film> films;

    public FilmListAdapter(Context context, ArrayList<Film> films) {
        this.context = context;
        this.films = films;
    }

    @Override
    public int getCount() {
        return films.size();
    }

    @Override
    public Object getItem(int position) {
        return films.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.valueOf(films.get(position).getId());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.image_item, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.img_film_poster);
            Film film = (Film)this.getItem(position);
            String imagePath = film.getPosterPath();
            Picasso.with(context).load(TMDBConfig.IMG_URL + context.getResources().getString(R.string.img_size) + imagePath).into(imageView);
        } else {
            view = convertView;
            ImageView imageView = (ImageView) view.findViewById(R.id.img_film_poster);
            Film film = (Film)this.getItem(position);
            String imagePath = film.getPosterPath();
            Picasso.with(context).load(TMDBConfig.IMG_URL + context.getResources().getString(R.string.img_size) + imagePath).into(imageView);
        }
        return view;
    }
}
