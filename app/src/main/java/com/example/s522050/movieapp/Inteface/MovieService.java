package com.example.s522050.movieapp.Inteface;


import com.example.s522050.movieapp.Model.MovieList;
import com.example.s522050.movieapp.Model.MovieDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieService {

    @GET("movie/popular")
    Call<MovieList> getPopularLists(@Query("page")int pageNum,@Query("api_key")String api_key);

    @GET("movie/top_rated")
    Call<MovieList> getTopRatedLists(@Query("page")int pageNum,@Query("api_key")String api_key);

    @GET("movie/now_playing")
    Call<MovieList> getPlayingLists(@Query("page")int pageNum,@Query("api_key")String api_key);

    @GET("movie/upcoming")
    Call<MovieList> getUpcomingLists(@Query("page")int pageNum,@Query("api_key")String api_key);

    @GET("search/movie")
    Call<MovieList> getSearchLists(@Query("page")int pageNum,@Query("api_key")String api_key,@Query("query") String query);

    //https://api.themoviedb.org/3/search/movie?api_key=f550e08b06ace6ad5af15c91dfd849fe&query=spiderman

    //346364?api_key=f550e08b06ace6ad5af15c91dfd849fe&append_to_response=videos

    @GET("movie/{id}")
    Call<MovieDetails> getTrailerList(@Path("id") int movie_id,
                                      @Query("api_key")String api_key,
                                      @Query("append_to_response")String video);

}
