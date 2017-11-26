package com.example.s522050.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.s522050.movieapp.Adapter.MovieListAdapter;
import com.example.s522050.movieapp.Common.Common;
import com.example.s522050.movieapp.Inteface.MovieService;
import com.example.s522050.movieapp.Model.Genre;
import com.example.s522050.movieapp.Model.Movie;
import com.example.s522050.movieapp.Model.MovieDetails;
import com.example.s522050.movieapp.Model.Result;
import com.example.s522050.movieapp.Model.Videos;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetail extends YouTubeBaseActivity  {

    MovieService mMovieService;
    private Movie mMovie;
    private String trailer_key;

    //Youtube player
    private YouTubePlayerView trailer;
    private TextView no_trailer_text_message;
    private LinearLayout trailer_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        //Init movieService
        mMovieService = Common.getMovieService();

        // Get movie object from the movie adapter when user clicks an item
        mMovie = getIntent().getParcelableExtra(MovieListAdapter.MOVIE_DETAIL_KEY);

        //Init Views
        KenBurnsView imageView = findViewById(R.id.top_poster_image);
        TextView title_text = findViewById(R.id.detail_title);
        TextView overview_text = findViewById(R.id.detail_overview);
        final TextView status_text = findViewById(R.id.detail_status);
        TextView release_date_text = findViewById(R.id.detail_release_date);
        TextView original_language_text = findViewById(R.id.detail_language);
        final TextView runtime_text = findViewById(R.id.detail_runtime);
        final TextView genre_text = findViewById(R.id.detail_genre);

        /** Initializing Youtube Player View **/
        trailer = findViewById(R.id.trailer);
        trailer_layout = findViewById(R.id.trailer_layout);
        no_trailer_text_message = findViewById(R.id.no_trailer_message);

        overview_text.setText(mMovie.getOverview());
        title_text.setText(mMovie.getTitle());
        original_language_text.setText(mMovie.getOriginal_language());
        release_date_text.setText(mMovie.getRelease_date());

        Picasso.with(getBaseContext())
                .load(mMovie.getBackdrop_path())
                .into(imageView);

        final List<String> genreList = new ArrayList<>();

        //Start get movie detail contents
        //https://www.youtube.com/watch?v=SUXWAEX2jlg
        mMovieService.getTrailerList(mMovie.getId(),Common.API_KEY,getString(R.string.query_videos)).enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                MovieDetails movieDetails = response.body();
                status_text.setText(movieDetails.getStatus());
                runtime_text.setText(String.format("%s %s", String.valueOf(response.body().getRuntime()), getString(R.string.min_label)));
                List<Genre> genres = movieDetails.getGenres();
                for(Genre genre : genres)
                    genreList.add(genre.getName());
                String addedGenre = TextUtils.join(",  ",genreList);
                genre_text.setText(addedGenre);

                final List<Result> trailerKeys;
                //Get Trailer's keys
                trailerKeys = movieDetails.getVideos().getResults();
                //여기서부터 시작  (트레일러 키 값을 3개 조건확인 후 가져오기)

                // No trailer
                if (trailerKeys.size() < 1){
                    trailer_layout.setVisibility(View.GONE);
                }else{
                    trailer_layout.setVisibility(View.VISIBLE);

                    trailer.initialize(Common.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
                        @Override
                        public void onInitializationSuccess(Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
                            if (!wasRestored) {
                                List<String> trailers = new ArrayList<>();
                                trailers.add(trailerKeys.get(0).getKey());
                                if (trailers.size() > 1){
                                    trailers.add(trailerKeys.get(1).getKey());
                                }
                                youTubePlayer.loadVideos(trailers);
                            }
                        }

                        @Override
                        public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorReason) {
                            String error = String.format(getString(R.string.player_error), errorReason.toString());
                            Toast.makeText(MovieDetail.this, error, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {

            }
        });
    }
}
