package com.example.rental_mobil.Admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rental_mobil.R
import com.example.rental_mobil.databinding.ActivityRentalAdminBinding

class RentalActivity : AppCompatActivity() {
    private lateinit var b: ActivityRentalAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRentalAdminBinding.inflate(layoutInflater)
        setContentView(b.root)
        supportActionBar?.setTitle("Rental Mobil")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        b.btnBooking.setOnClickListener {
            startActivity(Intent(this, RentalBookingActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }

        b.btnBerjalan.setOnClickListener {
            startActivity(Intent(this, RentalBerjalanActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }

        b.btnRiwayat.setOnClickListener {
            startActivity(Intent(this, RentalRiwayatActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
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