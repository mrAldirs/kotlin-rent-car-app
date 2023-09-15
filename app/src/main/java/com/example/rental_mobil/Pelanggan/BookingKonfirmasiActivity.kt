package com.example.rental_mobil.Pelanggan

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.rental_mobil.R
import com.example.rental_mobil.databinding.ActivityBookingKonfirmasiBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

class BookingKonfirmasiActivity : AppCompatActivity() {
    private lateinit var b: ActivityBookingKonfirmasiBinding

    lateinit var preferences: SharedPreferences
    val PREF_NAMA = "akun"
    val EMAIL = "email"
    val DEF_EMAIL = ""
    val ID = "id_pelanggan"
    val DEF_ID = ""

    var idM = ""
    var idS = ""
    var totalSopir = ""
    var totalMobil = ""
    var hr = ""

    var hrgM = 0
    var tot = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityBookingKonfirmasiBinding.inflate(layoutInflater)
        setContentView(b.root)
        supportActionBar?.setTitle("Konfirmasi Mobil")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        preferences = getSharedPreferences(PREF_NAMA, Context.MODE_PRIVATE)

        var paket : Bundle? = intent.extras
        idS = paket?.getString("idS").toString()
        hr = paket?.getString("hari").toString()
        b.bookingHari.setText(paket?.getString("hari").toString()+" Hari")
        b.bookingTglMulai.setText(paket?.getString("tgl_mulai").toString())
        b.bookingTglSelesai.setText(paket?.getString("tgl_selesai").toString())
        b.bookingTgl.setText(paket?.getString("tgl_rental").toString())

        detailMobil()
        detailPelanggan()

        if (idS.equals("")) {
            b.cardSopir.visibility = View.GONE
            b.tvSopir.visibility = View.GONE
            b.cdBiayaSopir.visibility = View.GONE
        } else {
            profilSopir()
        }

        val hari = hr.toInt()
        val sopir = 50000
        val result_sopir = hari * sopir
        b.bookingBiayaSopir.setText("Rp."+result_sopir)
        totalSopir = result_sopir.toString()
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

