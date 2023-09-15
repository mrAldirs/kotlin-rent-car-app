package com.example.rental_mobil.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rental_mobil.R

class AdapterLaporan  (val dataRental: List<HashMap<String,String>>) :
    RecyclerView.Adapter<AdapterLaporan.HolderDataRental>(){
    class HolderDataRental (v : View) : RecyclerView.ViewHolder(v) {
        val nm = v.findViewById<TextView>(R.id.rentalNama)
        val mb = v.findViewById<TextView>(R.id.rentalMobil)
        val tgl = v.findViewById<TextView>(R.id.rentalTanggal)
        val sts = v.findViewById<TextView>(R.id.rentalStatus)
        val dtt = v.findViewById<Button>(R.id.btnDetailRental)
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

        val nm = data.get(("nama_pelanggan")).toString()
        if (nm.isEmpty()) {

        }
        holder.nm.setText(data.get("nama_pelanggan"))
        holder.mb.setText(data.get("nama_mobil"))
        holder.tgl.setText(data.get("tgl_rental"))

        val sts = data.get("status_rental").toString()
        if (sts.equals("Booking")) {
            holder.sts.setText(data.get("status_rental"))
        } else if (sts.equals("Berjalan")) {
            holder.sts.setText(data.get("status_rental"))
        } else if (sts.equals("Selesai")) {
            holder.sts.setText(data.get("status_rental"))
            holder.sts.setTextColor(Color.GREEN)
        }

        holder.dtt.setOnClickListener {
//            val intent = Intent(it.context, RentalDetailActivity::class.java)
//            intent.putExtra("idR", data.get("id_rental").toString())
//            intent.putExtra("idM", data.get("id_mobil").toString())
//            intent.putExtra("emP", data.get("email_pelanggan").toString())
//            it.context.startActivity(intent)
        }
    }
}