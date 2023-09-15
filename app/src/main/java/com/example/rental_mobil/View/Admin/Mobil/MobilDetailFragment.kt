package com.example.rental_mobil.View.Admin.Mobil

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.rental_mobil.R
import com.example.rental_mobil.ViewModel.MobilViewModel
import com.example.rental_mobil.databinding.FragmentMobilDetailBinding
import com.squareup.picasso.Picasso

class MobilDetailFragment : DialogFragment() {
    private lateinit var b: FragmentMobilDetailBinding
    private lateinit var mobilVM: MobilViewModel
    lateinit var v: View

    var idM = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        b = FragmentMobilDetailBinding.inflate(layoutInflater)
        v = b.root

        mobilVM = ViewModelProvider(this).get(MobilViewModel::class.java)

        b.btnEdit.setOnClickListener {
            val intent = Intent(v.context, MobilEditActivity::class.java)
            intent.putExtra("id", arguments?.get("id").toString())
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }

        return v
    }

    override fun onStart() {
        super.onStart()
        detail()
    }

    private fun detail() {
        var id = arguments?.get("id").toString()
        mobilVM.detail(id).observe(this, Observer { mobil ->
            idM = mobil.id_mobil
            b.detailMobil.setText(mobil.nama)
            b.detailKategori.setText("Kategori : "+mobil.kategori)
            b.detailMerk.setText("Merek : "+mobil.merk)
            b.detailHarga.setText("Harga Rental Rp."+mobil.harga)
            b.detailStnk.setText("STNK : "+mobil.stnk)
            b.detailPlat.setText("Nomor Plat : "+mobil.plat)
            b.detailTahun.setText("Tahun Keluaran : "+mobil.tahun)
            b.detailKeterangan.setText(mobil.keterangan)

            if (mobil.status.equals("tersedia")) {
                b.detailStatus.setText(mobil.status)
                b.detailStatus.setTextColor(Color.parseColor("#0037FF"))
            } else {
                b.detailStatus.setText(mobil.status)
                b.detailStatus.setTextColor(Color.RED)
            }

            b.detailCreated.setText("Dibuat pada "+mobil.created)
            b.detailUpdated.setText("Diubah pada "+mobil.updated)
            Picasso.get().load(mobil.foto_mobil).into(b.detailFoto)
        })
    }
}