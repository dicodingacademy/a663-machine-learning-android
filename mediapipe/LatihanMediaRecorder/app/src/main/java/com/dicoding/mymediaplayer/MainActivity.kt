package com.dicoding.mymediaplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.mymediaplayer.databinding.ActivityMainBinding
import java.text.NumberFormat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private var isRecording = false
    private lateinit var binding: ActivityMainBinding
    private lateinit var backgroundExecutor: ExecutorService
    private lateinit var audioClassifierHelper: AudioClassifierHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissionsIfNeeded()
        updateButtonStates()
        setClickListener()
        initializeAudioClassifierHelper()
    }

    private fun initializeAudioClassifierHelper() {
        backgroundExecutor = Executors.newSingleThreadExecutor()
        backgroundExecutor.execute {
            audioClassifierHelper = AudioClassifierHelper(
                context = this,
                classifierListener = object : AudioClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        Toast.makeText(this@MainActivity, error, Toast.LENGTH_LONG).show()
                    }

                    override fun onResult(resultBundle: AudioClassifierHelper.ResultBundle) {
                        runOnUiThread {
                            resultBundle.results[0].classificationResults().first().let { it ->
                                if (it.classifications()[0].categories().isNotEmpty()) {
                                    println(it)
                                    val sortedCategories =
                                        it.classifications()[0].categories()
                                            .sortedByDescending { it?.score() }
                                    val displayResult =
                                        sortedCategories.joinToString("\n") {
                                            "${it.categoryName()} " + NumberFormat.getPercentInstance()
                                                .format(it.score()).trim()
                                        }
                                    binding.tvResult.text = displayResult
                                } else {
                                    binding.tvResult.text = ""
                                }
                            }
                        }
                    }
                }
            )
        }
    }

    private fun setClickListener() {
        binding.btnStart.setOnClickListener {
            audioClassifierHelper.startAudioClassification()
            isRecording = true
            updateButtonStates()
        }
        binding.btnStop.setOnClickListener {
            audioClassifierHelper.stopAudioClassification()
            isRecording = false
            updateButtonStates()
        }
    }

    private fun updateButtonStates() {
        binding.btnStart.isEnabled = !isRecording
        binding.btnStop.isEnabled = isRecording
    }

    override fun onResume() {
        super.onResume()
        backgroundExecutor.execute {
            if (audioClassifierHelper.isClosed()) {
                audioClassifierHelper.initClassifier()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        backgroundExecutor.execute {
            if (::audioClassifierHelper.isInitialized) {
                audioClassifierHelper.stopAudioClassification()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        backgroundExecutor.shutdown()
    }

    private fun requestPermissionsIfNeeded() {
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            val message = if (isGranted) "Permission granted" else "Permission denied"
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.RECORD_AUDIO
    }
}