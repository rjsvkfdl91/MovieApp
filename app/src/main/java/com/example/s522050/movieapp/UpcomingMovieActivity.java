package com.example.s522050.movieapp;

import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.s522050.movieapp.Adapter.MovieListAdapter;
import com.example.s522050.movieapp.Common.Common;
import com.example.s522050.movieapp.Helper.NetworkHelper;
import com.example.s522050.movieapp.Helper.PaginationScrollListener;
import com.example.s522050.movieapp.Inteface.MovieService;
import com.example.s522050.movieapp.Inteface.RetryClickListener;
import com.example.s522050.movieapp.Model.Movie;
import com.example.s522050.movieapp.Model.MovieList;

import java.util.List;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpcomingMovieActivity extends AppCompatActivity implements RetryClickListener {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ProgressBar progressBar;
    private RecyclerView mListMovie;
    private LinearLayout error_layout;
    private AppBarLayout main_appbar_layout;
    private Button retry_btn;
    private TextView error_text;
    private LinearLayoutManager linearLayoutManager;
    private MovieService mMovieService;
    private MovieListAdapter mAdapter;
    private Toolbar mMainToolBar;

    // Settings for pagination
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 15;
    private int currentPage = PAGE_START;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting toolbar
        mMainToolBar = findViewById(R.id.main_toolBar);
        setSupportActionBar(mMainToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Upcoming Movies");

        //Init Service
        mMovieService = Common.getMovieService();

        //Init Views
        progressBar = findViewById(R.id.main_progress);
        main_appbar_layout = findViewById(R.id.app_bar);
        error_layout = findViewById(R.id.error_layout);
        error_text = findViewById(R.id.error_txt_cause);
        retry_btn = findViewById(R.id.error_btn_retry);

        // Set up RecyclerView
        mListMovie = findViewById(R.id.movie_list_recycler_view);
        mAdapter = new MovieListAdapter(this);
        mListMovie.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mListMovie.setLayoutManager(linearLayoutManager);
        mListMovie.setItemAnimator(new DefaultItemAnimator());
        mListMovie.setAdapter(mAdapter);
        mListMovie.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1; //Increment page index to load the next one

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                },1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        loadFirstPage();

        retry_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFirstPage();
            }
        });
    }

    private void loadFirstPage() {

        hideErrorView();

        mMovieService.getUpcomingLists(currentPage, Common.API_KEY).enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response) {

                progressBar.setVisibility(View.GONE);
                main_appbar_layout.setVisibility(View.VISIBLE);

                List<Movie> movieList = response.body().getResults();
                mAdapter.addAll(movieList);

                if (currentPage <= TOTAL_PAGES)
                    mAdapter.addLoadingFooter();
                else
                    isLastPage = true;
            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable error) {
                error.printStackTrace();
                showErrorView(error);
            }
        });

    }

    private void loadNextPage() {

        mMovieService.getUpcomingLists(currentPage, Common.API_KEY).enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response) {

                mAdapter.removeLoadingFooter();
                isLoading = false;

                List<Movie> movieList = response.body().getResults();
                mAdapter.addAll(movieList);

                if (currentPage != TOTAL_PAGES)
                    mAdapter.addLoadingFooter();
                else
                    isLastPage = true;
            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable error) {
                error.printStackTrace();
                mAdapter.showRetry(true,fetchErrorMessage(error));
            }
        });
    }

    // Retry Listener interface call back
    @Override
    public void retryPageLoad() {
        loadNextPage();
    }

    private void hideErrorView() {
        error_layout.setVisibility(View.INVISIBLE);
        main_appbar_layout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showErrorView(Throwable throwable){

        //Show error message depending on the situation
        progressBar.setVisibility(View.GONE);
        main_appbar_layout.setVisibility(View.INVISIBLE);
        error_layout.setVisibility(View.VISIBLE);
        error_text.setText(fetchErrorMessage(throwable));
    }

    private String fetchErrorMessage(Throwable throwable) {

        String errorMsg = getString(R.string.error_msg_unknown);
        if (!NetworkHelper.isNetworkConnected(this)) {
            errorMsg = getString(R.string.error_msg_no_internet);
        }else if (throwable instanceof TimeoutException){
            errorMsg = getString(R.string.error_msg_timeout);
        }
        return errorMsg;
    }
}
