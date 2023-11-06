package com.dicoding.picodiploma.mycamera

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.mycamera.databinding.ActivityCameraBinding

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Digunakan untuk mengganti kamera
        binding.switchCamera.setOnClickListener {
            startCamera()
        }

        // Digunakan untuk mengambil gambar/capture
        binding.captureImage.setOnClickListener { takePhoto() }
    }

    // Pemanggilan function agar tampilan fullscreen dan memunculkan Camera X
    public override fun onResume() {
        super.onResume()
        hideSystemUI()
        startCamera()
    }

    // Function untuk memunculkan Camera X
    private fun startCamera() {
        // showCamera
    }

    // Function untuk mengambil gambar
    private fun takePhoto() {
        // takePhoto
    }

    // Digunakan agar tampilan CameraX bisa full screen
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
}