package com.example.api_mobile_testing.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.api_mobile_testing.R
import com.example.api_mobile_testing.api.ApiConfig
import com.example.api_mobile_testing.model.Movie
import com.example.api_mobile_testing.ui.DetailActivity

class MovieListAdapter(private val movies: List<Movie>) :
    RecyclerView.Adapter<MovieListAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val poster: ImageView = view.findViewById(R.id.ivPoster)
        val title: TextView = view.findViewById(R.id.tvTitle)
        val rating: TextView = view.findViewById(R.id.tvRating)
        val releaseDate: TextView = view.findViewById(R.id.tvReleaseDate)
        val overview: TextView = view.findViewById(R.id.tvOverview)

        fun bind(movie: Movie) {
            title.text = movie.title
            rating.text = String.format("★ %.1f", movie.voteAverage)
            releaseDate.text = if (movie.releaseDate.isNotEmpty()) {
                movie.releaseDate.substring(0, 4)
            } else {
                "N/A"
            }
            overview.text = movie.overview.ifEmpty { "No description available" }

            Glide.with(itemView.context)
                .load(ApiConfig.getPosterUrl(movie.posterPath))
                .transform(RoundedCorners(12))
                .placeholder(R.color.cardBackground)
                .error(R.color.cardBackground)
                .into(poster)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra("MOVIE_ID", movie.id)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie_list, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount() = movies.size
}