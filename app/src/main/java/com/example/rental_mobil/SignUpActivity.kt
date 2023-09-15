package com.example.rental_mobil

import android.app.Activity
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.rental_mobil.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.UUID

class SignUpActivity : AppCompatActivity() {
    private lateinit var b: ActivitySignupBinding

    lateinit var auth : FirebaseAuth
    var currentUser : FirebaseUser? = null

    val RC_P = 100
    val RC_K = 200

    lateinit var uriP: Uri
    lateinit var uriK: Uri

    var url_p = ""
    var url_k = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(b.root)
        supportActionBar?.hide()

        var ocr : Bundle? = intent.extras
        b.insNama.setText(ocr?.getString("ocr-nama").toString() ?: "")
        b.insNik.setText(ocr?.getString("ocr-nik").toString() ?: "")
        b.insTgl.setText(ocr?.getString("ocr-tanggal_lahir").toString() ?: "")
        val image = ocr?.getString("ocr-url-ktp").toString() ?:""

        uriK = Uri.parse("file://${ image}")
        Log.e("image-ktp",uriK.toString())
        Picasso.get().load(uriK).into(b.imgFotoKtp)
        b.insTgl.setOnClickListener {
            showDatePickerDialog()
        }

        b.btnFile.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.setType("image/*")
            startActivityForResult(intent, RC_P)
        }

        b.btnFileKtp.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.setType("image/*")
            startActivityForResult(intent, RC_K)
        }

        auth = Firebase.auth

        b.btnKirim.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Peringatan!")
                .setIcon(R.drawable.warning)
                .setMessage("Apakah Anda ingin membuat akun Baru?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    if (b.insPasswordConfirm.text.toString() == b.insPassword.text.toString()) {
                        b.progressBarSignUp1.visibility = View.VISIBLE
                        validasi()
                    } else {
                        AlertDialog.Builder(this)
                            .setTitle("Peringatan!")
                            .setIcon(R.drawable.warning)
                            .setMessage("Tolong masukkan password dengan benar?")
                            .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                                null
                            })
                            .show()
                        b.progressBarSignUp1.visibility = View.GONE
                    }
                })
                .show()
        }
    }

    override fun onStart() {
        super.onStart()
        b.progressBarSignUp1.visibility = View.GONE
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
                b.insTgl.setText(date)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Peringatan!")
            .setIcon(R.drawable.warning)
            .setMessage("Apakah Anda ingin membatalkan pendaftaran akun baru?")
            .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                super.onBackPressed()
            })
            .setNegativeButton("Tidak",DialogInterface.OnClickListener { dialogInterface, i ->
                null
            })
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_P) {
            if (resultCode == Activity.RESULT_OK) {
                uriP = data?.data!!
                Log.e("url-foto", uriP.toString())
                Picasso.get().load(uriP.toString()).into(b.imgFotoProfil)
            }
        } else if (requestCode == RC_K) {
            if (resultCode == Activity.RESULT_OK) {
                uriK = data?.data!!
                Picasso.get().load(uriK.toString()).into(b.imgFotoKtp)
            }
        }
    }

    private fun regis() {
        auth.createUserWithEmailAndPassword(b.insEmail.text.toString(), b.insPassword.text.toString())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    currentUser = auth.currentUser
                    if (currentUser != null) {
                        currentUser!!.updateProfile(
                            userProfileChangeRequest {
                                displayName = b.insNama.text.toString()
                            }
                        )
                        currentUser!!.sendEmailVerification()
                    }
                }
                val randomId = UUID.randomUUID().toString()

                val fileName = "IMG" + SimpleDateFormat("yyyyMMddHHmmssSSS").format(Date()) + "new"
                val fileRef = FirebaseStorage.getInstance().reference.child(fileName + ".jpg")
                val uploadTask = fileRef.putFile(uriP)
                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    fileRef.downloadUrl
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

                        val hm = HashMap<String, Any>()
                        hm.set("id_pelanggan", randomId)
                        hm.set("nama", b.insNama.text.toString())
                        hm.set("email", b.insEmail.text.toString())
                        hm.set("nik", b.insNik.text.toString())
                        hm.set("hp", b.insNoHpAkun.text.toString())
                        hm.set("ttl", b.insTempat.text.toString()+", "+b.insTgl.text.toString())
                        hm.set("alamat", b.insAlamt.text.toString())
                        hm.set("password", b.insPassword.text.toString())
                        hm.set("deleted", "")
                        hm.set("created", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))
                        hm.set("updated", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))
                        hm.set("foto_profil", url_p)
                        hm.set("foto_ktp", url_k)

                        FirebaseFirestore.getInstance().collection("pelanggan")
                            .document(b.insEmail.text.toString())
                            .set(hm)
                            .addOnSuccessListener {
                                val intent = Intent(this, SignUpSelesaiActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                startActivity(intent)
                                overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
                                Toast.makeText(this, "Masuk ke Akun Email untuk Verifikasi Akun!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            .addOnFailureListener {

                            }
                    }
                }
            }
    }

    fun validasi() {
        val nik = b.insNik.text.toString()
        val email = b.insEmail.text.toString()

        FirebaseFirestore.getInstance().collection("pelanggan")
            .whereEqualTo("nik", nik)
            .get()
            .addOnSuccessListener { nikQuerySnapshot ->
                if (nikQuerySnapshot.isEmpty) {
                    FirebaseFirestore.getInstance().collection("pelanggan")
                        .whereEqualTo("email", email)
                        .get()
                        .addOnSuccessListener { simQuerySnapshot ->
                            if (simQuerySnapshot.isEmpty) {
                                regis()
                            } else {
                                Toast.makeText(this, "Email sudah terdaftar!", Toast.LENGTH_SHORT).show()
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