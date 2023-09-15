package com.example.rental_mobil.Pelanggan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.rental_mobil.databinding.FragmentBookingFinishBinding

class BookingFinishFragment : DialogFragment() {
    private lateinit var b: FragmentBookingFinishBinding
    lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        b = FragmentBookingFinishBinding.inflate(layoutInflater)
        v = b.root

        b.btnBack.setOnClickListener {
            val intent = Intent(v.context, DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        return v
    }
}