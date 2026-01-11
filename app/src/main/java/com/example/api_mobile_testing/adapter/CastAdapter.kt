package com.example.api_mobile_testing.adapter

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
import com.example.api_mobile_testing.model.Cast

class CastAdapter(private val castList: List<Cast>) :
    RecyclerView.Adapter<CastAdapter.CastViewHolder>() {

    inner class CastViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profile: ImageView = view.findViewById(R.id.ivProfile)
        val name: TextView = view.findViewById(R.id.tvName)
        val character: TextView = view.findViewById(R.id.tvCharacter)

        fun bind(cast: Cast) {
            name.text = cast.name
            character.text = cast.character

            Glide.with(itemView.context)
                .load(ApiConfig.getProfileUrl(cast.profilePath))
                .transform(RoundedCorners(16))
                .placeholder(R.color.cardBackground)
                .error(R.color.cardBackground)
                .into(profile)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cast, parent, false)
        return CastViewHolder(view)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        holder.bind(castList[position])
    }

    override fun getItemCount() = castList.size.coerceAtMost(10)
}