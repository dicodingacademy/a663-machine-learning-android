package com.dicoding.mymediaplayer

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SolutionMainActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private var mediaRecorder: MediaRecorder? = null
    private var isMediaReady: Boolean = false
    private var isRecording = false
    private var isPlaying = false


    private lateinit var btnPlay: Button
    private lateinit var btnStop: Button
    private lateinit var btnStartRecord: Button
    private lateinit var btnStopRecord: Button

    private lateinit var audioFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        requestPermissionsIfNeeded()
        setClickListeners()
        updateButtonStates()
    }

    private fun initializeViews() {
        btnStartRecord = findViewById(R.id.btn_start_record)
        btnStopRecord = findViewById(R.id.btn_stop_record)
        btnPlay = findViewById(R.id.btn_play)
        btnStop = findViewById(R.id.btn_stop)
    }

    private fun updateButtonStates() {
        btnStartRecord.isEnabled = !isRecording && !isPlaying
        btnStopRecord.isEnabled = isRecording && !isPlaying
        btnPlay.isEnabled = !isRecording && !isPlaying && isMediaReady
        btnStop.isEnabled = isPlaying
    }

    private fun requestPermissionsIfNeeded() {
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
    }

    private fun setClickListeners() {
        btnStartRecord.setOnClickListener { startRecording() }
        btnStopRecord.setOnClickListener { stopRecording() }
        btnPlay.setOnClickListener { handlePlay() }
        btnStop.setOnClickListener { handleStop() }
    }

    private fun startRecording() {
        initializeMediaRecorder()
        mediaRecorder?.start()
        isRecording = true
        updateButtonStates()
    }

    private fun stopRecording() {
        mediaRecorder?.stop()
        mediaRecorder?.reset()
        mediaRecorder = null
        isRecording = false

        if (!isPlaying) {
            isMediaReady = true
            prepareMediaPlayer()
        }
        updateButtonStates()
    }

    private fun initializeMediaRecorder() {
        audioFile = File(cacheDir, "audio.mp3")
        mediaRecorder  = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(this)
        } else {
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(FileOutputStream(audioFile).fd)
            prepare()
        }
    }

    private fun prepareMediaPlayer() {
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(buildAudioAttributes())
            setDataSource(this@SolutionMainActivity, audioFile.toUri())
            setOnPreparedListener {
                isMediaReady = true
                start()
            }
            setOnErrorListener { _, _, _ -> false }
        }
    }

    private fun buildAudioAttributes(): AudioAttributes {
        return AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
    }

    private fun handlePlay() {
        if (!isMediaReady) {
            mediaPlayer?.prepareAsync()
        } else {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            } else {
                mediaPlayer?.start()
            }
        }
        isPlaying = !isPlaying
        updateButtonStates()
    }

    private fun handleStop() {
        if (mediaPlayer?.isPlaying == true || isMediaReady) {
            mediaPlayer?.stop()
            isMediaReady = false
        }
        isPlaying = false
        updateButtonStates()
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSION) == PackageManager.PERMISSION_GRANTED

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            val message = if (isGranted) "Permission granted" else "Permission denied"
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.RECORD_AUDIO
    }
}