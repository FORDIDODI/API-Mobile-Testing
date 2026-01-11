package com.example.api_mobile_testing.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.api_mobile_testing.MainActivity
import com.example.api_mobile_testing.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    // Data Placeholder - Ganti dengan data Anda
    private val userName = "John Doe"
    private val userEmail = "johndoe@example.com"
    private val userPhone = "+62 812-3456-7890"
    private val userLocation = "Jakarta, Indonesia"
    private val userBio = "Movie enthusiast and tech lover. Always looking for the next great film to watch!"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        setupBottomNav()
        loadProfileData()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }

    private fun setupBottomNav() {
        bottomNav = findViewById(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_profile

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> true
                else -> false
            }
        }
    }

    private fun loadProfileData() {
        findViewById<TextView>(R.id.tvProfileName).text = userName
        findViewById<TextView>(R.id.tvProfileEmail).text = userEmail
        findViewById<TextView>(R.id.tvFullName).text = userName
        findViewById<TextView>(R.id.tvEmail).text = userEmail
        findViewById<TextView>(R.id.tvPhone).text = userPhone
        findViewById<TextView>(R.id.tvLocation).text = userLocation
        findViewById<TextView>(R.id.tvBio).text = userBio
    }
}