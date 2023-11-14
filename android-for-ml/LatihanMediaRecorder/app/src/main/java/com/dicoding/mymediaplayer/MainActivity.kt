package com.dicoding.mymediaplayer

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var btnPlay: Button
    private lateinit var btnStop: Button
    private lateinit var btnStartRecord: Button
    private lateinit var btnStopRecord: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnPlay = findViewById(R.id.btn_play)
        btnStop = findViewById(R.id.btn_stop)
        btnStartRecord = findViewById(R.id.btn_start_record)
        btnStopRecord = findViewById(R.id.btn_stop_record)

        val audioFile = File(cacheDir, "audio.mp3")

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        val audioPlayer = AudioPlayer(this)
        val audioRecorder = AudioRecorder(this)

        isPlay(false)
        isRecord(false)

        btnPlay.setOnClickListener {
            isPlay(audioPlayer.playFile(audioFile))
        }
        btnStop.setOnClickListener {
            isPlay(audioPlayer.stop())
        }

        btnStartRecord.setOnClickListener {
            isRecord(audioRecorder.start(audioFile))
        }

        btnStopRecord.setOnClickListener {
            isRecord(audioRecorder.stop())
        }
    }

    private fun isRecord(state: Boolean) {
        if (state){
            btnStartRecord.isEnabled = false
            btnStopRecord.isEnabled = true
        } else {
            btnStartRecord.isEnabled = true
            btnStopRecord.isEnabled = false
        }
    }
    private fun isPlay(state: Boolean) {
        if (state){
            btnPlay.text = getString(R.string.btn_text_pause)
            btnStop.isEnabled = true
        } else {
            btnPlay.text = getString(R.string.btn_text_play)
            btnStop.isEnabled = false
        }
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.RECORD_AUDIO
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        this, REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
        }
    }
}