package com.example.rental_mobil.Pelanggan

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.rental_mobil.R
import com.example.rental_mobil.databinding.FragmentMobilDetailPelangganBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class MobilDetailFragment : DialogFragment() {
    private lateinit var b: FragmentMobilDetailPelangganBinding
    lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        b = FragmentMobilDetailPelangganBinding.inflate(layoutInflater)
        v = b.root

        return v
    }

    override fun onStart() {
        super.onStart()
        detail()
    }

    private fun detail() {
        FirebaseFirestore.getInstance().collection("mobil")
            .document(arguments?.get("id").toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val st1 = document.get("foto_mobil").toString()
                    val st2 = document.get("nama").toString()
                    val st3 = document.get("kategori").toString()
                    val st4 = document.get("merk").toString()
                    val st5 = document.get("status").toString()
                    val st6 = document.get("created").toString()
                    val st7 = document.get("updated").toString()
                    val st8 = document.get("id_mobil").toString()
                    val st9 = document.get("harga").toString()
                    val st10 = document.get("stnk").toString()
                    val st11 = document.get("plat").toString()
                    val st12 = document.get("tahun").toString()

                    b.detailMobil.setText(st2)
                    b.detailKategori.setText("Kategori : "+st3)
                    b.detailMerk.setText("Merek : "+st4)
                    b.detailHarga.setText("Harga Rental Rp."+st9)
                    b.detailStnk.setText("STNK : "+st10)
                    b.detailPlat.setText("Nomor Plat : "+st11)
                    b.detailTahun.setText("Tahun Keluaran : "+st12)

                    if (st5.equals("tersedia")) {
                        b.detailStatus.setText(st5)
                        b.detailStatus.setTextColor(Color.parseColor("#0037FF"))
                    } else {
                        b.detailStatus.setText(st5)
                        b.detailStatus.setTextColor(Color.RED)
                    }

                    b.detailCreated.setText("Dibuat pada "+st6)
                    b.detailUpdated.setText("Diubah pada "+st7)
                    Picasso.get().load(st1).into(b.detailFoto)
                } else {
                    // Dokumen tidak ditemukan
                }
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }
}