package com.example.rental_mobil.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.rental_mobil.Admin.PelangganActivity
import com.example.rental_mobil.Admin.PelangganDetailActivity
import com.example.rental_mobil.ImageDetailActivity
import com.example.rental_mobil.R

class AdapterPelanggan(val dataPelanggan: List<HashMap<String,String>>, val parent: PelangganActivity) :
    RecyclerView.Adapter<AdapterPelanggan.HolderDataPelanggan>(){
    class HolderDataPelanggan(v : View) : RecyclerView.ViewHolder(v) {
        val nm = v.findViewById<TextView>(R.id.pelangganNama)
        val nik = v.findViewById<TextView>(R.id.pelangganNik)
        val ktp = v.findViewById<Button>(R.id.btnKtpPelanggan)
        val hps = v.findViewById<Button>(R.id.btnHapusPelanggan)
        val cd = v.findViewById<CardView>(R.id.cardPelanggan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataPelanggan {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_pelanggan, parent, false)
        return HolderDataPelanggan(v)
    }

    override fun getItemCount(): Int {
        return dataPelanggan.size
    }

    override fun onBindViewHolder(holder: HolderDataPelanggan, position: Int) {
        val  data = dataPelanggan.get(position)
        holder.nm.setText(data.get("nama"))
        holder.nik.setText(data.get("nik"))
        holder.ktp.setOnClickListener {
            val intent = Intent(it.context, ImageDetailActivity::class.java)
            intent.putExtra("img", data.get("ktp").toString())
            it.context.startActivity(intent)
            parent.overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }

        holder.cd.setOnClickListener {
            val intent = Intent(it.context, PelangganDetailActivity::class.java)
            intent.putExtra("email", data.get("email").toString())
            it.context.startActivity(intent)
            parent.overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }

        holder.hps.setOnClickListener {
            parent.em = data.get("email").toString()
            parent.deletePelanggan()
        }
    }
}