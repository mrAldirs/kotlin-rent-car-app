package com.example.rental_mobil.Admin

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.rental_mobil.MainActivity
import com.example.rental_mobil.R
import com.example.rental_mobil.databinding.FragmentLoginAdminBinding

class LoginFragment : DialogFragment() {
    lateinit var thisParent: MainActivity
    private lateinit var b: FragmentLoginAdminBinding
    lateinit var v: View

    lateinit var preferences: SharedPreferences
    val PREF_NAME = "akun"
    val USER = "kd_user"
    val DEF_USER = ""
    val NAMA = "nama"
    val DEF_NAMA = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as MainActivity
        b = FragmentLoginAdminBinding.inflate(layoutInflater)
        v = b.root

        b.btnLogin.setOnClickListener {
            if (b.etEmail.text.toString().equals("admin@gmail.com") && b.etPassword.text.toString().equals("admin")) {
                startActivity(Intent(v.context, DashboardActivity::class.java))
                thisParent.finishAffinity()
                thisParent.overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
            } else {
                Toast.makeText(this.context, "Username yang Anda masukkan salah!", Toast.LENGTH_SHORT).show()
            }
        }
        return v
    }
}