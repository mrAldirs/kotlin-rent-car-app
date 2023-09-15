package com.example.rental_mobil.View.Pelanggan

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.rental_mobil.Model.Pelanggan
import com.example.rental_mobil.R
import com.example.rental_mobil.View.Admin.Mobil.MobilActivity
import com.example.rental_mobil.ViewModel.ProfilViewModel
import com.example.rental_mobil.databinding.ActivityProfilEditBinding
import com.squareup.picasso.Picasso

class ProfilEditActivity : AppCompatActivity() {
    private lateinit var b: ActivityProfilEditBinding
    private lateinit var pVM: ProfilViewModel

    lateinit var preferences: SharedPreferences
    val PREF_NAMA = "akun"
    val EMAIL = "email"
    val DEF_EMAIL = ""

    val RC_OK = 100
    lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityProfilEditBinding.inflate(layoutInflater)
        setContentView(b.root)
        supportActionBar?.setTitle("Profil")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        preferences = getSharedPreferences(PREF_NAMA, Context.MODE_PRIVATE)
        pVM = ViewModelProvider(this).get(ProfilViewModel::class.java)

        b.btnFile.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.setType("image/*")
            startActivityForResult(intent, RC_OK)
        }

        b.btnKirim.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Informasi!")
                .setIcon(R.drawable.warning)
                .setMessage("Apakah Anda ingin mengedit profil Anda?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    edit(uri)
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                .show()
        }

        loadData()
        uri = Uri.EMPTY
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_OK) {
            if (resultCode == Activity.RESULT_OK) {
                uri = data?.data!!
                Log.e("url-foto", uri.toString())
                Picasso.get().load(uri.toString()).into(b.profilFoto)
            }
        }
    }

    private fun loadData() {
        pVM.profil(preferences.getString(EMAIL, DEF_EMAIL).toString()).observe(this, Observer { profil ->
            b.profilNama.setText(profil.nama)
            b.profilAlamt.setText(profil.alamat)
            b.profilEmail.setText(profil.email)
            b.profilNik.setText(profil.nik)
            b.profilNohp.setText(profil.hp)
            Picasso.get().load(profil.foto_profil).into(b.profilFoto)
        })
    }

    private fun edit(uri: Uri?) {
        val data = Pelanggan(
            "",
            b.profilAlamt.text.toString(),
            "",preferences.getString(EMAIL, DEF_EMAIL).toString(), "", "",
            b.profilNohp.text.toString(), b.profilNama.text.toString(), b.profilNik.text.toString(),
            "", "", ""
        )

        pVM.edit(data, uri)
            .observe(this, Observer { succes ->
                if (succes) {
                    Toast.makeText(this, "Berhasil mengedit profil!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ProfilActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    recreate()
                    overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
                } else {
                    Toast.makeText(this, "Gagal mengedit data!", Toast.LENGTH_SHORT).show()
                }
            })
    }
}