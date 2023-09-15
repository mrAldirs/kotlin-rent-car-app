package com.example.rental_mobil.Pelanggan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rental_mobil.R
import com.example.rental_mobil.databinding.ActivityContactBinding

class ContactActivity : AppCompatActivity() {
    private lateinit var b: ActivityContactBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityContactBinding.inflate(layoutInflater)
        setContentView(b.root)
        supportActionBar?.setTitle("Contact")
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