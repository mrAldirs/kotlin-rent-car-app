package com.example.rental_mobil.View.Pelanggan

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.rental_mobil.Admin.PelangganActivity
import com.example.rental_mobil.R
import com.example.rental_mobil.ViewModel.ProfilViewModel
import com.example.rental_mobil.databinding.ActivityProfilBinding
import com.squareup.picasso.Picasso

class ProfilActivity : AppCompatActivity() {
    private lateinit var b: ActivityProfilBinding
    private lateinit var pVM: ProfilViewModel

    lateinit var preferences: SharedPreferences
    val PREF_NAMA = "akun"
    val EMAIL = "email"
    val DEF_EMAIL = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityProfilBinding.inflate(layoutInflater)
        setContentView(b.root)
        supportActionBar?.setTitle("Profil")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        preferences = getSharedPreferences(PREF_NAMA, Context.MODE_PRIVATE)
        pVM = ViewModelProvider(this).get(ProfilViewModel::class.java)

        b.btnEdit.setOnClickListener {
            startActivity(Intent(this, ProfilEditActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
        return true
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    private fun loadData() {
        pVM.profil(preferences.getString(EMAIL, DEF_EMAIL).toString()).observe(this, Observer { profil ->
            b.profilNama.setText(profil.nama)
            b.profilAlamat.setText(profil.alamat)
            b.profilEmail.setText(profil.email)
            b.profilNik.setText(profil.nik)
            b.profilTtl.setText(profil.ttl)
            b.profilNohp.setText(profil.hp)
            Picasso.get().load(profil.foto_profil).into(b.profilFoto)
            Picasso.get().load(profil.foto_ktp).into(b.profilKtp)
        })
    }
}