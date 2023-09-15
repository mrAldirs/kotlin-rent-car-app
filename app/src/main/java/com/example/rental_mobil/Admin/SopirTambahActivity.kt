package com.example.rental_mobil.Admin

import android.app.Activity
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.rental_mobil.R
import com.example.rental_mobil.databinding.ActivitySopirTambahBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.UUID

class SopirTambahActivity : AppCompatActivity() {
    private lateinit var b: ActivitySopirTambahBinding

    lateinit var uriP: Uri
    lateinit var uriK: Uri
    lateinit var uriS: Uri

    val RC_P = 100
    val RC_K = 200
    val RC_S = 300

    var url_p = ""
    var url_k = ""
    var url_s = ""

    lateinit var storage : StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySopirTambahBinding.inflate(layoutInflater)
        setContentView(b.root)
        supportActionBar?.setTitle("Tambah Sopir")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        b.insTanggal.setOnClickListener {
            showDatePickerDialog()
        }

        storage = FirebaseStorage.getInstance().reference

        uriP = Uri.EMPTY
        uriS = Uri.EMPTY
        uriK = Uri.EMPTY

        b.btnImage.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.setType("image/*")
            startActivityForResult(intent, RC_P)
        }

        b.btnChooseKtp.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.setType("image/*")
            startActivityForResult(intent, RC_K)
        }

        b.btnChooseSim.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.setType("image/*")
            startActivityForResult(intent, RC_S)
        }

        b.btnSimpan.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Informasi!")
                .setIcon(R.drawable.warning)
                .setMessage("Apakah Anda ingin menambahkan sopir baru?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    if (b.insNama.text.toString().equals("") || b.insEmail.text.toString().equals("") || b.insHp.text.toString().equals("") ||
                        b.insAlamat.text.toString().equals("") || b.insNik.text.toString().equals("") || b.insSim.text.toString().equals("") ||
                        b.insTempat.text.toString().equals("") || b.insTanggal.text.toString().equals("") || uriP == Uri.EMPTY ||
                        uriS == Uri.EMPTY || uriK == Uri.EMPTY) {
                        Toast.makeText(this, "Tolong lengkapi data terlebih dahulu", Toast.LENGTH_SHORT).show()
                    } else {
                        b.btnSimpan.visibility = View.GONE
                        b.progressBar.visibility = View.VISIBLE
                        validasi()
                    }
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

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val date = "$year-${month + 1}-$dayOfMonth"
                b.insTanggal.setText(date)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_P) {
            if (resultCode == Activity.RESULT_OK) {
                uriP = data?.data!!
                Picasso.get().load(uriP.toString()).into(b.insImage)
            }
        } else if (requestCode == RC_K) {
            if (resultCode == Activity.RESULT_OK) {
                uriK = data?.data!!
                Picasso.get().load(uriK.toString()).into(b.insImageKtp)
            }
        } else if (requestCode == RC_S) {
            if (resultCode == Activity.RESULT_OK) {
                uriS = data?.data!!
                Picasso.get().load(uriS.toString()).into(b.insImageSim)
            }
        }
    }

    private fun insert() {
        val randomId = UUID.randomUUID().toString()

        val fileName1 = "IMG"+SimpleDateFormat("yyyyMMddHHmmssSSS").format(Date())
        val fileRef1 = storage.child(fileName1 + ".jpg")
        val uploadTask1 = fileRef1.putFile(uriP)
        uploadTask1.continueWithTask{ task ->
            if (!task.isSuccessful){
                task.exception?.let {
                    throw it
                }
            }
            fileRef1.downloadUrl
        }.addOnCompleteListener {
            url_p = it.result.toString()

            val fileName2 = "KTP"+SimpleDateFormat("yyyyMMddHHmmssSSS").format(Date())
            val fileRef2 = FirebaseStorage.getInstance().reference.child(fileName2 + ".jpg")
            val uploadTask2 = fileRef2.putFile(uriK)
            uploadTask2.continueWithTask{ task ->
                if (!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }
                fileRef2.downloadUrl
            }.addOnCompleteListener {
                url_k = it.result.toString()

                val fileName3 = "SIM"+SimpleDateFormat("yyyyMMddHHmmssSSS").format(Date())
                val fileRef3 = storage.child(fileName3 + ".jpg")
                val uploadTask3 = fileRef3.putFile(uriS)
                uploadTask3.continueWithTask{ task ->
                    if (!task.isSuccessful){
                        task.exception?.let {
                            throw it
                        }
                    }
                    fileRef3.downloadUrl
                }.addOnCompleteListener {
                    url_s = it.result.toString()

                    val hm = HashMap<String, Any>()
                    hm.set("id_sopir",randomId)
                    hm.set("nama", b.insNama.text.toString())
                    hm.set("email",b.insEmail.text.toString())
                    hm.set("hp",b.insHp.text.toString())
                    hm.set("tempat_lahir",b.insTempat.text.toString())
                    hm.set("tanggal_lahir", b.insTanggal.text.toString())
                    hm.set("alamat",b.insAlamat.text.toString())
                    hm.set("nik",b.insNik.text.toString())
                    hm.set("sim",b.insSim.text.toString())
                    hm.set("status", "tersedia")
                    hm.set("deleted", "")
                    hm.set("created", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))
                    hm.set("updated", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))
                    hm.put("foto_profil", url_p)
                    hm.put("foto_ktp", url_k)
                    hm.put("foto_sim", url_s)

                    FirebaseFirestore.getInstance().collection("sopir")
                        .document(randomId)
                        .set(hm)
                        .addOnSuccessListener {
                            onBackPressed()
                            recreate()
                            Toast.makeText(this, "Sukses menambah sopir baru!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {

                        }
                }
            }
        }
    }

    fun validasi() {
        val nik = b.insNik.text.toString()
        val sim = b.insSim.text.toString()

        FirebaseFirestore.getInstance().collection("sopir")
            .whereEqualTo("nik", nik)
            .get()
            .addOnSuccessListener { nikQuerySnapshot ->
                if (nikQuerySnapshot.isEmpty) {
                    FirebaseFirestore.getInstance().collection("sopir")
                        .whereEqualTo("sim", sim)
                        .get()
                        .addOnSuccessListener { simQuerySnapshot ->
                            if (simQuerySnapshot.isEmpty) {
                                insert()
                            } else {
                                Toast.makeText(this, "SIM sudah digunakan", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Handle failure
                        }
                } else {
                    Toast.makeText(this, "NIK sudah terdaftar", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }
    }
}
