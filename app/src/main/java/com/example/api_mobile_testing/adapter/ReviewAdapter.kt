package com.example.api_mobile_testing.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.api_mobile_testing.R
import com.example.api_mobile_testing.model.Review

class ReviewAdapter(private val reviews: List<Review>) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val author: TextView = view.findViewById(R.id.tvAuthor)
        val rating: TextView = view.findViewById(R.id.tvReviewRating)
        val content: TextView = view.findViewById(R.id.tvContent)
        val date: TextView = view.findViewById(R.id.tvDate)

        fun bind(review: Review) {
            author.text = review.author

            if (review.authorDetails.rating != null) {
                rating.text = String.format("★ %.1f", review.authorDetails.rating)
                rating.visibility = View.VISIBLE
            } else {
                rating.visibility = View.GONE
            }

            content.text = review.content
            date.text = review.createdAt.substring(0, 10)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    override fun getItemCount() = reviews.size
}