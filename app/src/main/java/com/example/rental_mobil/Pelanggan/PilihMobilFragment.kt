package com.example.rental_mobil.Pelanggan

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rental_mobil.R
import com.example.rental_mobil.databinding.FragmentPilihMobilBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class PilihMobilFragment : Fragment() {
    private lateinit var b: FragmentPilihMobilBinding
    lateinit var v : View
    lateinit var parent: BookingActivity

    val dataMobil = mutableListOf<HashMap<String,String>>()
    lateinit var mobilAdp : AdapterMobil

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parent = activity as BookingActivity
        b = FragmentPilihMobilBinding.inflate(layoutInflater)
        v = b.root

        mobilAdp = AdapterMobil(dataMobil, parent)
        b.rvMobil.layoutManager = GridLayoutManager(v.context, 2)
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing) // Mengambil nilai spacing dari dimens.xml

        val includeEdge = true // Atur true jika Anda ingin padding pada tepi luar grid
        val itemDecoration = GridSpacingItemDecoration(2, spacingInPixels, includeEdge)
        b.rvMobil.addItemDecoration(itemDecoration)
        b.rvMobil.adapter = mobilAdp

        showData()

        return v
    }

    fun showData() {
        FirebaseFirestore.getInstance().collection("mobil")
            .whereEqualTo("deleted", "")
            .get()
            .addOnSuccessListener { result ->
                dataMobil.clear()
                for (doc in result) {
                    var hm = HashMap<String, String>()
                    hm.put("id", doc.get("id_mobil").toString())
                    hm.put("nama", doc.get("nama").toString())
                    hm.put("foto_mobil", doc.get("foto_mobil").toString())
                    hm.put("harga", doc.get("harga").toString())
                    hm.put("status", doc.get("status").toString())

                    dataMobil.add(hm)
                }
                mobilAdp.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    class GridSpacingItemDecoration(private val spanCount: Int, private val spacing: Int, private val includeEdge: Boolean) :
        RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view) // Mendapatkan posisi item
            val column = position % spanCount // Mendapatkan kolom saat ini

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount
                outRect.right = (column + 2) * spacing / spanCount

                if (position < spanCount) {
                    outRect.top = spacing
                }
                outRect.bottom = spacing
            } else {
                outRect.left = column * spacing / spanCount
                outRect.right = spacing - (column + 2) * spacing / spanCount

                if (position >= spanCount) {
                    outRect.top = spacing
                }
            }
        }
    }

    class AdapterMobil(val dataMobil: List<HashMap<String,String>>, val parent: BookingActivity) :
        RecyclerView.Adapter<AdapterMobil.HolderDataMobil>(){
        class HolderDataMobil (v : View) : RecyclerView.ViewHolder(v) {
            val ft = v.findViewById<ImageView>(R.id.mobilFoto)
            val hrg = v.findViewById<TextView>(R.id.mobilHarga)
            val nm = v.findViewById<TextView>(R.id.mobilNama)
            val dt = v.findViewById<Button>(R.id.btnDetailMobil)
            val plh = v.findViewById<Button>(R.id.btnPilihMobil)
            val cd = v.findViewById<CardView>(R.id.card)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataMobil {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.row_pilih_mobil, parent, false)
            return HolderDataMobil(v)
        }

        override fun getItemCount(): Int {
            return dataMobil.size
        }

        override fun onBindViewHolder(holder: HolderDataMobil, position: Int) {
            val data = dataMobil.get(position)
            holder.nm.setText(data.get("nama"))
            holder.hrg.setText("Rp."+data.get("harga"))
            Picasso.get().load(data.get("foto_mobil")).into(holder.ft)

            val sts = data.get("status").toString()
            if (sts.equals("tidak tersedia")) {
                holder.cd.setCardBackgroundColor(Color.parseColor("#D1D1D1"))
                holder.dt.setBackgroundColor(Color.parseColor("#5C5C5C"))
                holder.plh.setBackgroundColor(Color.parseColor("#5C5C5C"))
            } else {
                holder.dt.setOnClickListener {
                    val dialog = MobilDetailFragment()

                    val bundle = Bundle()
                    bundle.putString("id", data.get("id").toString())
                    dialog.arguments = bundle
                    dialog.show(parent.supportFragmentManager, "MobilDetailFragment")
                }

                holder.plh.setOnClickListener {
                    parent.idM = data.get("id").toString()
                    parent.b.insMobil.setText((data.get("nama").toString()))
                }
            }
        }
    }
}