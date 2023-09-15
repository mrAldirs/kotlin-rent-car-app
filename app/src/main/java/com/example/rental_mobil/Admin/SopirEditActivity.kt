package com.example.rental_mobil.Admin

import android.app.Activity
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.rental_mobil.R
import com.example.rental_mobil.databinding.ActivitySopirEditBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.HashMap

class SopirEditActivity : AppCompatActivity() {
    private lateinit var b: ActivitySopirEditBinding

    lateinit var storage : StorageReference
    lateinit var uri: Uri

    val RC_OK = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySopirEditBinding.inflate(layoutInflater)
        setContentView(b.root)
        supportActionBar?.setTitle("Edit Sopir")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var paket : Bundle? = intent.extras
        Toast.makeText(this, paket?.getString("id").toString(), Toast.LENGTH_SHORT).show()
        detailSopir()

        b.edTanggal.setOnClickListener {
            showDatePickerDialog()
        }

        uri = Uri.EMPTY

        b.btnImage.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.setType("image/*")
            startActivityForResult(intent, RC_OK)
        }

        b.btnSimpan.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Informasi!")
                .setIcon(R.drawable.warning)
                .setMessage("Apakah Anda ingin mengedit Sopir ini?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    edit()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((resultCode == Activity.RESULT_OK) && (requestCode == RC_OK)) {
            if (data != null){
                uri = data.data!!
                Picasso.get().load(uri.toString()).into(b.editImage)
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val date = "$year-${month + 1}-$dayOfMonth"
                b.edTanggal.setText(date)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
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
                    val st7 = document.get("foto_ktp").toString()
                    val st8 = document.get("tanggal_lahir").toString()
                    val st9 = document.get("alamat").toString()
                    val st10 = document.get("sim").toString()
                    val st11 = document.get("foto_sim").toString()

                    Picasso.get().load(st1).into(b.editImage)
                    Picasso.get().load(st11).into(b.editImageSim)
                    Picasso.get().load(st7).into(b.editImageKtp)
                    b.edNama.setText(st2)
                    b.edNik.setText(st3)
                    b.edSim.setText(st10)
                    b.edEmaik.setText(st5)
                    b.edHp.setText(st6)
                    b.edAlamat.setText(st9)
                    b.edTempat.setText(st4)
                    b.edTanggal.setText(st8)
                } else {
                    // Dokumen tidak ditemukan
                }
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    private fun edit() {
        var paket : Bundle? = intent.extras
        if (uri != Uri.EMPTY) {
            val fileName = "IMG"+ SimpleDateFormat("yyyyMMddHHmmssSSS").format(Date())+"new"
            val fileRef = FirebaseStorage.getInstance().reference.child(fileName+".jpg")
            val uploadTask = fileRef.putFile(uri)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }
                fileRef.downloadUrl
            }.addOnCompleteListener {
                val hm = HashMap<String, Any>()
                hm.set("nama", b.edNama.text.toString())
                hm.set("email", b.edEmaik.text.toString())
                hm.set("hp", b.edHp.text.toString())
                hm.set("tempat_lahir", b.edTempat.text.toString())
                hm.set("tanggal_lahir", b.edTanggal.text.toString())
                hm.set("alamat", b.edAlamat.text.toString())
                hm.set("updated", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))
                hm.put("foto_profil", it.result.toString())

                FirebaseFirestore.getInstance().collection("sopir")
                    .document(paket?.getString("id").toString())
                    .update(hm)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Berhasil mengedit sopir!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, SopirDetailActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
                    }.addOnFailureListener {
                        Toast.makeText(this, "Gagal mengedit sopir!", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            val hm = HashMap<String, Any>()
            hm.set("nama", b.edNama.text.toString())
            hm.set("email", b.edEmaik.text.toString())
            hm.set("hp", b.edHp.text.toString())
            hm.set("ttl", b.edTempat.text.toString()+", "+b.edTanggal.text.toString())
            hm.set("alamat", b.edAlamat.text.toString())
            hm.set("updated", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))

            FirebaseFirestore.getInstance().collection("sopir")
                .document(paket?.getString("id").toString())
                .update(hm)
                .addOnSuccessListener {
                    Toast.makeText(this, "Berhasil mengedit sopir!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, SopirDetailActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
                }.addOnFailureListener {
                    Toast.makeText(this, "Gagal mengedit sopir!", Toast.LENGTH_SHORT).show()
                }
        }
    }
}