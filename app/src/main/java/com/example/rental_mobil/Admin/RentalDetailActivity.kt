package com.example.rental_mobil.Admin

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.rental_mobil.ImageDetailActivity
import com.example.rental_mobil.Model.Mobil
import com.example.rental_mobil.Model.Pelanggan
import com.example.rental_mobil.Model.Riwayat
import com.example.rental_mobil.R
import com.example.rental_mobil.View.Pelanggan.PdfActivity
import com.example.rental_mobil.ViewModel.RiwayatViewModel
import com.example.rental_mobil.databinding.ActivityRentalAdminDetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.io.Serializable
import java.util.HashMap

class RentalDetailActivity : AppCompatActivity() {
    private lateinit var b: ActivityRentalAdminDetailBinding

    var idS = ""
    var idM = ""
    var stsB = ""
    var imgUrl = ""
    private lateinit var mobil: Mobil
    private lateinit var pelanggan: Pelanggan
    private lateinit var riwayat: Riwayat
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRentalAdminDetailBinding.inflate(layoutInflater)
        setContentView(b.root)
        supportActionBar?.setTitle("Detail Rentail Mobil")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        detailRental()
        detailPelanggan()
        detailMobil()

        b.btnKonfirmasiRental.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Peringatan!")
                .setIcon(R.drawable.warning)
                .setMessage("Apakah Anda ingin mengonfirmasi bahwa mobil sudah berjalan?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    var paket : Bundle? = intent.extras
                    val dialog = RentalKonfirmasiFragment()

                    val bundle = Bundle()
                    bundle.putString("id", paket?.getString("idR").toString())
                    dialog.arguments = bundle

                    dialog.show(supportFragmentManager, "RentalKonfirmasiFragment")
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                .show()
        }

        b.btnBatalkanRental.setOnClickListener {
            batalkan()
        }

        b.btnDetailPembayaran.setOnClickListener {
            val intent = Intent(this, ImageDetailActivity::class.java)
            intent.putExtra("img", imgUrl)
            startActivity(intent)
        }


//        b.btnKonfirmasiPembayaranRental.setOnClickListener {
//            AlertDialog.Builder(this)
//                .setTitle("Peringatan!")
//                .setIcon(R.drawable.warning)
//                .setMessage("Apakah Anda ingin mengonfirmasi pembayaran rental mobil ini?")
//                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
//                    var paket : Bundle? = intent.extras
//                    val dialog = RentalPembayaranFragment()
//
//                    val bundle = Bundle()
//                    bundle.putString("id", paket?.getString("idR").toString())
//                    dialog.arguments = bundle
//
//                    dialog.show(supportFragmentManager, "RentalPembayaranFragment")
//                })
//                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
//
//                })
//                .show()
//        }



