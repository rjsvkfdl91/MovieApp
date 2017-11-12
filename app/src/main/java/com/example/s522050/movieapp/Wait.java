//package com.example.s522050.movieapp;
//
//import android.util.Log;
//
//import com.example.s522050.movieapp.Common.Common;
//import com.example.s522050.movieapp.Model.Movie;
//import com.example.s522050.movieapp.Model.MovieList;
//import com.google.gson.Gson;
//import com.squareup.picasso.Picasso;
//
//import java.util.List;
//
//import io.paperdb.Paper;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
///**
// * Created by S522050 on 11/9/2017.
// */
//
//public class Wait {
//
//    private void loadFirstPage(boolean isRefreshed) {
//
//        if (!isRefreshed){
//            String cache = Paper.book().read("cache");
//
//            //If have cache
//            if (cache != null && !cache.isEmpty()){
//                MovieList movieList = new Gson().fromJson(cache,MovieList.class);
//                try {
//                    Picasso.with(getBaseContext())
//                            .load(movieList.getResults().get(0).getPoster_path())
//                            .into(mMainImage);
//
//                    mFirstTitle.setText(movieList.getResults().get(0).getTitle());
//                    mFirstReleaseDate.setText(movieList.getResults().get(0).getRelease_date());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                //Load remaining articles
//                List<Movie> removeFirstItem = movieList.getResults();
//                removeFirstItem.remove(0);
//                mAdapter = new MovieListAdapter(getBaseContext(), removeFirstItem);
//                mAdapter.notifyDataSetChanged();
//                mListMovie.setAdapter(mAdapter);
//            }
//            else{
//                loadingBar.show();
//
//                mMovieService.getPopularLists(currentPage, Common.API_KEY).enqueue(new Callback<MovieList>() {
//                    @Override
//                    public void onResponse(Call<MovieList> call, Response<MovieList> response) {
//                        loadingBar.dismiss();
//
//                        try {
//                            Picasso.with(getBaseContext())
//                                    .load(response.body().getResults().get(0).getPoster_path())
//                                    .into(mMainImage);
//
//                            mFirstTitle.setText(response.body().getResults().get(0).getTitle());
//                            mFirstReleaseDate.setText(response.body().getResults().get(0).getRelease_date());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        //Load remaining articles
//                        List<Movie> removeFirstItem = response.body().getResults();
//                        removeFirstItem.remove(0);
//                        mAdapter = new MovieListAdapter(getBaseContext(), removeFirstItem);
//                        mAdapter.notifyDataSetChanged();
//                        mListMovie.setAdapter(mAdapter);
//
//                        //Save to cache
//                        Paper.book().write("cache", new Gson().toJson(response.body().getResults()));
//                    }
//
//                    @Override
//                    public void onFailure(Call<MovieList> call, Throwable e) {
//                        Log.e(LOG_TAG,e.getMessage());
//                    }
//                });
//            }
//        }else{ // Swipe to refresh
//            loadingBar.show();
//            //Fetch new data
//            mMovieService.getPopularLists(currentPage,Common.API_KEY).enqueue(new Callback<MovieList>() {
//                @Override
//                public void onResponse(Call<MovieList> call, Response<MovieList> response) {
//                    loadingBar.dismiss();
//
//                    try {
//                        Picasso.with(getBaseContext())
//                                .load(response.body().getResults().get(0).getPoster_path())
//                                .into(mMainImage);
//
//                        mFirstTitle.setText(response.body().getResults().get(0).getTitle());
//                        mFirstReleaseDate.setText(response.body().getResults().get(0).getRelease_date());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    //Load remaining articles
//                    List<Movie> removeFirstItem = response.body().getResults();
//                    removeFirstItem.remove(0);
//                    mAdapter = new MovieListAdapter(getBaseContext(), removeFirstItem);
//                    mAdapter.notifyDataSetChanged();
//                    mListMovie.setAdapter(mAdapter);
//
//                    //Save to cache
//                    Paper.book().write("cache",new Gson().toJson(response.body().getResults()));
//
//                    //Dismiss refresh progressing
//                    swipeRefreshLayout.setRefreshing(false);
//                }
//
//                @Override
//                public void onFailure(Call<MovieList> call, Throwable e) {
//                    Log.e(LOG_TAG,e.getMessage());
//                }
//            });
//        }
//    }
//}
