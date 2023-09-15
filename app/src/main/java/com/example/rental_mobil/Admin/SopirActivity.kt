package com.example.rental_mobil.Admin

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rental_mobil.Adapter.AdapterSopir
import com.example.rental_mobil.R
import com.example.rental_mobil.databinding.ActivitySopirBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class SopirActivity : AppCompatActivity() {
    private lateinit var b: ActivitySopirBinding

    val dataSopir = mutableListOf<HashMap<String,String>>()
    lateinit var sopirAdp : AdapterSopir

    var idS = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySopirBinding.inflate(layoutInflater)
        setContentView(b.root)
        supportActionBar?.setTitle("Daftar Sopir")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sopirAdp = AdapterSopir(dataSopir, this)
        b.rvSopir.layoutManager = LinearLayoutManager(this)
        b.rvSopir.adapter = sopirAdp

        b.btnTambah.setOnClickListener {
            startActivity(Intent(this, SopirTambahActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
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
        showData()
    }

    private fun showData() {
        FirebaseFirestore.getInstance().collection("sopir")
            .whereEqualTo("deleted", "")
            .get()
            .addOnSuccessListener { result ->
                dataSopir.clear()
                for (doc in result) {
                    var hm = HashMap<String, String>()
                    hm.put("id", doc.get("id_sopir").toString())
                    hm.put("nama", doc.get("nama").toString())
                    hm.put("nik", doc.get("nik").toString())
                    hm.put("status", doc.get("status").toString())
                    hm.put("ktp", doc.get("foto_ktp").toString())
                    hm.put("sim", doc.get("foto_sim").toString())

                    dataSopir.add(hm)
                }
                sopirAdp.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    fun deleteSopir(){
        val currentDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        b.progressBar.visibility = View.VISIBLE
        AlertDialog.Builder(this)
            .setTitle("Hapus Sopir!")
            .setIcon(R.drawable.warning)
            .setMessage("Apakah Anda ingin menghapus sopir ini?")
            .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                val hm = HashMap<String, Any>()
                hm.set("status", "tidak tersedia")
                hm.set("deleted", currentDateTime)
                FirebaseFirestore.getInstance().collection("sopir").document(idS).update(hm).addOnSuccessListener {
                    Toast.makeText(this, "berhasil menghapus sopir!", Toast.LENGTH_SHORT).show()
                    b.progressBar.visibility = View.GONE
                    recreate()
                }.addOnFailureListener { e ->

                }
            })
            .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
                b.progressBar.visibility = View.GONE
            })
            .show()
    }
}