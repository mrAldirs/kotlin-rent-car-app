package com.example.rental_mobil.Admin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rental_mobil.R
import com.example.rental_mobil.databinding.ActivityPelangganDetailAdminBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class PelangganDetailActivity : AppCompatActivity() {
    private lateinit var b: ActivityPelangganDetailAdminBinding

    lateinit var db : FirebaseFirestore
    var idP = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPelangganDetailAdminBinding.inflate(layoutInflater)
        setContentView(b.root)
        supportActionBar?.setTitle("Detail Pelanggan")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()
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
        detailPelanggan()
    }

    private fun detailPelanggan() {
        var paket : Bundle? = intent.extras
        db.collection("pelanggan").document(paket?.getString("email").toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val st1 = document.get("foto_profil").toString()
                    val st2 = document.get("nama").toString()
                    val st3 = document.get("nik").toString()
                    val st4 = document.get("ttl").toString()
                    val st5 = document.get("email").toString()
                    val st6 = document.get("hp").toString()
                    val st7 = document.get("created").toString()
                    val st8 = document.get("updated").toString()
                    val st9 = document.get("alamat").toString()

                    Picasso.get().load(st1).into(b.detailImage)
                    b.detailNama.setText(st2)
                    b.detailNik.setText("NIK : "+st3)
                    b.detailTtl.setText("Tempat Tanggal Lahir : "+st4)
                    b.detailEmail.setText("Email : "+st5)
                    b.detailHp.setText("Nomor Handphone : "+st6)
                    b.detailAlamat.setText("Alamat : "+st9)
                    b.detailCreated.setText(st7)
                    b.detailUpdated.setText(st8)
                } else {
                    // Dokumen tidak ditemukan
                }
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }
}