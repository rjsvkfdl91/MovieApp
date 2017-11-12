package com.example.s522050.movieapp;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeoutException;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RetryClickListener {

    public static final String MOVIE_DETAIL_KEY = "detail";
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ImageView first_movie_image;
    private TextView mFirstTitle, mFirstReleaseDate;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private RecyclerView mListMovie;
    private LinearLayout error_layout;
    private AppBarLayout main_appbar_layout;
    private Button retry_btn;
    private TextView error_text;
    LinearLayoutManager linearLayoutManager;
    MovieService mMovieService;
    MovieListAdapter mAdapter;
    private SpotsDialog loadingBar;

    // Index from which pagination should start (0 is 1st page in our case)
    private static final int PAGE_START = 1;
    // Indicates if footer ProgressBar is shown (i.e. next page is loading)
    private boolean isLoading = false;
    // If current page is the last page (Pagination will stop after this page load)
    private boolean isLastPage = false;
    // total no. of pages to load. Initial load is page 0, after which 2 more pages will load.
    private int TOTAL_PAGES = 10;
    // indicates the current page which Pagination is fetching.
    private int currentPage = PAGE_START;

    String allMovieList;
    Movie firstMovie_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting toolbar
        Toolbar mainToolBar = findViewById(R.id.main_toolBar);
        setSupportActionBar(mainToolBar);
        getSupportActionBar().setTitle("Movie List");

//        //Bottom navigation
//        final BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        //Init cache
        Paper.init(this);

        //Init Service
        mMovieService = Common.getMovieService();
        loadingBar = new SpotsDialog(this);

        //Init Views
        progressBar = findViewById(R.id.main_progress);
        main_appbar_layout = findViewById(R.id.app_bar);
        error_layout = findViewById(R.id.error_layout);
        error_text = findViewById(R.id.error_txt_cause);
        retry_btn = findViewById(R.id.error_btn_retry);
        first_movie_image = findViewById(R.id.first_movie_image);
        mFirstTitle = findViewById(R.id.first_movie_title);
        mFirstReleaseDate = findViewById(R.id.first_movie_rate);
        first_movie_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailIntent = new Intent(MainActivity.this,MovieDetail.class);
                detailIntent.putExtra(MOVIE_DETAIL_KEY,firstMovie_content);
                startActivity(detailIntent);
            }
        });

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFirstPage(true);
            }
        });

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
                        loadNextPage(false);
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

        loadFirstPage(false);

        retry_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFirstPage(false);
            }
        });

    }

    private void loadFirstPage(boolean isRefreshed) {

        hideErrorView();

        mMovieService.getPopularLists(currentPage, Common.API_KEY).enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response) {

                progressBar.setVisibility(View.GONE);
                main_appbar_layout.setVisibility(View.VISIBLE);

                firstMovie_content = response.body().getResults().get(0);

                try {
                    Picasso.with(getBaseContext())
                            .load(response.body().getResults().get(0).getPoster_path())
                            .into(first_movie_image);

                    mFirstTitle.setText(response.body().getResults().get(0).getTitle());
                    mFirstReleaseDate.setText(response.body().getResults().get(0).getRelease_date());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                List<Movie> movieList = response.body().getResults();
                movieList.remove(0);
                mAdapter.addAll(movieList);

                allMovieList += new Gson().toJson(response.body().getResults());

                //Save to cache
                Paper.book().write("cache",allMovieList);

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

    private void loadNextPage(boolean isRefreshed) {

        mMovieService.getPopularLists(currentPage, Common.API_KEY).enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response) {

                mAdapter.removeLoadingFooter();
                isLoading = false;

                List<Movie> movieList = response.body().getResults();
                mAdapter.addAll(movieList);

                allMovieList += new Gson().toJson(response.body().getResults());
//                //Save to cache
//                Paper.book().write("cache",allMovieList);

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
        loadNextPage(false);
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