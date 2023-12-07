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

class MainActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private var mediaRecorder: MediaRecorder? = null
    private var isReady: Boolean = false


    private lateinit var btnPlay: Button
    private lateinit var btnStop: Button
    private lateinit var btnStartRecord: Button
    private lateinit var btnStopRecord: Button


    private lateinit var audioFile: File


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btnStartRecord = findViewById(R.id.btn_start_record)
        btnStopRecord = findViewById(R.id.btn_stop_record)

        btnPlay = findViewById(R.id.btn_play)
        btnStop = findViewById(R.id.btn_stop)


        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }


        btnStartRecord.setOnClickListener {
            startRecord()
        }


        btnStopRecord.setOnClickListener {
            stopRecord()
        }


        btnPlay.setOnClickListener {
            play()
        }
        btnStop.setOnClickListener {
            stop()
        }
    }


    private fun initMediaRecorder() {
        audioFile = File(cacheDir, "audio.mp3")


        mediaRecorder = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(this)
        } else MediaRecorder()


        mediaRecorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(FileOutputStream(audioFile).fd)
            prepare()
        }
    }


    private fun startRecord() {
        initMediaRecorder()
        mediaRecorder?.start()
    }


    private fun stopRecord() {
        mediaRecorder?.stop()
        mediaRecorder?.reset()
        mediaRecorder = null
        initMediaPlayer()
    }


    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer()
        val attribute = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        mediaPlayer?.setAudioAttributes(attribute)
        try {
            mediaPlayer?.setDataSource(this, audioFile.toUri())
        } catch (e: IOException) {
            e.printStackTrace()
        }


        mediaPlayer?.setOnPreparedListener {
            isReady = true
            mediaPlayer?.start()
        }
        mediaPlayer?.setOnErrorListener { _, _, _ -> false }
    }


    private fun play() {
        if (!isReady) {
            mediaPlayer?.prepareAsync()
        } else {
            if (mediaPlayer?.isPlaying as Boolean) {
                mediaPlayer?.pause()
            } else {
                mediaPlayer?.start()
            }
        }
    }


    private fun stop() {
        if (mediaPlayer?.isPlaying as Boolean || isReady) {
            mediaPlayer?.stop()
            isReady = false
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