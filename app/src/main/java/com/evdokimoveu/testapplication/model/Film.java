package com.evdokimoveu.testapplication.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Film implements Parcelable {

    private String id;
    private String title;
    private String date;
    private String posterPath;
    private String voteAverage;
    private String overview;


    protected Film(Parcel in) {
        id = in.readString();
        title = in.readString();
        date = in.readString();
        posterPath = in.readString();
        voteAverage = in.readString();
        overview = in.readString();
    }

    public static final Creator<Film> CREATOR = new Creator<Film>() {
        @Override
        public Film createFromParcel(Parcel in) {
            return new Film(in);
        }

        @Override
        public Film[] newArray(int size) {
            return new Film[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(date);
        dest.writeString(posterPath);
        dest.writeString(voteAverage);
        dest.writeString(overview);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public static class Builder{
        private String id;
        private String title;
        private String date;
        private String posterPath;
        private String voteAverage;
        private String overview;

        public Builder() {}

        public Builder id(String val){
            id = val;
            return this;
        }
        public Builder title(String val){
            title = val;
            return this;
        }
        public Builder date(String val){
            date = val;
            return this;
        }
        public Builder posterPath(String val){
            posterPath = val;
            return this;
        }
        public Builder voteAverage(String val){
            voteAverage = val;
            return this;
        }
        public Builder overview(String val){
            overview = val;
            return this;
        }
        public Film build(){
            return new Film(this);
        }

    }
    private Film(Builder builder){
        id = builder.id;
        title = builder.title;
        date = builder.date;
        posterPath = builder.posterPath;
        voteAverage = builder.voteAverage;
        overview = builder.overview;
    }
}
