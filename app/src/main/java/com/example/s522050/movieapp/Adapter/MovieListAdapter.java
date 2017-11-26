package com.example.s522050.movieapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.s522050.movieapp.Inteface.ItemClickListener;
import com.example.s522050.movieapp.Inteface.RetryClickListener;
import com.example.s522050.movieapp.Model.Movie;
import com.example.s522050.movieapp.MovieDetail;
import com.example.s522050.movieapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String MOVIE_DETAIL_KEY = "detail";

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    private RetryClickListener retryClickListener;
    private String errorMsg;

    private Context context;
    private List<Movie> movieList;


    public MovieListAdapter(Context context) {
        this.context = context;
        this.retryClickListener = (RetryClickListener) context;
        this.movieList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                View itemView = inflater.inflate(R.layout.item_layout, parent, false);
                viewHolder = new MovieViewHolder(itemView);
                break;
            case LOADING:
                View loadingView = inflater.inflate(R.layout.item_load_progress, parent, false);
                viewHolder = new LoadingViewHolder(loadingView);
                break;
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Movie movie = movieList.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                final MovieViewHolder movieHolder = (MovieViewHolder) holder;

                movieHolder.movie_title.setText(movie.getTitle());
                movieHolder.movie_release_date.setText(movie.getRelease_date());
                movieHolder.movie_average_vote.setText(String.valueOf(movie.getVote_average()));
                movieHolder.movie_overview.setText(movie.getOverview());
                Picasso.with(context).load(movie.getPoster_path())
                        .fit()
                        .placeholder(R.mipmap.ic_launcher)
                        .into(movieHolder.movie_poster);

                movieHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onListItemClick(View view, int position) {
                        Intent detailIntent = new Intent(context,MovieDetail.class);
                        detailIntent.putExtra(MOVIE_DETAIL_KEY,movie);
                        context.startActivity(detailIntent);
                    }
                });
                break;

            case LOADING:
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;

                if (retryPageLoad){
                    loadingViewHolder.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingViewHolder.mProgressBar.setVisibility(View.GONE);
                    loadingViewHolder.mErrorTxt.setText(errorMsg != null ? errorMsg : context.getString(R.string.error_msg_unknown));
                }else{
                    loadingViewHolder.mErrorLayout.setVisibility(View.GONE);
                    loadingViewHolder.mProgressBar.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return movieList == null ? 0 : movieList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == movieList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(Movie mc) {
        movieList.add(mc);
        notifyItemInserted(movieList.size() - 1);
    }

    public void addAll(List<Movie> mcList) {
        for (Movie mc : mcList) {
            add(mc);
        }
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Movie());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = movieList.size() - 1;
        Movie item = getItem(position);

        if (item != null) {
            movieList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Movie getItem(int position) {
        return movieList.get(position);
    }

    // To store error message to show
    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(movieList.size() - 1);

        if (errorMsg != null)
            this.errorMsg = errorMsg;
    }


    /*
   ViewHolders
   _________________________________________________________________________________________________
    */

    protected class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ItemClickListener itemClickListener;
        TextView movie_title;
        TextView movie_release_date;
        TextView movie_average_vote;
        TextView movie_overview;
        ImageView movie_poster;


        public MovieViewHolder(View itemView) {
            super(itemView);

            movie_title = itemView.findViewById(R.id.movie_title);
            movie_release_date = itemView.findViewById(R.id.release_date);
            movie_average_vote = itemView.findViewById(R.id.average_vote);
            movie_overview = itemView.findViewById(R.id.movie_overview);
            movie_poster = itemView.findViewById(R.id.poster_image);

            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onListItemClick(view,getAdapterPosition());
        }
    }
    protected class LoadingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        public LoadingViewHolder(View itemView) {
            super(itemView);

            mProgressBar = itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = itemView.findViewById(R.id.loadmore_errorlayout);

            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:

                    showRetry(false, null);
                    retryClickListener.retryPageLoad();

                    break;
            }
        }
    }
}
