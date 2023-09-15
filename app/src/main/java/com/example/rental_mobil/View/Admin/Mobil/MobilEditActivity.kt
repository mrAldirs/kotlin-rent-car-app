package com.example.rental_mobil.View.Admin.Mobil

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.rental_mobil.Model.Mobil
import com.example.rental_mobil.R
import com.example.rental_mobil.ViewModel.MobilViewModel
import com.example.rental_mobil.databinding.ActivityMobilEditBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.squareup.picasso.Picasso

class MobilEditActivity : AppCompatActivity() {
    private lateinit var b: ActivityMobilEditBinding
    private lateinit var mobilVM: MobilViewModel

    val RC_OK = 100
    lateinit var uri: Uri

    var ktg = ""
    lateinit var adp : ArrayAdapter<String>
    val dataList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMobilEditBinding.inflate(layoutInflater)
        setContentView(b.root)
        supportActionBar?.setTitle("Edit Mobil")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mobilVM = ViewModelProvider(this).get(MobilViewModel::class.java)
        adp = ArrayAdapter(this, android.R.layout.simple_list_item_1, dataList)

        b.btnChoose.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.setType("image/*")
            startActivityForResult(intent, RC_OK)
        }

        b.btnSimpan.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Informasi!")
                .setIcon(R.drawable.warning)
                .setMessage("Apakah Anda ingin mengedit data ini?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    edit(uri)
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                .show()
        }

        detail()
        uri = Uri.EMPTY
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
        showKategori()
    }

    private fun showKategori() {
        FirebaseFirestore.getInstance().collection("kategori")
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                for (document in querySnapshot.documents) {
                    val data = document.get("kategori").toString()
                    dataList.add(data)
                }
                b.edtKategori.adapter = adp
            }
            .addOnFailureListener { exception ->
                // Tangani kegagalan mengambil data dari Firebase Firestore
            }
    }

    private fun detail() {
        var paket : Bundle? = intent.extras
        mobilVM.detail(paket?.getString("id").toString()).observe(this, Observer { mobil ->
            b.edtNama.setText(mobil.nama)
            b.edtKategori.setSelection(adp.getPosition(mobil.kategori))
            b.edtMerk.setText(mobil.merk)
            b.edtHarga.setText(mobil.harga)
            b.edtStnk.setText(mobil.stnk)
            b.edtPlat.setText(mobil.plat)
            b.edtTahun.setText(mobil.tahun)
            b.edtDetail.setText(mobil.keterangan)
            Picasso.get().load(mobil.foto_mobil).into(b.edtImage)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((resultCode == Activity.RESULT_OK) && (requestCode == RC_OK)) {
            if (data != null){
                uri = data.data!!
                Picasso.get().load(uri.toString()).into(b.edtImage)
            }
        }
    }

    private fun edit(uri: Uri) {
        var paket : Bundle? = intent.extras
        val data = Mobil(
            paket?.getString("id").toString(),
            b.edtMerk.text.toString(),
            b.edtNama.text.toString(),
            b.edtHarga.text.toString(),
            b.edtStnk.text.toString(),
            b.edtTahun.text.toString(),
            b.edtPlat.text.toString(),
            b.edtKategori.selectedItem.toString(),"",
            b.edtDetail.text.toString(),"","","", "",
        )

        mobilVM.edit(data, uri)
            .observe(this, Observer { succes ->
                if (succes) {
                    Toast.makeText(this, "Berhasil mengedit mobil!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MobilActivity::class.java)
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