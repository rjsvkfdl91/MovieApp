package com.example.s522050.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.s522050.movieapp.Adapter.MovieListAdapter;
import com.example.s522050.movieapp.Common.Common;
import com.example.s522050.movieapp.Helper.NetworkHelper;
import com.example.s522050.movieapp.Helper.PaginationScrollListener;
import com.example.s522050.movieapp.Inteface.MovieService;
import com.example.s522050.movieapp.Inteface.RetryClickListener;
import com.example.s522050.movieapp.Model.Movie;
import com.example.s522050.movieapp.Model.MovieList;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.List;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RetryClickListener{

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private MaterialSearchView mSearchView;
    private ProgressBar progressBar;
    private RecyclerView mListMovie;
    private LinearLayout error_layout;
    private Button retry_btn;
    private TextView error_text,user_name_text;
    private LinearLayoutManager linearLayoutManager;
    private MovieService mMovieService;
    private MovieListAdapter mAdapter;
    private Toolbar mMainToolBar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawableToggle;

    // Settings for pagination
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 15;
    private int currentPage = PAGE_START;

    //FireBase Auth
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing auth
        mAuth = FirebaseAuth.getInstance();

        //Setting toolbar
        mMainToolBar = findViewById(R.id.main_toolBar);
        setSupportActionBar(mMainToolBar);
        getSupportActionBar().setTitle("Popular Movies");


        //Setting DrawerLayout
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigation_view);
        mDrawableToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mMainToolBar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mDrawableToggle);
        mDrawableToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();
                switch (id) {
                    case R.id.navigation_top_rated_movie:
                        Intent topIntent = new Intent(MainActivity.this,TopMovieActivity.class);
                        startActivity(topIntent);
                        break;
                    case R.id.navigation_playing_movie:
                        Intent playingIntent = new Intent(MainActivity.this,PlayingMovieActivity.class);
                        startActivity(playingIntent);
                        break;

                    case R.id.navigation_upcoming_movie:
                        Intent upcomingIntent = new Intent(MainActivity.this,UpcomingMovieActivity.class);
                        startActivity(upcomingIntent);
                        break;
                    case R.id.navigation_sign_out:
                        // This is a code to log out
                        FirebaseAuth.getInstance().signOut();
                        //If user log out, return to start page
                        sentToStart();
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        //Init Service
        mMovieService = Common.getMovieService();

        //Init Views
        progressBar = findViewById(R.id.main_progress);
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
                        loadPopularNextPage();
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


        loadPopularFirstPage();

        retry_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPopularFirstPage();
            }
        });

        mSearchView = findViewById(R.id.searchView);
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent searchIntent = new Intent(MainActivity.this,SearchActivity.class);
                searchIntent.putExtra("searchQuery",query);
                startActivity(searchIntent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //Check whether user is signed in or not
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //if user is null, back to start page
        if(currentUser == null){
            sentToStart();
        }else{
            //if user is not null, show user name through the navigation header
            View headerView = mNavigationView.getHeaderView(0);
            user_name_text = headerView.findViewById(R.id.user_name);
            String current_uid = currentUser.getUid();
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid).child("name");
            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue().toString();
                    user_name_text.setText(name);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void sentToStart() {
        Intent startIntent = new Intent(MainActivity.this,StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    // To uncheck navigation menu item when return to the MainActivity
    @Override
    protected void onResume() {
        int size = mNavigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            mNavigationView.getMenu().getItem(i).setChecked(false);
        }
        super.onResume();
    }

    private void loadPopularFirstPage() {

        hideErrorView();

        mMovieService.getPopularLists(currentPage, Common.API_KEY).enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response) {

                progressBar.setVisibility(View.GONE);
//                main_appbar_layout.setVisibility(View.VISIBLE);

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

    private void loadPopularNextPage() {

        mMovieService.getPopularLists(currentPage, Common.API_KEY).enqueue(new Callback<MovieList>() {
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
        loadPopularNextPage();
    }

    private void hideErrorView() {
        error_layout.setVisibility(View.INVISIBLE);
//        main_appbar_layout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        mDrawerLayout.setVisibility(View.VISIBLE);
    }

    private void showErrorView(Throwable throwable){

        //Show error message depending on the situation
        progressBar.setVisibility(View.GONE);
//        main_appbar_layout.setVisibility(View.INVISIBLE);
        error_layout.setVisibility(View.VISIBLE);
        mDrawerLayout.setVisibility(View.INVISIBLE);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_detail_menu,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawableToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // close searchView when it is open
    @Override
    public void onBackPressed() {
        if (mSearchView.isSearchOpen()) {
            mSearchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }
}