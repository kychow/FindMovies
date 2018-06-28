package com.kychow.trailerexplorer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kychow.trailerexplorer.models.Movie;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    // list of movies
    ArrayList<Movie> movies;

    //initiate with list
    public MovieAdapter(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    // creates and inflates new view
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // get context and create inflater
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // create view using item_movie layout
        View movieView = inflater.inflate(R.layout.item_movie, parent, false);
        // return new ViewHolder
        return new ViewHolder(movieView);
    }

    // binds inflated view to new item
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get movie data at specified position
        Movie movie = movies.get(position);
        // populate view with movie data
        holder.tvTitle.setText(movie.getTitle());
        holder.tvOverview.setText(movie.getOverview());

        // TODO - set image using Gl ide
    }

    // returns size of data set
    @Override
    public int getItemCount() {
        return movies.size();
    }

    // create viewholder as static inner class
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // track view objects
        ImageView ivPosterImage;
        TextView tvTitle;
        TextView tvOverview;

        public ViewHolder(View itemView) {
            super(itemView);
            // lookup view objects by id
            ivPosterImage = itemView.findViewById(R.id.ivPosterImage);
            tvOverview = (TextView) itemView.findViewById(R.id.tvTitle);
        }
        public TextView getTvTitle() {
            return tvTitle;
        }
    }

}