    fun detailPelanggan() {
        FirebaseFirestore.getInstance().collection("pelanggan")
            .document(preferences.getString(EMAIL, DEF_EMAIL).toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val st1 = document.get("foto_profil").toString()
                    val st2 = document.get("nama").toString()
                    val st3 = document.get("hp").toString()

                    Picasso.get().load(st1).into(b.detailFotoPelanggan)
                    b.detailPelanggan.setText(st2)
                    b.detailNoHpPelanggan.setText(st3)
                }
            }
    }

    fun detailMobil() {
        var paket : Bundle? = intent.extras
        FirebaseFirestore.getInstance().collection("mobil")
            .document(paket?.getString("idM").toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val st1 = document.get("foto_mobil").toString()
                    val st2 = document.get("nama").toString()
                    val st3 = document.get("kategori").toString()
                    val st4 = document.get("harga").toString()

                    hrgM = st4.toInt()
                    Picasso.get().load(st1).into(b.detailFotoMobil)
                    b.bookingMobil.setText(st2)
                    b.bookingKategori.setText(st3)
                    b.bookingHarga.setText("Biaya Rental Rp."+st4)

                    val hr = hr.toInt()
                    val hrgMobil = hr * hrgM
                    b.bookingHargaRental.setText("Rp."+hrgMobil)
                    totalMobil = hrgMobil.toString()

                    if (idS == "") {
                        b.bookingTotal.setText("Rp."+totalMobil)

                        b.btnKonfirmasiRental.setOnClickListener {
                            val randomId = UUID.randomUUID().toString()
                            val hm = HashMap<String, Any>()
                            hm.set("id_rental", randomId)
                            hm.set("biaya_sopir", "")
                            hm.set("bukti_pembayaran", "")
                            hm.set("hari_rental", paket?.getString("hari").toString())
                            hm.set("id_pelanggan", preferences.getString(ID, DEF_ID).toString())
                            hm.set("id_mobil", paket?.getString("idM").toString())
                            hm.set("id_sopir", "")
                            hm.set("keterangan", "")
                            hm.set("mulai_rental", paket?.getString("tgl_mulai").toString())
                            hm.set("selesai_rental", paket?.getString("tgl_selesai").toString())
                            hm.set("status_pembayaran", "Belum Bayar")
                            hm.set("status_rental", "Booking")
                            hm.set("tgl_rental", paket?.getString("tgl_rental").toString())
                            hm.set("total", totalMobil)
                            hm.set("waktu_denda", "")
                            hm.set("deleted", "")
                            hm.set("created", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))
                            hm.set("updated", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))

                            FirebaseFirestore.getInstance().collection("rental")
                                .document(randomId)
                                .set(hm)
                                .addOnSuccessListener {
                                    val hmU = HashMap<String, Any>()
                                    hmU.set("status", "tidak tersedia")
                                    hmU.set("updated", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))

                                    FirebaseFirestore.getInstance().collection("mobil")
                                        .document(paket?.getString("idM").toString())
                                        .update(hmU)
                                        .addOnSuccessListener {
                                            val frag = BookingFinishFragment()

                                            frag.show(supportFragmentManager, "BookingFinishFragment")
                                            Toast.makeText(this, "Berhasil booking mobil pada tanggal "+paket?.getString("tgl_rental").toString(), Toast.LENGTH_SHORT).show()
                                        }
                                }
                        }
                    } else {
                        val totalAkhir = totalSopir.toInt() + totalMobil.toInt()
                        b.bookingTotal.setText("Rp."+totalAkhir.toString())

                        b.btnKonfirmasiRental.setOnClickListener {
                            val randomId = UUID.randomUUID().toString()
                            val hm = HashMap<String, Any>()
                            hm.set("id_rental", randomId)
                            hm.set("biaya_sopir", totalSopir)
                            hm.set("bukti_pembayaran", "")
                            hm.set("hari_rental", paket?.getString("hari").toString())
                            hm.set("id_pelanggan", preferences.getString(ID, DEF_ID).toString())
                            hm.set("id_sopir", paket?.getString("idS").toString())
                            hm.set("id_mobil", paket?.getString("idM").toString())
                            hm.set("keterangan", "")
                            hm.set("mulai_rental", paket?.getString("tgl_mulai").toString())
                            hm.set("selesai_rental", paket?.getString("tgl_selesai").toString())
                            hm.set("status_pembayaran", "Belum Bayar")
                            hm.set("status_rental", "Booking")
                            hm.set("tgl_rental", paket?.getString("tgl_rental").toString())
                            hm.set("total", totalAkhir.toString())
                            hm.set("waktu_denda", "")
                            hm.set("deleted", "")
                            hm.set("created", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))
                            hm.set("updated", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))

                            FirebaseFirestore.getInstance().collection("rental")
                                .document(randomId)
                                .set(hm)
                                .addOnSuccessListener {
                                    val hmU = HashMap<String, Any>()
                                    hmU.set("status", "tidak tersedia")
                                    hmU.set("updated", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))

                                    FirebaseFirestore.getInstance().collection("mobil")
                                        .document(paket?.getString("idM").toString())
                                        .update(hmU)
                                        .addOnSuccessListener {
                                            val hmS = HashMap<String, Any>()
                                            hmS.set("status", "tidak tersedia")
                                            hmS.set("updated", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))

                                            FirebaseFirestore.getInstance().collection("sopir")
                                                .document(paket?.getString("idS").toString())
                                                .update(hmS)
                                                .addOnSuccessListener {
                                                    val frag = BookingFinishFragment()

                                                    frag.show(supportFragmentManager, "BookingFinishFragment")
                                                    Toast.makeText(this, "Berhasil booking mobil pada tanggal "+paket?.getString("tgl_rental").toString(), Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                }
                        }
                    }
                } else {
                    Toast.makeText(this, "Mobil tidak ditemukan!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun profilSopir() {
        var paket : Bundle? = intent.extras
        FirebaseFirestore.getInstance().collection("sopir")
            .document(paket?.getString("idS").toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val st1 = document.get("foto_profil").toString()
                    val st2 = document.get("nama").toString()
                    val st3 = document.get("hp").toString()

                    Picasso.get().load(st1).into(b.detailFotoSopir)
                    b.bookingNamaSopir.setText(st2)
                    b.bookingHpSopir.setText(st3)
                }
            }
    }
}