package com.example.rental_mobil

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.rental_mobil.Pelanggan.BookingKonfirmasiActivity
import com.example.rental_mobil.Utils.KTPChecker
import com.example.rental_mobil.Utils.TextAnalyser
import com.example.rental_mobil.databinding.ActivityOcrBinding
import com.google.mlkit.nl.entityextraction.EntityExtractorOptions
import com.example.rental_mobil.Utils.customviews.ProgressDialog
import com.snatik.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
typealias CameraTextAnalyzerListener = (text: String) -> Unit

class OCRActivity : AppCompatActivity() {

    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraControl: CameraControl
    private lateinit var cameraInfo: CameraInfo
    private var currentLanguage = EntityExtractorOptions.ENGLISH
    private val executor by lazy {
        Executors.newSingleThreadExecutor()
    }
    private lateinit var progressDialog: ProgressDialog
    lateinit var binding : ActivityOcrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOcrBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        requestAllPermissions()

        progressDialog = ProgressDialog(this, false)
//        currentLanguage = EntityExtractorOptions.ENGLISH

        binding.viewFinder.post {
            startCamera()
        }
        binding.ivImageCapture.setOnClickListener {
            progressDialog.show()
            takePicture()
        }
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

    @Suppress("SameParameterValue")
    private fun createFile(baseFolder: File, format: String, extension: String) =
        File(
            baseFolder, SimpleDateFormat(format, Locale.US)
                .format(System.currentTimeMillis()) + extension
        )

    private fun takePicture() {

        val file = createFile(
            getOutputDirectory(
                this
            ),
            "yyyy-MM-dd-HH-mm-ss-SSS",
            ".png"
        )
        val outputFileOptions =
            ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture.takePicture(
            outputFileOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {


                    // sending the captured image for analysis
                    GlobalScope.launch(Dispatchers.IO) {
                        TextAnalyser({ result ->
                            if (result.isEmpty()) {

                                progressDialog.dismiss()
                                Toast.makeText(
                                  baseContext,
                                    "No Text Detected",
                                    Toast.LENGTH_SHORT
                                ).show()


                            } else {
                                progressDialog.dismiss()
                                val listResult = result.replace("\n", " ").split(" ")
                                Log.e("result-ocr", listResult.toString())
//                                Log.e("result-ocr-nik", KTPChecker.checkNik(listResult))
//                                Log.e("result-ocr-nama", KTPChecker.checkNama(listResult))
//                                Log.e("result-ocr-tanggal_lahir", KTPChecker.checkTanggalLahir(listResult))
//                                Log.e("result-ocr-jenisKelmain", KTPChecker.checkJenisKelamin(listResult))
//                                Log.e("result-ocr-agama", KTPChecker.checkAgama(listResult))
                                val intent = Intent(baseContext, SignUpActivity::class.java)
                                intent.putExtra("ocr-nik",  KTPChecker.checkNik(listResult))
                                intent.putExtra("ocr-nama", KTPChecker.checkNama(listResult))
                                intent.putExtra("ocr-tanggal_lahir", KTPChecker.checkTanggalLahir(listResult))
                                intent.putExtra("ocr-jenisKelmain", KTPChecker.checkJenisKelamin(listResult))
                                intent.putExtra("ocr-agama", KTPChecker.checkAgama(listResult))
//                                send image ktp
                                intent.putExtra("ocr-url-ktp", file.absolutePath)
                                startActivity(intent).apply {
                                    finish()
                                }
                            }

                        }, baseContext, Uri.fromFile(file)).analyseImage()

                    }
                }

                override fun onError(exception: ImageCaptureException) {

                    progressDialog.dismiss()
                    Log.e("error", exception.localizedMessage!!)
                }
            })
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun startCamera() {


        // Get screen metrics used to setup camera for full screen resolution
        val metrics = DisplayMetrics().also { binding.viewFinder.display.getRealMetrics(it) }

        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)

        val rotation = binding.viewFinder.display.rotation


        // Bind the CameraProvider to the LifeCycleOwner
        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(baseContext)
        cameraProviderFuture.addListener({

            // CameraProvider
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                // Set initial target rotation
                .setTargetRotation(rotation)

                .build()

            preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)

            // ImageCapture
            imageCapture = initializeImageCapture(screenAspectRatio, rotation)

            // ImageAnalysis


            cameraProvider.unbindAll()

            try {
                val camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
                cameraControl = camera.cameraControl
                cameraInfo = camera.cameraInfo
                cameraControl.setLinearZoom(0.5f)


            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(baseContext))


    }

    private fun getOutputDirectory(context: Context): File {

        val storage = Storage(context)
        val mediaDir = storage.internalCacheDirectory?.let {
            File(it, "KTP OCR").apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
    }

  val RATIO_4_3_VALUE = 4.0 / 3.0
     val RATIO_16_9_VALUE = 16.0 / 9.0
    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }


    private fun initializeImageCapture(
        screenAspectRatio: Int,
        rotation: Int
    ): ImageCapture {
        return ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()
    }

}