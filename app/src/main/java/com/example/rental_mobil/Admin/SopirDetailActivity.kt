package com.example.rental_mobil.Admin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rental_mobil.R
import com.example.rental_mobil.databinding.ActivitySopirDetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class SopirDetailActivity : AppCompatActivity() {
    private lateinit var b: ActivitySopirDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySopirDetailBinding.inflate(layoutInflater)
        setContentView(b.root)
        supportActionBar?.setTitle("Detail Sopir")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        detailSopir()

        b.btnEdit.setOnClickListener {
            var paket : Bundle? = intent.extras
            val intent = Intent(this, SopirEditActivity::class.java)
            intent.putExtra("id", paket?.getString("id").toString())
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
    }

    private fun detailSopir() {
        var paket : Bundle? = intent.extras
        FirebaseFirestore.getInstance().collection("sopir")
            .document(paket?.getString("id").toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val st1 = document.get("foto_profil").toString()
                    val st2 = document.get("nama").toString()
                    val st3 = document.get("nik").toString()
                    val st4 = document.get("tempat_lahir").toString()
                    val st5 = document.get("email").toString()
                    val st6 = document.get("hp").toString()
                    val st7 = document.get("created").toString()
                    val st8 = document.get("updated").toString()
                    val st9 = document.get("alamat").toString()
                    val st10 = document.get("sim").toString()
                    val st11 = document.get("status").toString()
                    val st12 = document.get("tanggal_lahir").toString()

                    Picasso.get().load(st1).into(b.detailImage)
                    b.detailNama.setText(st2)
                    b.detailNik.setText("NIK : "+st3)
                    b.detailNoSim.setText("SIM : "+st10)
                    b.detailTtl.setText("Tempat Tanggal Lahir : "+st4+", "+st12)
                    b.detailEmail.setText("Email : "+st5)
                    b.detailHp.setText("Nomor Handphone : "+st6)
                    b.detailAlamat.setText("Alamat : "+st9)
                    b.detailCreated.setText(st7)
                    b.detailUpdated.setText(st8)

                    if (st11.equals("tersedia")) {
                        b.detailStatus.setText(st11)
                        b.detailStatus.setTextColor(Color.parseColor("#0037FF"))
                    } else {
                        b.detailStatus.setText(st11)
                        b.detailStatus.setTextColor(Color.RED)
                    }
                } else {
                    // Dokumen tidak ditemukan
                }
            }
            .addOnFailureListener { exception ->
                // Handle error
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