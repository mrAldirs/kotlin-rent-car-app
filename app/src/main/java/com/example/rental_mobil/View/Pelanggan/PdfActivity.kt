package com.example.rental_mobil.View.Pelanggan

import android.Manifest
import android.R.attr.rowCount
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.rental_mobil.Model.Mobil
import com.example.rental_mobil.Model.Pelanggan
import com.example.rental_mobil.Model.Riwayat
import com.example.rental_mobil.R
import com.example.rental_mobil.Utils.PdfUtility
import com.example.rental_mobil.Utils.TemplatePdf
import com.example.rental_mobil.databinding.ActivityPdfBinding
import java.io.File
import java.util.ArrayList
import java.util.List


class PdfActivity : AppCompatActivity(), PdfUtility.OnDocumentClose {
    private lateinit var b: ActivityPdfBinding
    private lateinit var mobil:Mobil
    private lateinit var pelanggan:Pelanggan
    private lateinit var riwayat:Riwayat
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPdfBinding.inflate(layoutInflater)
        setContentView(b.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mobil = intent.getSerializableExtra("mobil") as Mobil
        pelanggan = intent.getSerializableExtra("pelanggan") as Pelanggan
        riwayat = intent.getSerializableExtra("riwayat") as Riwayat

        showPreview()
        b.fabDownload.setOnClickListener {

            var file:File = if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S){
                File(Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOCUMENTS),"/${System.currentTimeMillis()}.pdf")
            }else{
                File(Environment.getExternalStorageDirectory(), "/${System.currentTimeMillis()}.pdf")
            }

//            var templatePdf = TemplatePdf(this@PdfActivity)
//            templatePdf.generatePdf(pelanggan,mobil,riwayat, file, b.templatePdf.width, b.templatePdf.height)

            try
            {
                PdfUtility.createPdf(this@PdfActivity,this,getSampleData(),file.path,true);
            }
            catch (e:Exception)
            {
                e.printStackTrace();
                Log.e("TAG","Error Creating Pdf");
                Toast.makeText(this,"Error Creating Pdf",Toast.LENGTH_SHORT).show();
            }
        }





        requestAllPermissions();
    }

    private fun getSampleData(): MutableList<Array<String>> {
        var count = 20

        var temp: MutableList<Array<String>>  = mutableListOf()
            for (i in 0 until count) {
                temp.add(arrayOf("C1-R" + (i + 1), "C2-R" + (i + 1)))
            }
            return temp
        }


    private val multiPermissionCallback =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            if ( map.entries.size <3){
                Toast.makeText(this, "Please Accept all the permissions", Toast.LENGTH_SHORT).show()
            }

        }

    private fun requestAllPermissions(){
        multiPermissionCallback.launch(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        )
    }

    override fun onPDFDocumentClose(file: File?) {
        Toast.makeText(this,"Sample Pdf Created",Toast.LENGTH_SHORT).show();
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

    private fun showPreview() {
        b.templatePdf.setBackgroundPaint(1)
        b.templatePdf.setData(pelanggan,riwayat,mobil)
        b.templatePdf.invalidate()
    }

}