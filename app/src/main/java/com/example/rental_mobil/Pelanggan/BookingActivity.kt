package com.example.rental_mobil.Pelanggan

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rental_mobil.R
import com.example.rental_mobil.databinding.ActivityBookingBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BookingActivity : AppCompatActivity() {
    lateinit var b: ActivityBookingBinding

    var idM = ""
    var spr = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(b.root)
        supportActionBar?.setTitle("Booking Mobil")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var frag = PilihMobilFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, frag).commit()
        b.frameLayout.setBackgroundColor(Color.argb(255,255,255,255))

        b.insTglRental.setOnClickListener {
            showDatePickerDialogRental()
        }

        b.insTglMulai.setOnClickListener {
            showDatePickerDialog()
        }

        b.insJamMulai.setOnClickListener {
            showTimePickerDialog()
        }

        b.rg.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.rbSopir -> {
                    cariSopir()
                }
                R.id.rbTidakSopir -> spr = ""
            }
        }

        b.btnSelanjutnya.setOnClickListener {
            val intent = Intent(this, BookingKonfirmasiActivity::class.java)
            intent.putExtra("tgl_rental", b.insTglMulai.text.toString())
            intent.putExtra("hari", b.insHari.text.toString())
            intent.putExtra("tgl_mulai", b.insJamMulai.text.toString()+" "+b.insTglMulai.text.toString())
            intent.putExtra("tgl_selesai", b.insJamSelesai.text.toString()+" "+b.insTglSelesai.text.toString())
            intent.putExtra("idM", idM)
            intent.putExtra("idS", spr)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
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

    private fun showDatePickerDialogRental() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val date = "$dayOfMonth-${month + 1}-$year"
                b.insTglRental.setText(date)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, dayOfMonth)
                val date = "$dayOfMonth-${month + 1}-$year"
                b.insTglMulai.setText(date)

                val dateFormat = SimpleDateFormat("d-M-yyyy", Locale.getDefault())
                val tanggalAwal = dateFormat.parse(date) // Mengubah String menjadi Date

                val jumlahHari = b.insHari.text.toString().toInt() // Jumlah hari yang ingin ditambahkan

                val tanggalAkhir = tambahTanggal(tanggalAwal, jumlahHari)
                b.insTglSelesai.setText(tanggalAkhir.toString())
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    fun tambahTanggal(tanggalAwal: Date?, jumlahHari: Int): String? {
        if (tanggalAwal != null) {
            val calendar = Calendar.getInstance()
            calendar.time = tanggalAwal
            calendar.add(Calendar.DAY_OF_YEAR, jumlahHari)
            val dateFormat = SimpleDateFormat("d-M-yyyy", Locale.getDefault())
            return dateFormat.format(calendar.time)
        }
        return null
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val time = String.format("%02d:%02d", hourOfDay, minute)
                b.insJamMulai.setText(time)
                b.insJamSelesai.setText(time)
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }

    fun cariSopir() {
        FirebaseFirestore.getInstance().collection("sopir")
            .whereEqualTo("status", "tersedia")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    if (!result.isEmpty) {
                        for (doc in result) {
                            val st1 = doc.get("id_sopir").toString()
                            spr = st1
                        }
                    } else {
                        spr = ""
                        Toast.makeText(this, "Sopir tidak ada yang tersedia!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e(TAG, "Gagal mendapatkan dokumen: ${task.exception}")
                }
            }
    }
}