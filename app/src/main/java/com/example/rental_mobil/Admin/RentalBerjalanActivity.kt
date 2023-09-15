package com.example.rental_mobil.Admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rental_mobil.Adapter.AdapterRentalBerjalan
import com.example.rental_mobil.Adapter.AdapterRentalBooking
import com.example.rental_mobil.R
import com.example.rental_mobil.databinding.ActivityRentalAdminRiwayatBinding
import com.google.firebase.firestore.FirebaseFirestore

class RentalBerjalanActivity : AppCompatActivity() {
    private lateinit var b: ActivityRentalAdminRiwayatBinding

    val dataRental = mutableListOf<HashMap<String,String>>()
    lateinit var rentalAdp : AdapterRentalBerjalan

    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRentalAdminRiwayatBinding.inflate(layoutInflater)
        setContentView(b.root)
        supportActionBar?.setTitle("Daftar Mobil Berjalan")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        rentalAdp = AdapterRentalBerjalan(dataRental, this)
        b.rvRental.layoutManager = LinearLayoutManager(this)
        b.rvRental.adapter = rentalAdp

        db = FirebaseFirestore.getInstance()
        showData()
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

    private fun showData() {
        db.collection("rental")
            .whereEqualTo("deleted", "")
            .whereEqualTo("status_rental", "Berjalan")
            .get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    val hm = HashMap<String, String>()
                    hm.put("id_rental", doc.get("id_rental").toString())
                    hm.put("id_mobil", doc.get("id_mobil").toString())
                    hm.put("id_pelanggan", doc.get("id_pelanggan").toString())
                    hm.put("tgl_rental", doc.get("tgl_rental").toString())
                    hm.put("status_rental", doc.get("status_rental").toString())

                    dataRental.add(hm)
                }

                val idMobilList = dataRental.map { it.get("id_mobil").toString() }
                if (idMobilList.isNotEmpty()) {
                    val mobilRef = db.collection("mobil")
                        .whereIn("id_mobil", idMobilList)
                    mobilRef.get()
                        .addOnSuccessListener {mobilResult ->
                            for (mobilDoc in mobilResult) {
                                val idMobil = mobilDoc.get("id_mobil").toString()
                                dataRental.forEach { rental ->
                                    if (rental["id_mobil"] == idMobil) {
                                        rental.put("nama_mobil", mobilDoc.get("nama").toString())
                                    }
                                }
                            }
                        }
                        .addOnFailureListener {

                        }
                }

                val idPelangganList = dataRental.map { it.get("id_pelanggan").toString() }

                if (idPelangganList.isNotEmpty()) {
                    val pelangganRef = db.collection("pelanggan")
                        .whereIn("id_pelanggan", idPelangganList)
                    pelangganRef.get()
                        .addOnSuccessListener { pelangganResult ->
                            for (pelangganDoc in pelangganResult) {
                                val idPelanggan = pelangganDoc.get("id_pelanggan").toString()

                                dataRental.forEach { rental ->
                                    if (rental["id_pelanggan"] == idPelanggan) {
                                        rental.put("nama_pelanggan", pelangganDoc.get("nama").toString())
                                        rental.put("email_pelanggan", pelangganDoc.get("email").toString())
                                    }
                                }
                            }
                            rentalAdp.notifyDataSetChanged()
                        }
                        .addOnFailureListener {

                        }
                }
            }
            .addOnFailureListener { exception ->

            }
    }
}