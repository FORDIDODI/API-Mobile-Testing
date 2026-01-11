package com.example.api_mobile_testing.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.api_mobile_testing.R
import com.example.api_mobile_testing.api.ApiConfig
import com.example.api_mobile_testing.model.Movie

class BannerAdapter(
    private val movies: List<Movie>,
    private val onBannerClick: (Movie) -> Unit
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    inner class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val poster: ImageView = view.findViewById(R.id.ivBannerPoster)

        fun bind(movie: Movie) {
            Glide.with(itemView.context)
                .load(ApiConfig.getBackdropUrl(movie.backdropPath))
                .placeholder(R.color.cardBackground)
                .error(R.color.cardBackground)
                .into(poster)

            itemView.setOnClickListener {
                onBannerClick(movie)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_banner, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount() = movies.size.coerceAtMost(5) // Max 5 banners
}