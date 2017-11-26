package com.example.s522050.movieapp.Common;


import com.example.s522050.movieapp.Inteface.MovieService;
import com.example.s522050.movieapp.Remote.RetrofitClient;

public class Common {

    private static final String BASE_URL ="https://api.themoviedb.org/3/";
    public static final String API_KEY = "f550e08b06ace6ad5af15c91dfd849fe";
    public static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";
    public static final String YOUTUBE_API_KEY = "AIzaSyBVOHaGKJfdr2YnrQKu9wIwlQsxr3ynvj8";


    public static MovieService getMovieService(){
        return RetrofitClient.getRetrofit(BASE_URL).create(MovieService.class);
    }
}
