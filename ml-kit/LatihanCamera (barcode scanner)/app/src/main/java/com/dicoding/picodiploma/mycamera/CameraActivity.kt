package com.dicoding.picodiploma.mycamera

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.dicoding.picodiploma.mycamera.databinding.ActivityCameraBinding
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode


class CameraActivity : AppCompatActivity() {
    private lateinit var barcodeScanner: BarcodeScanner
    private lateinit var binding: ActivityCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUI()
        startCamera()
    }

    private fun startCamera() {
        val cameraController = LifecycleCameraController(baseContext)
        val previewView: PreviewView = binding.viewFinder

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        barcodeScanner = BarcodeScanning.getClient(options)
        var firstCall = true

        cameraController.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(this),
            MlKitAnalyzer(
                listOf(barcodeScanner),
                COORDINATE_SYSTEM_VIEW_REFERENCED,
                ContextCompat.getMainExecutor(this)
            ) { result: MlKitAnalyzer.Result? ->

                if (firstCall) {
                    val barcodeResults = result?.getValue(barcodeScanner)

                    val alertDialog = AlertDialog.Builder(this)

                    if ((barcodeResults != null) &&
                        (barcodeResults.size != 0) &&
                        (barcodeResults.first() != null)
                    ) {
                        firstCall = false
                        val barcode = barcodeResults[0]

                        alertDialog.setTitle("Hasil Scan")
                            .setMessage(barcode.rawValue)
                            .setPositiveButton(
                                "Buka"
                            ) { p0, p1 ->
                                when (barcode.valueType) {
                                    Barcode.TYPE_URL -> {
                                        val openBrowserIntent = Intent(Intent.ACTION_VIEW)
                                        openBrowserIntent.data = Uri.parse(barcode.url?.url)
                                        startActivity(openBrowserIntent)
                                    }

                                    else -> {
                                        Toast.makeText(
                                            this,
                                            "Unsupported data type",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        startCamera()
                                    }
                                }
                            }
                            .setNegativeButton("Scan lagi"){_, _ ->
                                firstCall = true
                            }
                            .create()
                        alertDialog
                            .setCancelable(false)
                            .show()
                    }
                }

            }
        )

        cameraController.bindToLifecycle(this)
        previewView.controller = cameraController
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    companion object {
        private const val TAG = "CameraActivity"
        const val EXTRA_CAMERAX_IMAGE = "CameraX Image"
        const val CAMERAX_RESULT = 200
    }
}