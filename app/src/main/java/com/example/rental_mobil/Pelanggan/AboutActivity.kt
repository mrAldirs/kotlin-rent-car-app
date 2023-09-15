package com.example.rental_mobil.Pelanggan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rental_mobil.R
import com.example.rental_mobil.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var b: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(b.root)
        supportActionBar?.setTitle("About")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
}