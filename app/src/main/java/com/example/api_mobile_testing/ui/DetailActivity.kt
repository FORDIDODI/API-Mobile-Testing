package com.example.api_mobile_testing.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.api_mobile_testing.R
import com.example.api_mobile_testing.adapter.CastAdapter
import com.example.api_mobile_testing.adapter.ReviewAdapter
import com.example.api_mobile_testing.api.ApiConfig
import com.example.api_mobile_testing.model.MovieDetail
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var ivBackdrop: ImageView
    private lateinit var ivPoster: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvTagline: TextView
    private lateinit var tvRating: TextView
    private lateinit var tvVoteCount: TextView
    private lateinit var tvReleaseDate: TextView
    private lateinit var tvRuntime: TextView
    private lateinit var tvGenres: TextView
    private lateinit var tvOverview: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvBudget: TextView
    private lateinit var tvRevenue: TextView
    private lateinit var tvLanguages: TextView
    private lateinit var tvCountries: TextView
    private lateinit var tvCompanies: TextView
    private lateinit var rvCast: RecyclerView
    private lateinit var rvReviews: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvNoReviews: TextView

    private var movieId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)

        movieId = intent.getIntExtra("MOVIE_ID", 0)

        initViews()
        setupToolbar()
        setupRecyclerViews()
        loadMovieDetails()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        ivBackdrop = findViewById(R.id.ivBackdrop)
        ivPoster = findViewById(R.id.ivPoster)
        tvTitle = findViewById(R.id.tvTitle)
        tvTagline = findViewById(R.id.tvTagline)
        tvRating = findViewById(R.id.tvRating)
        tvVoteCount = findViewById(R.id.tvVoteCount)
        tvReleaseDate = findViewById(R.id.tvReleaseDate)
        tvRuntime = findViewById(R.id.tvRuntime)
        tvGenres = findViewById(R.id.tvGenres)
        tvOverview = findViewById(R.id.tvOverview)
        tvStatus = findViewById(R.id.tvStatus)
        tvBudget = findViewById(R.id.tvBudget)
        tvRevenue = findViewById(R.id.tvRevenue)
        tvLanguages = findViewById(R.id.tvLanguages)
        tvCountries = findViewById(R.id.tvCountries)
        tvCompanies = findViewById(R.id.tvCompanies)
        rvCast = findViewById(R.id.rvCast)
        rvReviews = findViewById(R.id.rvReviews)
        progressBar = findViewById(R.id.progressBar)
        tvNoReviews = findViewById(R.id.tvNoReviews)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerViews() {
        rvCast.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvReviews.layoutManager = LinearLayoutManager(this)
    }

    private fun loadMovieDetails() {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                // Load Movie Detail
                val detailResponse = ApiConfig.apiService.getMovieDetail(movieId, ApiConfig.API_KEY)
                if (detailResponse.isSuccessful) {
                    detailResponse.body()?.let { movie ->
                        displayMovieDetails(movie)
                    }
                }

                // Load Cast
                val creditsResponse = ApiConfig.apiService.getMovieCredits(movieId, ApiConfig.API_KEY)
                if (creditsResponse.isSuccessful) {
                    creditsResponse.body()?.let {
                        if (it.cast.isNotEmpty()) {
                            rvCast.adapter = CastAdapter(it.cast)
                        }
                    }
                }

                // Load Reviews
                val reviewsResponse = ApiConfig.apiService.getMovieReviews(movieId, ApiConfig.API_KEY)
                if (reviewsResponse.isSuccessful) {
                    reviewsResponse.body()?.let {
                        if (it.results.isNotEmpty()) {
                            rvReviews.adapter = ReviewAdapter(it.results)
                            tvNoReviews.visibility = View.GONE
                        } else {
                            tvNoReviews.visibility = View.VISIBLE
                        }
                    }
                }

            } catch (e: Exception) {
                Toast.makeText(this@DetailActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun displayMovieDetails(movie: MovieDetail) {
        Glide.with(this)
            .load(ApiConfig.getBackdropUrl(movie.backdropPath))
            .into(ivBackdrop)

        Glide.with(this)
            .load(ApiConfig.getPosterUrl(movie.posterPath))
            .into(ivPoster)

        tvTitle.text = movie.title

        if (!movie.tagline.isNullOrEmpty()) {
            tvTagline.text = "\"${movie.tagline}\""
            tvTagline.visibility = View.VISIBLE
        } else {
            tvTagline.visibility = View.GONE
        }

        tvRating.text = String.format("★ %.1f", movie.voteAverage)
        tvVoteCount.text = "${NumberFormat.getNumberInstance(Locale.US).format(movie.voteCount)} votes"
        tvReleaseDate.text = movie.releaseDate

        if (movie.runtime != null) {
            val hours = movie.runtime / 60
            val minutes = movie.runtime % 60
            tvRuntime.text = "${hours}h ${minutes}m"
        }

        tvGenres.text = movie.genres.joinToString(", ") { it.name }
        tvOverview.text = movie.overview
        tvStatus.text = movie.status

        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
        tvBudget.text = if (movie.budget > 0) {
            currencyFormat.format(movie.budget)
        } else {
            "N/A"
        }

        tvRevenue.text = if (movie.revenue > 0) {
            currencyFormat.format(movie.revenue)
        } else {
            "N/A"
        }

        tvLanguages.text = movie.spokenLanguages.joinToString(", ") { it.englishName }
        tvCountries.text = movie.productionCountries.joinToString(", ") { it.name }
        tvCompanies.text = movie.productionCompanies.joinToString(", ") { it.name }
    }
}