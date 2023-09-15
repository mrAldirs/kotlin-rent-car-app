package com.example.rental_mobil

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rental_mobil.databinding.ActivitySignupSelesaiBinding

class SignUpSelesaiActivity : AppCompatActivity() {
    private lateinit var b: ActivitySignupSelesaiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySignupSelesaiBinding.inflate(layoutInflater)
        setContentView(b.root)
        supportActionBar?.setTitle("Sign Up Berhasil")

        b.btnSelesai.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }
}