package com.dicoding.mymediaplayer

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private var isReady: Boolean = false

    private lateinit var btnPlay: Button
    private lateinit var btnStop: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnPlay = findViewById(R.id.btn_play)
        btnStop = findViewById(R.id.btn_stop)

        initMediaPlayer()
//        isPlay(false)

        btnPlay.setOnClickListener {
            play()
        }
        btnStop.setOnClickListener {
            stop()
        }
    }

    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer()
        val attribute = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        mediaPlayer?.setAudioAttributes(attribute)
        val afd = applicationContext.resources.openRawResourceFd(R.raw.guitar_background)
        try {
            mediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
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
//            isPlay(true)
        } else {
            if (mediaPlayer?.isPlaying as Boolean) {
                mediaPlayer?.pause()
//                isPlay(false)
            } else {
                mediaPlayer?.start()
//                isPlay(true)
            }
        }
    }

    private fun stop() {
        if (mediaPlayer?.isPlaying as Boolean || isReady) {
            mediaPlayer?.stop()
            isReady = false
//            isPlay(false)
        }
    }
//
//    private fun isPlay(state: Boolean) {
//        if (state){
//            btnPlay.text = getString(R.string.btn_text_pause)
//            btnStop.isEnabled = true
//        } else {
//            btnPlay.text = getString(R.string.btn_text_play)
//            btnStop.isEnabled = false
//        }
//    }
}