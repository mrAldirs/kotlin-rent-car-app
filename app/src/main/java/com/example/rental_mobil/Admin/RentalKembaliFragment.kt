package com.example.rental_mobil.Admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.rental_mobil.R
import com.example.rental_mobil.databinding.FragmentRentalAdminKembaliBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.HashMap

class RentalKembaliFragment : DialogFragment() {
    private lateinit var b: FragmentRentalAdminKembaliBinding
    lateinit var v: View
    lateinit var parent: RentalDetailActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        b = FragmentRentalAdminKembaliBinding.inflate(layoutInflater)
        v = b.root
        parent = activity as RentalDetailActivity

        val idS = arguments?.get("idS").toString()
        val idM = arguments?.get("idM").toString()

        b.btnHitung.setOnClickListener {
            val wkt = b.insWaktuDenda.text.toString().toInt()
            val dnd = b.insBiayaDenda.text.toString().toInt()
            val result = wkt * dnd
            b.insTotal.setText(result.toString())
        }

        b.btnKirim.setOnClickListener {
            val hm = HashMap<String, Any>()
            hm.set("status_rental", "Selesai")
            hm.set("denda", b.insTotal.text.toString())
            hm.set("waktu_denda", b.insWaktuDenda.text.toString())
            hm.set("updated", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))

            FirebaseFirestore.getInstance().collection("rental")
                .document(arguments?.get("id").toString())
                .update(hm)
                .addOnSuccessListener {
                    val hmU = HashMap<String, Any>()
                    hmU.set("status", "tersedia")
                    hmU.set("updated", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))

                    FirebaseFirestore.getInstance().collection("mobil")
                        .document(arguments?.get("idM").toString())
                        .update(hmU)
                        .addOnSuccessListener {
                            if (arguments?.get("idS").toString().isEmpty()) {
                                Toast.makeText(v.context, "Berhasil mengonfirmasi bahwa mobil sudah kembali!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(v.context, RentalActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                                requireActivity().recreate()
                                parent.overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
                            } else {
                                val hmS = HashMap<String, Any>()
                                hmS.set("status", "tersedia")
                                hmS.set("updated", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))

                                FirebaseFirestore.getInstance().collection("sopir")
                                    .document(arguments?.get("idS").toString())
                                    .update(hmS)
                                    .addOnSuccessListener {
                                        Toast.makeText(v.context, "Berhasil mengonfirmasi bahwa mobil sudah kembali!", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(v.context, RentalActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        startActivity(intent)
                                        requireActivity().recreate()
                                        parent.overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
                                    }
                            }
                        }
                }
        }

        return v
    }
}