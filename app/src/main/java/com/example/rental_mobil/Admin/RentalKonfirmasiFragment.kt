package com.example.rental_mobil.Admin

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.rental_mobil.R
import com.example.rental_mobil.databinding.FragmentRentalAdminKonfirmasiBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.HashMap

class RentalKonfirmasiFragment : DialogFragment() {
    private lateinit var b: FragmentRentalAdminKonfirmasiBinding
    lateinit var v: View
    lateinit var parent: RentalDetailActivity

    val statusBay = arrayOf("Belum Bayar", "DP", "Lunas")
    lateinit var adapterStatus: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        b = FragmentRentalAdminKonfirmasiBinding.inflate(layoutInflater)
        v = b.root
        parent = activity as RentalDetailActivity

        adapterStatus = ArrayAdapter(v.context, android.R.layout.simple_list_item_1, statusBay)
        b.insPembayaran.adapter = adapterStatus

        b.btnKirim.setOnClickListener {
            if (b.insPembayaran.selectedItem.toString().equals("Lunas")) {
                bayar()
            } else {
                AlertDialog.Builder(v.context)
                    .setTitle("Peringatan!")
                    .setIcon(R.drawable.warning)
                    .setMessage("Pelanggan belum melunasi pembayaran, tidak dapat mengonfirmasi mobil telah berjalan?")
                    .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                        null
                    })
                    .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
                    })
                    .show()
            }
        }

        b.insPembayaran.setSelection(adapterStatus.getPosition(parent.stsB))

        return v
    }

    fun bayar() {
        val hm = HashMap<String, Any>()
        hm.set("status_rental", "Berjalan")
        hm.set("status_pembayaran", b.insPembayaran.selectedItem.toString())
        hm.set("keterangan", b.insKeterangan.text.toString())
        hm.set("updated", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))

        FirebaseFirestore.getInstance().collection("rental")
            .document(arguments?.get("id").toString())
            .update(hm)
            .addOnSuccessListener {
                Toast.makeText(v.context, "Berhasil mengonfirmasi bahwa mobil sudah berjalan!", Toast.LENGTH_SHORT).show()
                val intent = Intent(v.context, RentalActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                requireActivity().recreate()
                parent.overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
            }
    }
}