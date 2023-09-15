package com.example.rental_mobil.View.Pelanggan

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
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
import com.example.rental_mobil.ViewModel.MobilViewModel
import com.example.rental_mobil.ViewModel.PelangganViewModel
import com.example.rental_mobil.ViewModel.RiwayatViewModel
import com.example.rental_mobil.ViewModel.SopirViewModel
import com.example.rental_mobil.databinding.ActivityRiwayatDetailBinding
import com.squareup.picasso.Picasso
import java.io.Serializable

class RiwayatDetailActivity : AppCompatActivity() {
    private lateinit var b: ActivityRiwayatDetailBinding
    private lateinit var pVM: PelangganViewModel
    private lateinit var mVM: MobilViewModel
    private lateinit var rVM: RiwayatViewModel
    private lateinit var sVM: SopirViewModel

    var idR = ""
    var imgUrl = ""

    lateinit var preferences: SharedPreferences
    val PREF_NAMA = "akun"
    val EMAIL = "email"
    val DEF_EMAIL = ""
    val ID = "id_pelanggan"
    val DEF_ID = ""
    private lateinit var mobil: Mobil
    private lateinit var pelanggan: Pelanggan
    private lateinit var riwayat: Riwayat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRiwayatDetailBinding.inflate(layoutInflater)
        setContentView(b.root)
        supportActionBar?.setTitle("Riwayat Detail")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        preferences = getSharedPreferences(PREF_NAMA, Context.MODE_PRIVATE)

        rVM = ViewModelProvider(this).get(RiwayatViewModel::class.java)
        pVM = ViewModelProvider(this).get(PelangganViewModel::class.java)
        mVM = ViewModelProvider(this).get(MobilViewModel::class.java)
        sVM = ViewModelProvider(this).get(SopirViewModel::class.java)

        b.btnKonfirmasiRental.setOnClickListener {
            var dialog = DialogPembayaran()

            dialog.show(supportFragmentManager, "DialogPembayaran")
        }

        b.btnDetailPembayaran.setOnClickListener {
            val intent = Intent(this, ImageDetailActivity::class.java)
            intent.putExtra("img", imgUrl)
            startActivity(intent)
        }
        b.fabPdf.setOnClickListener {
            val intent = Intent(this, PdfActivity::class.java)
            intent.putExtra("pelanggan", pelanggan as Serializable)
            intent.putExtra("mobil", mobil as Serializable)
            intent.putExtra("riwayat", riwayat as Serializable)
            startActivity(intent)
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

    override fun onStart() {
        super.onStart()
        detailRiwayat()
    }

    fun informasi() {
        AlertDialog.Builder(this)
            .setTitle("Informasi!")
            .setIcon(R.drawable.warning)
            .setMessage("Anda telah melunasi pembayaran, Anda diperbolehkan mengambil kunci mobil sekarang!")
            .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->

            })
            .show()
    }

    fun detailRiwayat() {
        var paket : Bundle? = intent.extras
        var id = paket?.getString("id").toString()
        rVM.detail(id).observe(this, Observer { riwayat ->
            this.riwayat = riwayat
            idR = riwayat.id_rental
            b.detailStatusRental.setText(riwayat.status_rental)
            b.detailTgl.setText(riwayat.tgl_rental)
            var idP = riwayat.id_pelanggan
            var idM = riwayat.id_mobil
            var idS = riwayat.id_sopir
            b.detailTglMulai.setText(riwayat.mulai_rental)
            b.detailTglSelesai.setText(riwayat.selesai_rental)
            b.detailCreated.setText(riwayat.created)
            val hari = riwayat.hari_rental.toInt()
            b.detailHari.setText(riwayat.hari_rental+" Hari")
            b.detailTotal.setText("Rp. "+riwayat.total)
            b.detailStatusBayar.setText(riwayat.status_pembayaran)
            imgUrl = riwayat.bukti_pembayaran

            if (riwayat.status_pembayaran != "Belum Bayar") {
                b.btnKonfirmasiRental.visibility = View.GONE
                b.btnBatalkanRental.visibility = View.GONE
                b.btnDetailPembayaran.visibility = View.VISIBLE
            } else {
                b.btnKonfirmasiRental.visibility = View.VISIBLE
                b.btnBatalkanRental.visibility = View.VISIBLE
                b.btnDetailPembayaran.visibility = View.GONE
            }

            if (riwayat.status_pembayaran == "Lunas" && riwayat.status_rental == "Booking") {
                informasi()
                b.detailKunci.visibility = View.VISIBLE
            }

            pVM.detail(preferences.getString(EMAIL, DEF_EMAIL).toString()).observe(this, Observer { pelanggan ->
                b.detailPelanggan.setText(pelanggan.nama)
                b.detailNoHpPelanggan.setText(pelanggan.hp)
                Picasso.get().load(pelanggan.foto_profil).into(b.detailFotoPelanggan)
                this.pelanggan = pelanggan

            })

            mVM.detail(idM).observe(this, Observer { mobil ->
                this.mobil = mobil

                b.detailMobil.setText(mobil.nama)
                b.detailKategori.setText(mobil.kategori)
                b.detailHarga.setText("Rp. "+mobil.harga)
                Picasso.get().load(mobil.foto_mobil).into(b.detailFotoMobil)

                val hrg = mobil.harga.toInt()
                val totalMobil = hari * hrg
                b.detailHargaRental.setText("Rp. "+totalMobil.toString())
            })

            if (idS.isNotEmpty()) {
                sVM.detail(idS).observe(this, Observer { sopir ->
                    b.detailSopir.setText(sopir.nama)
                    b.detailHpSopir.setText(sopir.hp)
                    Picasso.get().load(sopir.foto_profil).into(b.detailFotoSopir)

                    val hrg = 50000
                    val totalSopir = hari * hrg
                    b.detailHargaSopir.setText("Rp. "+totalSopir.toString())
                })
            } else {
                b.cardSopir.visibility = View.GONE
                b.cdHrgaSopir.visibility = View.GONE
                b.tvSopir.visibility = View.GONE
            }

            b.btnBatalkanRental.setOnClickListener {
                delete(idR, idM, idS)
            }
        })
    }

    fun delete(id: String, idM: String, idS: String) {
        AlertDialog.Builder(this)
            .setTitle("Batalkan!")
            .setIcon(R.drawable.warning)
            .setMessage("Apakah Anda ingin membatalkan rental mobil ini?")
            .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                rVM.delete(id, idM, idS).observe(this, Observer { result ->
                    onBackPressed()
                    Toast.makeText(this, "Berhasil membatalkan rental!", Toast.LENGTH_SHORT).show()
                })
            })
            .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
            })
            .show()
    }
}