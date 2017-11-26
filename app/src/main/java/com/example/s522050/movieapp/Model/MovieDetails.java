package com.example.s522050.movieapp.Model;

import java.util.List;

public class MovieDetails {

    private boolean adult;
    private String backdrop_path;
    private Object belongs_to_collection;
    private int budget;
    private List<Genre> genres = null;
    private String homepage;
    private int id;
    private String imdb_id;
    private String original_Language;
    private String original_Title;
    private String overview;
    private double popularity;
    private String poster_Path;
    private List<Object> production_Companies = null;
    private List<Object> production_Countries = null;
    private String release_Date;
    private int revenue;
    private int runtime;
    private List<Object> spoken_Languages = null;
    private String status;
    private String tagline;
    private String title;
    private boolean video;
    private double vote_Average;
    private int vote_Count;
    private Videos videos;

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getBackdrop_path() {
        return "https://image.tmdb.org/t/p/w500"  + backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public Object getBelongs_to_collection() {
        return belongs_to_collection;
    }

    public void setBelongs_to_collection(Object belongs_to_collection) {
        this.belongs_to_collection = belongs_to_collection;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImdb_id() {
        return imdb_id;
    }

    public void setImdb_id(String imdb_id) {
        this.imdb_id = imdb_id;
    }

    public String getOriginal_Language() {
        return original_Language;
    }

    public void setOriginal_Language(String original_Language) {
        this.original_Language = original_Language;
    }

    public String getOriginal_Title() {
        return original_Title;
    }

    public void setOriginal_Title(String original_Title) {
        this.original_Title = original_Title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getPoster_Path() {
        return poster_Path;
    }

    public void setPoster_Path(String poster_Path) {
        this.poster_Path = poster_Path;
    }

    public List<Object> getProduction_Companies() {
        return production_Companies;
    }

    public void setProduction_Companies(List<Object> production_Companies) {
        this.production_Companies = production_Companies;
    }

    public List<Object> getProduction_Countries() {
        return production_Countries;
    }

    public void setProduction_Countries(List<Object> production_Countries) {
        this.production_Countries = production_Countries;
    }

    public String getRelease_Date() {
        return release_Date;
    }

    public void setRelease_Date(String release_Date) {
        this.release_Date = release_Date;
    }

    public int getRevenue() {
        return revenue;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public List<Object> getSpoken_Languages() {
        return spoken_Languages;
    }

    public void setSpoken_Languages(List<Object> spoken_Languages) {
        this.spoken_Languages = spoken_Languages;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public double getVote_Average() {
        return vote_Average;
    }

    public void setVote_Average(double vote_Average) {
        this.vote_Average = vote_Average;
    }

    public int getVote_Count() {
        return vote_Count;
    }

    public void setVote_Count(int vote_Count) {
        this.vote_Count = vote_Count;
    }

    public Videos getVideos() {
        return videos;
    }

    public void setVideos(Videos videos) {
        this.videos = videos;
    }

}