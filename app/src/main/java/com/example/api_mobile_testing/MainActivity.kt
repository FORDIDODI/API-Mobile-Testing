package com.example.api_mobile_testing

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.api_mobile_testing.adapter.BannerAdapter
import com.example.api_mobile_testing.adapter.MovieAdapter
import com.example.api_mobile_testing.api.ApiConfig
import com.example.api_mobile_testing.model.Movie
import com.example.api_mobile_testing.ui.DetailActivity
import com.example.api_mobile_testing.ui.SearchActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var viewPagerBanner: ViewPager2
    private lateinit var dotsIndicator: LinearLayout
    private lateinit var tvBannerTitle: TextView
    private lateinit var tvBannerRating: TextView
    private lateinit var tvBannerYear: TextView
    private lateinit var ivSearch: ImageView
    private lateinit var ivProfile: ImageView
    private lateinit var rvNowPlaying: RecyclerView
    private lateinit var rvUpcoming: RecyclerView
    private lateinit var rvTopRated: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar

    private var popularMovies: List<Movie> = emptyList()
    private var currentBannerPosition = 0
    private val handler = Handler(Looper.getMainLooper())
    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            if (popularMovies.isNotEmpty()) {
                currentBannerPosition = (currentBannerPosition + 1) % popularMovies.size.coerceAtMost(5)
                viewPagerBanner.setCurrentItem(currentBannerPosition, true)
                handler.postDelayed(this, 4000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerViews()
        setupSwipeRefresh()
        setupListeners()
        loadMovies()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initViews() {
        viewPagerBanner = findViewById(R.id.viewPagerBanner)
        dotsIndicator = findViewById(R.id.dotsIndicator)
        tvBannerTitle = findViewById(R.id.tvBannerTitle)
        tvBannerRating = findViewById(R.id.tvBannerRating)
        tvBannerYear = findViewById(R.id.tvBannerYear)
        ivSearch = findViewById(R.id.ivSearch)
        ivProfile = findViewById(R.id.ivProfile)
        rvNowPlaying = findViewById(R.id.rvNowPlaying)
        rvUpcoming = findViewById(R.id.rvUpcoming)
        rvTopRated = findViewById(R.id.rvTopRated)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupRecyclerViews() {
        rvNowPlaying.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvUpcoming.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvTopRated.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener {
            loadMovies()
        }
        swipeRefresh.setColorSchemeResources(
            R.color.primary,
            R.color.secondary,
            R.color.accent
        )
    }

    private fun setupListeners() {
        ivSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        ivProfile.setOnClickListener {
            Toast.makeText(this, "Profile feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadMovies() {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                // Load Popular Movies for Banner
                val popularResponse = ApiConfig.apiService.getPopularMovies(ApiConfig.API_KEY)
                if (popularResponse.isSuccessful) {
                    popularResponse.body()?.let {
                        popularMovies = it.results
                        setupBanner(popularMovies)
                    }
                }

                // Load Now Playing Movies
                val nowPlayingResponse = ApiConfig.apiService.getNowPlayingMovies(ApiConfig.API_KEY)
                if (nowPlayingResponse.isSuccessful) {
                    nowPlayingResponse.body()?.let {
                        rvNowPlaying.adapter = MovieAdapter(it.results)
                    }
                }

                // Load Upcoming Movies
                val upcomingResponse = ApiConfig.apiService.getUpcomingMovies(ApiConfig.API_KEY)
                if (upcomingResponse.isSuccessful) {
                    upcomingResponse.body()?.let {
                        rvUpcoming.adapter = MovieAdapter(it.results)
                    }
                }

                // Load Top Rated Movies
                val topRatedResponse = ApiConfig.apiService.getTopRatedMovies(ApiConfig.API_KEY)
                if (topRatedResponse.isSuccessful) {
                    topRatedResponse.body()?.let {
                        rvTopRated.adapter = MovieAdapter(it.results)
                    }
                }

            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
                swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun setupBanner(movies: List<Movie>) {
        val bannerAdapter = BannerAdapter(movies) { movie ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("MOVIE_ID", movie.id)
            startActivity(intent)
        }

        viewPagerBanner.adapter = bannerAdapter

        // Setup dot indicators
        setupDotsIndicator(movies.size.coerceAtMost(5))
        setCurrentDot(0)

        // Update banner info on page change
        viewPagerBanner.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentBannerPosition = position
                setCurrentDot(position)
                updateBannerInfo(movies[position])
            }
        })

        // Show first movie info
        if (movies.isNotEmpty()) {
            updateBannerInfo(movies[0])
            startAutoScroll()
        }
    }

    private fun setupDotsIndicator(count: Int) {
        dotsIndicator.removeAllViews()
        val dots = Array(count) { ImageView(this) }

        for (i in dots.indices) {
            dots[i] = ImageView(this)
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_inactive))

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)

            dotsIndicator.addView(dots[i], params)
        }
    }

    private fun setCurrentDot(position: Int) {
        val childCount = dotsIndicator.childCount
        for (i in 0 until childCount) {
            val imageView = dotsIndicator.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_active))
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_inactive))
            }
        }
    }

    private fun updateBannerInfo(movie: Movie) {
        tvBannerTitle.text = movie.title
        tvBannerRating.text = String.format("★ %.1f", movie.voteAverage)
        tvBannerYear.text = if (movie.releaseDate.isNotEmpty()) {
            movie.releaseDate.substring(0, 4)
        } else {
            "N/A"
        }
    }

    private fun startAutoScroll() {
        handler.postDelayed(autoScrollRunnable, 4000)
    }

    override fun onResume() {
        super.onResume()
        startAutoScroll()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(autoScrollRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(autoScrollRunnable)
    }
}