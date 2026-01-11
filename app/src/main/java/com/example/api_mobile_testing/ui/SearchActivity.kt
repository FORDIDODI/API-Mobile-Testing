package com.example.api_mobile_testing.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.api_mobile_testing.R
import com.example.api_mobile_testing.adapter.MovieListAdapter
import com.example.api_mobile_testing.api.ApiConfig
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var ivBack: ImageView
    private lateinit var etSearch: EditText
    private lateinit var ivClear: ImageView
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvNoResults: TextView
    private lateinit var resultsSection: LinearLayout
    private lateinit var initialState: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        initViews()
        setupRecyclerView()
        setupListeners()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initViews() {
        ivBack = findViewById(R.id.ivBack)
        etSearch = findViewById(R.id.etSearch)
        ivClear = findViewById(R.id.ivClear)
        rvSearchResults = findViewById(R.id.rvSearchResults)
        progressBar = findViewById(R.id.progressBar)
        tvNoResults = findViewById(R.id.tvNoResults)
        resultsSection = findViewById(R.id.resultsSection)
        initialState = findViewById(R.id.initialState)
    }

    private fun setupRecyclerView() {
        rvSearchResults.layoutManager = LinearLayoutManager(this)
    }

    private fun setupListeners() {
        ivBack.setOnClickListener {
            finish()
        }

        ivClear.setOnClickListener {
            etSearch.text.clear()
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                ivClear.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        etSearch.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                val query = etSearch.text.toString().trim()
                if (query.isNotEmpty()) {
                    searchMovies(query)
                }
                return@setOnEditorActionListener true
            }
            false
        }

        // Popular searches
        findViewById<TextView>(R.id.tvPopular1).setOnClickListener {
            etSearch.setText("Action")
            searchMovies("Action")
        }

        findViewById<TextView>(R.id.tvPopular2).setOnClickListener {
            etSearch.setText("Comedy")
            searchMovies("Comedy")
        }

        findViewById<TextView>(R.id.tvPopular3).setOnClickListener {
            etSearch.setText("Horror")
            searchMovies("Horror")
        }

        findViewById<TextView>(R.id.tvPopular4).setOnClickListener {
            etSearch.setText("Sci-Fi")
            searchMovies("Sci-Fi")
        }
    }

    private fun searchMovies(query: String) {
        progressBar.visibility = View.VISIBLE
        tvNoResults.visibility = View.GONE
        initialState.visibility = View.GONE
        resultsSection.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = ApiConfig.apiService.searchMovies(ApiConfig.API_KEY, query)
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.results.isNotEmpty()) {
                            rvSearchResults.adapter = MovieListAdapter(it.results)
                            resultsSection.visibility = View.VISIBLE
                        } else {
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