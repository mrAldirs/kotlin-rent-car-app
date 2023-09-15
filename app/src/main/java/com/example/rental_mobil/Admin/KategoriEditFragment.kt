package com.example.rental_mobil.Admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.rental_mobil.R
import com.example.rental_mobil.databinding.FragmentKategoriTambahBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class KategoriEditFragment : DialogFragment() {
    private lateinit var b: FragmentKategoriTambahBinding
    lateinit var v: View

    var tahun = 0
    var bulan = 0
    var hari = 0
    var jam = 0
    var menit = 0
    var detik = 0

    var jmTgl = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        b = FragmentKategoriTambahBinding.inflate(layoutInflater)
        v = b.root

        val cal : Calendar = Calendar.getInstance()

        bulan = cal.get(Calendar.MONTH)+1
        hari = cal.get(Calendar.DAY_OF_MONTH)
        tahun = cal.get(Calendar.YEAR)
        jam = cal.get(Calendar.HOUR_OF_DAY)
        menit = cal.get(Calendar.MINUTE)
        detik = cal.get(Calendar.SECOND)

        jmTgl = "$tahun-$bulan-$hari $jam:$menit:$detik"

        b.close.setOnClickListener {
            dismiss()
        }

        detail()

        b.btnSimpan.setOnClickListener {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("Informasi!")
                .setIcon(R.drawable.warning)
                .setMessage("Apakah Anda ingin mengedit kategori ini?")
                .setPositiveButton("Ya") { dialog, _ ->
                    // Tindakan yang dilakukan saat tombol OK ditekan
                    edit()
                }
                .setNegativeButton("Batal") { dialog, _ ->
                    // Tindakan yang dilakukan saat tombol Batal ditekan
                    dialog.dismiss()
                }
            builder.show()
        }

        return v
    }

    private fun detail() {
        FirebaseFirestore.getInstance().collection("kategori")
            .document(arguments?.get("id").toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val st1 = document.get("kategori").toString()
                    val st2 = document.get("deskripsi").toString()

                    b.insKategori.setText(st1)
                    b.insDeskripsi.setText(st2)
                } else {
                    // Dokumen tidak ditemukan
                }
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    private fun edit() {
        val hm = HashMap<String, Any>()
        hm.set("kategori", b.insKategori.text.toString())
        hm.set("deskripsi", b.insDeskripsi.text.toString())
        hm.set("updated", jmTgl)
        FirebaseFirestore.getInstance().collection("kategori")
            .document(arguments?.get("id").toString())
            .update(hm)
            .addOnSuccessListener {
                Toast.makeText(v.context, "berhasil mengedit kategori!", Toast.LENGTH_SHORT).show()
                dismiss()
                requireParentFragment().requireActivity().recreate()
        }.addOnFailureListener { e ->

        }
    }
}