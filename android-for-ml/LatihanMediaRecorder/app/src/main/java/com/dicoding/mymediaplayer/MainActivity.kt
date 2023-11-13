package com.dicoding.mymediaplayer

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private var mMediaPlayer: MediaPlayer? = null
    private var mMediaRecorder: MediaRecorder? = null

    private var isReady: Boolean = false

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

        initMediaRecorder()

        btnStartRecord.setOnClickListener {
            startRecording()
        }
        btnStopRecord.setOnClickListener {
            stopRecording()
        }

        btnPlay.setOnClickListener {
            play()
        }
        btnStop.setOnClickListener {
            stop()
        }

    }


    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.RECORD_AUDIO
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun initMediaRecorder() {

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        mMediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(this)
        } else {
            MediaRecorder()
        }

        mMediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mMediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mMediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

        val outputFileName = "${externalCacheDir?.absolutePath}/recorded_audio.3gp"

        mMediaRecorder?.setOutputFile(outputFileName)
    }

    private fun startRecording() {
        try {
            mMediaRecorder?.prepare()
            mMediaRecorder?.start()
            isRecording(true)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        mMediaRecorder?.stop()
        mMediaRecorder?.release()
        mMediaRecorder = null
        isRecording(false)
        init()
    }

    private fun isRecording(state: Boolean) {
        if (state){
            btnStartRecord.isEnabled = false
            btnStopRecord.isEnabled = true
        } else {
            btnStartRecord.isEnabled = true
            btnStopRecord.isEnabled = false
        }
    }

    private fun init() {
        mMediaPlayer = MediaPlayer()
        mMediaPlayer?.reset()

        val attribute = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        mMediaPlayer?.setAudioAttributes(attribute)
        try {
            val outputFileName = "${externalCacheDir?.absolutePath}/recorded_audio.3gp"
            mMediaPlayer?.setDataSource(outputFileName)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        mMediaPlayer?.setOnPreparedListener {
            isReady = true
            mMediaPlayer?.start()
        }
        mMediaPlayer?.setOnErrorListener { _, _, _ -> false }
    }

    private fun play() {
        if (!isReady) {
            mMediaPlayer?.prepareAsync()
            isPlaying(true)
        } else {
            if (mMediaPlayer?.isPlaying as Boolean) {
                mMediaPlayer?.pause()
                isPlaying(false)
            } else {
                mMediaPlayer?.start()
                isPlaying(true)
            }
        }
    }

    private fun stop() {
        if (mMediaPlayer?.isPlaying as Boolean || isReady) {
            mMediaPlayer?.stop()
            isReady = false
            isPlaying(false)
        }
    }

    private fun isPlaying(state: Boolean) {
        if (state){
            btnPlay.text = getString(R.string.btn_text_pause)
            btnStop.isEnabled = true
        } else {
            btnPlay.text = getString(R.string.btn_text_play)
            btnStop.isEnabled = false
        }
    }
}