        b.btnKonfirmasiPengembalian.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Peringatan!")
                .setIcon(R.drawable.warning)
                .setMessage("Apakah Anda ingin mengonfirmasi mobil sudah kembali?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    var paket : Bundle? = intent.extras
                    val dialog = RentalKembaliFragment()

                    val bundle = Bundle()
                    bundle.putString("id", paket?.getString("idR").toString())
                    bundle.putString("idM", idM)
                    bundle.putString("idS", idS)
                    dialog.arguments = bundle

                    dialog.show(supportFragmentManager, "RentalKembaliFragment")
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                .show()
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

    private fun detailRental() {
        var paket : Bundle? = intent.extras
        FirebaseFirestore.getInstance().collection("rental").document(paket?.getString("idR").toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val st1 = document.get("tgl_rental").toString()
                    val st2 = document.get("mulai_rental").toString()
                    val st3 = document.get("selesai_rental").toString()
                    val st4 = document.get("hari_rental").toString()
                    val st5 = document.get("status_rental").toString()
                    val st6 = document.get("total").toString()
                    val st7 = document.get("created").toString()
                    val st8 = document.get("status_pembayaran").toString()
                    val st9 = document.get("id_sopir").toString()
                    val st10 = document.get("biaya_sopir").toString()
                    val st11 = document.get("id_mobil").toString()
                    val st12 = document.get("bukti_pembayaran").toString()
                    val st13 = document.get("waktu_denda").toString()
                    val st14 = document.get("nama_mobil").toString()
                    riwayat = Riwayat(st10,st12,st7,"",st4,st11,"","",st9,"",st2, st3,st8,st3,st1,st6,"",st13,st14)
                    idM = st11
                    idS = st9
                    stsB = st8
                    imgUrl = st12
                    b.detailTgl.setText(st1)
                    b.detailTglMulai.setText(st2)
                    b.detailTglSelesai.setText(st3)
                    b.detailHari.setText(st4+" Hari")
                    b.detailCreated.setText(st7)
                    b.detailStatusBayar.setText(st8)
                    b.detailHargaSopir.setText("Rp."+st10)

                    if (st9.equals("")) {
                        b.cardSopir.visibility = View.GONE
                        b.tvSopir.visibility = View.GONE
                        b.detailHargaSopir.visibility = View.GONE
                        b.cdHrgaSopir.visibility = View.GONE
                        b.detailTotal.setText("Rp."+st6)
                    } else {
                        detailSopir(st9)
                        b.detailTotal.setText("Rp."+st6)
                    }

                    if (st8.equals("Belum Bayar")) {
                        b.btnDetailPembayaran.visibility = View.GONE
                    } else if (st8.equals("DP")) {
                        b.detailStatusBayar.setTextColor(Color.BLUE)
                        b.btnDetailPembayaran.visibility = View.VISIBLE
                    } else {
                        b.detailStatusBayar.setTextColor(Color.parseColor("#1ADC33"))
                        b.btnDetailPembayaran.visibility = View.VISIBLE
                    }

                    if (st5.equals("Booking")) {
                        b.detailStatusRental.setText(st5)
                        b.btnBatalkanRental.visibility = View.VISIBLE
                    } else if (st5.equals("Berjalan")) {
                        b.detailStatusRental.setText(st5)
                        b.btnKonfirmasiRental.visibility = View.GONE
                        b.btnKonfirmasiPengembalian.visibility = View.VISIBLE
                        b.btnBatalkanRental.visibility = View.GONE
                    } else if (st5.equals("Selesai")) {
                        b.detailStatusRental.setText(st5)
                        b.detailStatusRental.setTextColor(Color.parseColor("#1ADC33"))
                        b.btnKonfirmasiRental.visibility = View.GONE
                        b.btnBatalkanRental.visibility = View.GONE
                    }

                } else {
                    // Dokumen tidak ditemukan
                }
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    private fun detailSopir(idS: String) {
        FirebaseFirestore.getInstance().collection("sopir").document(idS)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val st1 = document.get("foto_profil").toString()
                    val st2 = document.get("nama").toString()
                    val st3 = document.get("hp").toString()

                    Picasso.get().load(st1).into(b.detailFotoSopir)
                    b.detailSopir.setText(st2)
                    b.detailHpSopir.setText(st3)
                } else {
                    // Dokumen tidak ditemukan
                }
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    private fun detailPelanggan() {
        var paket : Bundle? = intent.extras
        FirebaseFirestore.getInstance().collection("pelanggan").document(paket?.getString("emP").toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val st1 = document.get("foto_profil").toString()
                    val st2 = document.get("nama").toString()
                    val st3 = document.get("hp").toString()
                    val st4 = document.get("alamat").toString()
                    pelanggan = Pelanggan("",st4,"","","","",st3,st2,"","","","")
                    Picasso.get().load(st1).into(b.detailFotoPelanggan)
                    b.detailPelanggan.setText(st2)
                    b.detailNoHpPelanggan.setText(st3)
                } else {
                    // Dokumen tidak ditemukan
                }
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    private fun detailMobil() {
        var paket : Bundle? = intent.extras
        FirebaseFirestore.getInstance().collection("mobil").document(paket?.getString("idM").toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val st1 = document.get("foto_mobil").toString()
                    val st2 = document.get("nama").toString()
                    val st3 = document.get("kategori").toString()
                    val st4 = document.get("harga").toString()
                    val st5 = document.get("merk").toString()
                    val st6 = document.get("plat").toString()
                    mobil = Mobil("", st5,st2,st4,"","",st6,"","","","","","","")

                    Picasso.get().load(st1).into(b.detailFotoMobil)
                    b.detailMobil.setText(st2)
                    b.detailKategori.setText(st3)
                    b.detailHarga.setText("Harga Rental Rp. "+st4)
                    b.detailHargaRental.setText("Rp. "+st4)
                } else {
                    // Dokumen tidak ditemukan
                }
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    fun batalkan() {
        AlertDialog.Builder(this)
            .setTitle("Hapus Rental!")
            .setIcon(R.drawable.warning)
            .setMessage("Apakah Anda ingin membatalkan rental mobil ini?")
            .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                var paket : Bundle? = intent.extras
                FirebaseFirestore.getInstance().collection("rental")
                    .document(paket?.getString("idR").toString())
                    .delete()
                    .addOnSuccessListener {
                        val hm = HashMap<String, Any>()
                        hm.set("status", "tersedia")

                        FirebaseFirestore.getInstance().collection("mobil")
                            .document(paket?.getString("idM").toString())
                            .update(hm)
                            .addOnSuccessListener {

                            }

                        if (idS.isNotEmpty()) {
                            val hm = HashMap<String, Any>()
                            hm.set("status", "tersedia")

                            FirebaseFirestore.getInstance().collection("sopir")
                                .document(idS)
                                .update(hm)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Berhasil menghapus data!", Toast.LENGTH_SHORT).show()
                                    onBackPressed()
                                }
                        }
                    }
            })
            .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->

            })
            .show()
    }
}