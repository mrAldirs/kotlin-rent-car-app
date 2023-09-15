package com.example.rental_mobil.Adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.rental_mobil.Admin.PelangganDetailActivity
import com.example.rental_mobil.Admin.RentalBookingActivity
import com.example.rental_mobil.Admin.RentalDetailActivity
import com.example.rental_mobil.R

class AdapterRentalBooking(val dataRental: List<HashMap<String,String>>, val parent: RentalBookingActivity) :
    RecyclerView.Adapter<AdapterRentalBooking.HolderDataRental>(){
    class HolderDataRental (v : View) : RecyclerView.ViewHolder(v) {
        val nm = v.findViewById<TextView>(R.id.rentalNama)
        val mb = v.findViewById<TextView>(R.id.rentalMobil)
        val tgl = v.findViewById<TextView>(R.id.rentalTanggal)
        val sts = v.findViewById<TextView>(R.id.rentalStatus)
        val dtt = v.findViewById<Button>(R.id.btnDetailRental)
        val del = v.findViewById<Button>(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataRental {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_rental, parent, false)
        return HolderDataRental(v)
    }

    override fun getItemCount(): Int {
        return dataRental.size
    }

    override fun onBindViewHolder(holder: HolderDataRental, position: Int) {
        val data = dataRental.get(position)
        holder.nm.setText(data.get("nama_pelanggan"))
        holder.mb.setText(data.get("nama_mobil"))
        holder.tgl.setText(data.get("tgl_rental"))

        val sts = data.get("status_rental").toString()
        if (sts.equals("Booking")) {
            holder.sts.setText(data.get("status_rental"))
            holder.del.visibility = View.GONE
        } else if (sts.equals("Berjalan")) {
            holder.sts.setText(data.get("status_rental"))
            holder.del.visibility = View.GONE
        } else if (sts.equals("Selesai")) {
            holder.sts.setText(data.get("status_rental"))
            holder.sts.setTextColor(Color.GREEN)
        }

        holder.dtt.setOnClickListener {
            val intent = Intent(it.context, RentalDetailActivity::class.java)
            intent.putExtra("idR", data.get("id_rental").toString())
            intent.putExtra("idM", data.get("id_mobil").toString())
            intent.putExtra("emP", data.get("email_pelanggan").toString())
            it.context.startActivity(intent)
            parent.overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
    }
}