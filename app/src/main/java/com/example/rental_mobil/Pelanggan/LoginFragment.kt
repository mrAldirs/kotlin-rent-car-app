package com.example.rental_mobil.Pelanggan

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.rental_mobil.MainActivity
import com.example.rental_mobil.OCRActivity
import com.example.rental_mobil.R
import com.example.rental_mobil.SignUpActivity
import com.example.rental_mobil.databinding.FragmentLoginPelangganBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class LoginFragment : DialogFragment() {
    lateinit var thisParent: MainActivity
    private lateinit var b: FragmentLoginPelangganBinding
    lateinit var v: View

    lateinit var preferences: SharedPreferences
    val PREF_NAMA = "akun"
    val EMAIL = "email"
    val DEF_EMAIL = ""
    val ID = "id_pelanggan"
    val DEF_ID = ""

    lateinit var auth : FirebaseAuth
    var currentUser : FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as MainActivity
        b = FragmentLoginPelangganBinding.inflate(layoutInflater)
        v = b.root

        auth = Firebase.auth

        b.btnLogin.setOnClickListener {
            if (!b.etEmail.text.toString().equals("") && !b.etPassword.text.toString().equals("")) {
                auth.signInWithEmailAndPassword(b.etEmail.text.toString(),b.etPassword.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            currentUser = auth.currentUser
                            if (currentUser!=null){
                                if (currentUser!!.isEmailVerified){
                                    FirebaseFirestore.getInstance().collection("pelanggan")
                                        .whereEqualTo("deleted", "")
                                        .whereEqualTo("email", b.etEmail.text.toString())
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            if (!documents.isEmpty) {
                                                val document = documents.documents[0]
                                                val st1 = document.get("id_pelanggan").toString()

                                                preferences = thisParent.getSharedPreferences(PREF_NAMA, Context.MODE_PRIVATE)
                                                val prefEditor = preferences.edit()
                                                prefEditor.putString(EMAIL, b.etEmail.text.toString())
                                                prefEditor.putString(ID, st1)
                                                prefEditor.commit()

                                                startActivity(Intent(v.context, DashboardActivity::class.java))
                                                thisParent.overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
                                            } else {
                                                Toast.makeText(thisParent, "Akun Anda telah dinonaktifkan oleh Admin", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                }else{
                                    Toast.makeText(this.context, "Email anda belum terverifikasi", Toast.LENGTH_LONG).show()
                                }
                            }
                        }else{
                            Toast.makeText(this.context, "Username / Password salah", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this.context, "Username dan Password tidak boleh kosong!", Toast.LENGTH_LONG).show()
            }
        }

        b.btnRegistrasi.setOnClickListener {
            startActivity(Intent(v.context, OCRActivity::class.java))
            thisParent.overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }

        return v
    }
}