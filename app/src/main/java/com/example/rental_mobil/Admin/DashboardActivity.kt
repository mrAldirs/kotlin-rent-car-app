package com.example.rental_mobil.Admin

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.rental_mobil.MainActivity
import com.example.rental_mobil.R
import com.example.rental_mobil.View.Admin.Mobil.MobilActivity
import com.example.rental_mobil.databinding.ActivityDashboardAdminBinding
import com.example.rental_mobil.databinding.NavHeaderBinding
import com.google.firebase.firestore.FirebaseFirestore

class DashboardActivity : AppCompatActivity() {
    private lateinit var b: ActivityDashboardAdminBinding
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var hb: NavHeaderBinding

    val db = FirebaseFirestore.getInstance()
    val refMobil = db.collection("mobil")
    val refPela = db.collection("pelanggan")
    val refSopir = db.collection("sopir")
    val refRental = db.collection("rental")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDashboardAdminBinding.inflate(layoutInflater)
        hb = NavHeaderBinding.bind(b.navView.getHeaderView(0))
        setContentView(b.root)
        supportActionBar?.setTitle("Dashboard Admin")

        toggle = ActionBarDrawerToggle(this, b.drawerLayout, R.string.open, R.string.close)
        b.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        b.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_homeAdmin -> {
                    b.drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_pelangganAdmin -> {
                    startActivity(Intent(this, PelangganActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
                }
                R.id.nav_sopirAdmin -> {
                    startActivity(Intent(this, SopirActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
                }
                R.id.nav_kategoriAdmin -> {
                    startActivity(Intent(this, KategoriActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
                }
                R.id.nav_mobilAdmin -> {
                    startActivity(Intent(this, MobilActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
                }
                R.id.nav_rentalAdmin -> {
                    startActivity(Intent(this, RentalActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
                }
                R.id.nav_laporanAdmin -> {
                    startActivity(Intent(this, LaporanActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
                }
                R.id.nav_logoutAdmin -> {
                    AlertDialog.Builder(this)
                        .setIcon(R.drawable.warning)
                        .setTitle("Logout")
                        .setMessage("Apakah Anda ingin keluar aplikasi?")
                        .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
//                            val prefEditor = preferences.edit()
//                            prefEditor.putString(USER,null)
//                            prefEditor.putString(NAMA,null)
//                            prefEditor.commit()

                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
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
    }

    override fun onStart() {
        super.onStart()
        count()
        countRental()
    }

    fun count() {
        refMobil
            .whereEqualTo("deleted", "")
            .get()
            .addOnSuccessListener { result ->
                val count = result.size()
                b.tvMobil.setText(count.toString())

                refPela
                    .whereEqualTo("deleted", "")
                    .get()
                    .addOnSuccessListener { result ->
                        val count = result.size()
                        b.tvPelanggan.setText(count.toString())

                        refSopir
                            .whereEqualTo("deleted", "")
                            .get()
                            .addOnSuccessListener { result ->
                                val count = result.size()
                                b.tvSopir.setText(count.toString())
                            }
                    }
            }
    }

    fun countRental() {
        refRental
            .whereEqualTo("status_rental", "Booking")
            .get()
            .addOnSuccessListener { result ->
                val count = result.size()
                b.tvBooking.setText(count.toString())

                refRental
                    .whereEqualTo("status_rental", "Berjalan")
                    .get()
                    .addOnSuccessListener { result ->
                        val count = result.size()
                        b.tvBerjalan.setText(count.toString())

                        refRental
                            .whereEqualTo("status_rental", "Selesai")
                            .get()
                            .addOnSuccessListener { result ->
                                val count = result.size()
                                b.tvKembali.setText(count.toString())

                            }
                    }
            }
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
}