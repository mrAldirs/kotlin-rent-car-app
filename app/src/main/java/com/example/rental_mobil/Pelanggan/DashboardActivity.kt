package com.example.rental_mobil.Pelanggan

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.rental_mobil.MainActivity
import com.example.rental_mobil.R
import com.example.rental_mobil.View.Pelanggan.ProfilActivity
import com.example.rental_mobil.View.Pelanggan.RiwayatActivity
import com.example.rental_mobil.databinding.ActivityDashboardPelangganBinding
import com.example.rental_mobil.databinding.NavHeaderBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class DashboardActivity : AppCompatActivity() {
    private lateinit var b: ActivityDashboardPelangganBinding
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var hb: NavHeaderBinding

    lateinit var preferences: SharedPreferences
    val PREF_NAMA = "akun"
    val EMAIL = "email"
    val DEF_EMAIL = ""
    val ID = "id_pelanggan"
    val DEF_ID = ""

    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDashboardPelangganBinding.inflate(layoutInflater)
        hb = NavHeaderBinding.bind(b.navView.getHeaderView(0))
        setContentView(b.root)
        supportActionBar?.setTitle("Rental Mobil Wijaya")

        preferences = getSharedPreferences(PREF_NAMA, Context.MODE_PRIVATE)
        auth = Firebase.auth

        toggle = ActionBarDrawerToggle(this, b.drawerLayout, R.string.open, R.string.close)
        b.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        b.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_homePelanggan -> {
                    b.drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_profilAdmin -> {
                    startActivity(Intent(this, ProfilActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
                }
                R.id.nav_mobilPelanggan -> {
                    startActivity(Intent(this, MobilActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
                }
                R.id.nav_rentalPelanggan -> {
                    startActivity(Intent(this, BookingActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
                }
                R.id.nav_riwayatPelanggan -> {
                    startActivity(Intent(this, RiwayatActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
                }
                R.id.nav_logoutPelanggan -> {
                    AlertDialog.Builder(this)
                        .setIcon(R.drawable.warning)
                        .setTitle("Logout")
                        .setMessage("Apakah Anda ingin keluar aplikasi?")
                        .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                            val prefEditor = preferences.edit()
                            prefEditor.putString(EMAIL,null)
                            prefEditor.commit()

                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            auth.signOut()
                            overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
                            finishAffinity()
                        })
                        .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
                        })
                        .show()
                    true
                }
            }
            true
        }

        b.btnBooking.setOnClickListener {
            startActivity(Intent(this, BookingActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }

        b.btnMobil.setOnClickListener {
            startActivity(Intent(this, MobilActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }

        b.btnRiwayat.setOnClickListener {
            startActivity(Intent(this, RiwayatActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }

        b.btnProfil.setOnClickListener {
            startActivity(Intent(this, ProfilActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
    }

    override fun onStart() {
        super.onStart()
        showProfil()
    }

    override fun onBackPressed() {
        if (b.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            b.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun showProfil() {
        FirebaseFirestore.getInstance().collection("pelanggan")
            .document(preferences.getString(EMAIL, DEF_EMAIL).toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val st1 = document.get("foto_profil").toString()
                    val st2 = document.get("nama").toString()
                    val st3 = document.get("hp").toString()

                    Picasso.get().load(st1).into(hb.profilHeader)
                    hb.usernameHeader.setText(st2)
                    hb.subHeader.setText(st3)
                } else {
                    // Dokumen tidak ditemukan
                }
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }
}