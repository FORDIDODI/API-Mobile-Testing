package com.example.api_mobile_testing.ui

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.api_mobile_testing.R
import com.example.api_mobile_testing.adapter.MovieAdapter
import com.example.api_mobile_testing.api.ApiConfig
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var searchView: SearchView
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvNoResults: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        initViews()
        setupToolbar()
        setupSearchView()
        setupRecyclerView()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        searchView = findViewById(R.id.searchView)
        rvSearchResults = findViewById(R.id.rvSearchResults)
        progressBar = findViewById(R.id.progressBar)
        tvNoResults = findViewById(R.id.tvNoResults)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Search Movies"
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchMovies(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    rvSearchResults.adapter = null
                    tvNoResults.visibility = View.GONE
                }
                return true
            }
        })
    }

    private fun setupRecyclerView() {
        rvSearchResults.layoutManager = GridLayoutManager(this, 2)
    }

    private fun searchMovies(query: String) {
        progressBar.visibility = View.VISIBLE
        tvNoResults.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = ApiConfig.apiService.searchMovies(ApiConfig.API_KEY, query)
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.results.isNotEmpty()) {
                            rvSearchResults.adapter = MovieAdapter(it.results)
                        } else {
                            rvSearchResults.adapter = null
                            tvNoResults.visibility = View.VISIBLE
                        }
                    }
                }
            } catch (e: Exception) {
                tvNoResults.text = "Error: ${e.message}"
                tvNoResults.visibility = View.VISIBLE
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
}