package com.example.rental_mobil.Admin

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rental_mobil.Adapter.AdapterLaporan
import com.example.rental_mobil.Adapter.AdapterRentalRiwayat
import com.example.rental_mobil.Model.Mobil
import com.example.rental_mobil.Model.Pelanggan
import com.example.rental_mobil.Model.Riwayat
import com.example.rental_mobil.R
import com.example.rental_mobil.Utils.PdfUtility
import com.example.rental_mobil.databinding.ActivityLaporanBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File

class LaporanActivity : AppCompatActivity(), PdfUtility.OnDocumentClose {
    private lateinit var b: ActivityLaporanBinding

    val dataRental = mutableListOf<HashMap<String,String>>()
    val dataPelanggan = mutableListOf<Pelanggan>()
    val dataMobil = mutableListOf<Mobil>()
    val dataRiwayat = mutableListOf<Riwayat>()
    lateinit var rentalAdp : AdapterLaporan

    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLaporanBinding.inflate(layoutInflater)
        setContentView(b.root)
        supportActionBar?.setTitle("Laporan Rental")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // width: 1080 height:1956

        rentalAdp = AdapterLaporan(dataRental)
        b.recycleView.layoutManager = LinearLayoutManager(this)
        b.recycleView.adapter = rentalAdp

        b.fabPdf.setOnClickListener {
            var file:File = if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S){
                File(Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOCUMENTS),"/${System.currentTimeMillis()}.pdf")
            }else{
                File(Environment.getExternalStorageDirectory(), "/${System.currentTimeMillis()}.pdf")
            }

//            var templatePdf = TemplatePdf(this@PdfActivity)
//            templatePdf.generatePdf(pelanggan,mobil,riwayat, file, b.templatePdf.width, b.templatePdf.height)

            try
            {
                PdfUtility.createPdf(this@LaporanActivity,this,getSampleData(dataPelanggan, dataMobil, dataRiwayat),file.path,false);
            }
            catch (e:Exception)
            {
                e.printStackTrace();
                Log.e("TAG","Error Creating Pdf");
                Toast.makeText(this,"Error Creating Pdf", Toast.LENGTH_SHORT).show();
            }
        }

        db = FirebaseFirestore.getInstance()
        showData()
    }

    override fun onPDFDocumentClose(file: File?) {
        Toast.makeText(this,"Laporan berhasil diunduh!",Toast.LENGTH_SHORT).show();
    }
    private fun getSampleData(dataPelanggan: MutableList<Pelanggan>,
                              dataMobil: MutableList<Mobil>,
                              dataRiwayat: MutableList<Riwayat>): MutableList<Array<String>> {
        var count = 20

        var temp: MutableList<Array<String>>  = mutableListOf()
        for (i in 0 until dataRiwayat.size) {
            temp.add(arrayOf(dataPelanggan[i].nama, dataMobil[i].plat,dataRiwayat[i].mulai_rental,dataRiwayat[i].selesai_rental,dataMobil[i].harga,dataRiwayat[i].denda,dataRiwayat[i].total))
        }
        return temp
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
    }

    private fun showData() {
        db.collection("rental")
            .whereEqualTo("deleted", "")
            .whereEqualTo("status_rental", "Selesai")
            .get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    val hm = HashMap<String, String>()
                    hm.put("id_rental", doc.get("id_rental").toString())
                    hm.put("id_mobil", doc.get("id_mobil").toString())
                    hm.put("id_pelanggan", doc.get("id_pelanggan").toString())
                    hm.put("tgl_rental", doc.get("tgl_rental").toString())
                    hm.put("status_rental", doc.get("status_rental").toString())

                    dataRental.add(hm)
                    dataRiwayat.add(Riwayat(denda = doc.get("denda").toString(), mulai_rental = doc.get("mulai_rental").toString(), selesai_rental = doc.get("selesai_rental").toString(), total = doc.get("total").toString()))
                }

                val idMobilList = dataRental.map { it.get("id_mobil").toString() }
                if (idMobilList.isNotEmpty()) {
                    val mobilRef = db.collection("mobil")
                        .whereIn("id_mobil", idMobilList)
                    mobilRef.get()
                        .addOnSuccessListener {mobilResult ->
                            for (mobilDoc in mobilResult) {
                                val idMobil = mobilDoc.get("id_mobil").toString()
                                dataRental.forEach { rental ->
                                    if (rental["id_mobil"] == idMobil) {
                                        rental.put("nama_mobil", mobilDoc.get("nama").toString())

                                        dataMobil.add(Mobil(nama=mobilDoc.get("nama").toString(),plat= mobilDoc.get("plat").toString(), harga = mobilDoc.get("harga").toString()))
                                    }
                                }
                            }
                        }
                        .addOnFailureListener {

                        }
                }

                val idPelangganList = dataRental.map { it.get("id_pelanggan").toString() }

                if (idPelangganList.isNotEmpty()) {
                    val pelangganRef = db.collection("pelanggan")
                        .whereIn("id_pelanggan", idPelangganList)
                    pelangganRef.get()
                        .addOnSuccessListener { pelangganResult ->
                            for (pelangganDoc in pelangganResult) {
                                val idPelanggan = pelangganDoc.get("id_pelanggan").toString()

                                dataRental.forEach { rental ->
                                    if (rental["id_pelanggan"] == idPelanggan) {
                                        rental.put("nama_pelanggan", pelangganDoc.get("nama").toString())
                                        rental.put("email_pelanggan", pelangganDoc.get("email").toString())
                                    dataPelanggan.add(Pelanggan(nama = pelangganDoc.get("nama").toString()))
                                    }
                                }
                            }
                            rentalAdp.notifyDataSetChanged()
                        }
                        .addOnFailureListener {

                        }
                }
            }
            .addOnFailureListener { exception ->

            }
    }
}