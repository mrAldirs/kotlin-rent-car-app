package com.example.rental_mobil.View.Pelanggan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.rental_mobil.Admin.RentalPembayaranFragment
import com.example.rental_mobil.databinding.FragmentDialogPembayaranBinding

class DialogPembayaran : DialogFragment() {
    private lateinit var b: FragmentDialogPembayaranBinding
    lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        b = FragmentDialogPembayaranBinding.inflate(layoutInflater)
        v = b.root

        b.btnBayarDp.setOnClickListener {
            val dialog = RiwayatPembayaranFragment()

            val bundle = Bundle()
            bundle.putString("sts", "DP")
            dialog.arguments = bundle

            dialog.show(childFragmentManager, "RiwayatPembayaranFragment")
        }

        b.btnBayarLunas.setOnClickListener {
            val dialog = RiwayatPembayaranFragment()

            val bundle = Bundle()
            bundle.putString("sts", "Lunas")
            dialog.arguments = bundle

            dialog.show(childFragmentManager, "RiwayatPembayaranFragment")
        }

        return v
    }

    override fun dismiss() {
        super.dismiss()
    }
}