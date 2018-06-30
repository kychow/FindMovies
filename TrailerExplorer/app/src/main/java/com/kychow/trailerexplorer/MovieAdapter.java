package com.kychow.trailerexplorer;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kychow.trailerexplorer.models.Config;
import com.kychow.trailerexplorer.models.Movie;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    ArrayList<Movie> movies;
    // image url config
    Config config;
    // rendering context
    Context context;
    Boolean changeColor;

    //initiate with list
    public MovieAdapter(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    // creates and inflates new view
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // get context and create inflater
        context=parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // create view using item_movie layout
        View movieView = inflater.inflate(R.layout.item_movie, parent, false);
        changeColor = false;
        // return new ViewHolder
        return new ViewHolder(movieView);
    }

    // binds inflated view to new item
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        if (changeColor) {
            if (changeColor) {
                holder.tvOverview.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                holder.tvTitle.setText(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            } else {
                holder.tvOverview.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                holder.tvTitle.setText(ContextCompat.getColor(context, R.color.colorPrimary));
            }
        }
        // get movie data at specified position
        Movie movie = movies.get(position);
        // populate view with movie data
        holder.tvTitle.setText(movie.getTitle());
        holder.tvOverview.setText(movie.getOverview());
        //holder.tvOverview.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);

        // determine current orientation
        boolean isPortrait = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        // build url for poster image
        String imageUrl = null;

        // if im portrait mode, load poster image
        if (isPortrait) {
            imageUrl = config.getImageUrl(config.getPosterSize(), movie.getPosterPath());
        } else {
            // load backdrop image
            imageUrl = config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath());
        }

        // get correct placeholder & imageView for current orientation
        int placeholderId = isPortrait ? R.drawable.flicks_movie_placeholder : R.drawable.flicks_backdrop_placeholder;
        ImageView imageView = isPortrait ? holder.ivPosterImage : holder.ivBackdropImage;

        // load image using glide
        GlideApp.with(context)
                .load(imageUrl)
                .transform(new RoundedCornersTransformation(15, 0))
                .placeholder(placeholderId)
                .error(placeholderId)
                .into(imageView);
    }

    // returns size of data set
    @Override
    public int getItemCount() {
        return movies.size();
    }

    // create view holder, implements View.OnClickListener
    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {

        @Nullable @BindView(R.id.ivPosterImage) ImageView ivPosterImage;
        @Nullable @BindView(R.id.ivBackdropImage) ImageView ivBackdropImage;
        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.tvOverview) TextView tvOverview;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public TextView getTvTitle() {
            return tvTitle;
        }
        // when user clicks on row, show MovieDetailsActivity for selected movie

        @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                // check position is valid
                if (position != RecyclerView.NO_POSITION) {
                    Movie movie = movies.get(position);
                    Intent intent = new Intent (context, MovieDetailsActivity.class);
                    intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));
                    intent.putExtra("image_info", Parcels.wrap(config));
                    context.startActivity(intent);
                }
        }


    }



}
