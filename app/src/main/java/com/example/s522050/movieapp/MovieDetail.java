package com.example.s522050.movieapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.s522050.movieapp.Adapter.MovieListAdapter;
import com.example.s522050.movieapp.Common.Common;
import com.example.s522050.movieapp.Inteface.MovieService;
import com.example.s522050.movieapp.Model.Movie;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.squareup.picasso.Picasso;

public class MovieDetail extends AppCompatActivity {

    MovieService mMovieService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        //Init movieService
        mMovieService = Common.getMovieService();

        //Init Views
        KenBurnsView imageView = findViewById(R.id.top_poster_image);
        TextView detail = findViewById(R.id.detail_text);

        Movie movie = getIntent().getParcelableExtra(MovieListAdapter.MOVIE_DETAIL_KEY);
        detail.setText(movie.getTitle() + "\n" + movie.getRelease_date());

        Picasso.with(getBaseContext())
                .load(movie.getPoster_path())
                .fit()
                .into(imageView);
    }
}
