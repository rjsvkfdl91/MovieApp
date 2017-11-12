package com.example.s522050.movieapp.Inteface;


import com.example.s522050.movieapp.Model.MovieList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieService {

    @GET("movie/popular")
    Call<MovieList> getPopularLists(@Query("page")int pageNum,@Query("api_key")String api_key);

    @GET("movie/top_rated")
    Call<MovieList> getTopRatedLists(@Query("api_key")String api_key);

}
