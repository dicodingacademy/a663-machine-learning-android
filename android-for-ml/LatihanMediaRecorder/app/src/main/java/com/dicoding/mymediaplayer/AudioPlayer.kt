package com.dicoding.mymediaplayer

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.core.net.toUri
import java.io.File
import java.io.IOException

class AudioPlayer(
    private val context: Context
): AudioPlayerInterface {
    private var mMediaPlayer: MediaPlayer? = null
    private var isReady: Boolean = false

    override fun playFile(file: File): Boolean {
        mMediaPlayer = MediaPlayer()
        val attribute = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        mMediaPlayer?.setAudioAttributes(attribute)
        try {
            mMediaPlayer?.setDataSource(context, file.toUri())
        } catch (e: IOException) {
            e.printStackTrace()
        }

        mMediaPlayer?.setOnPreparedListener {
            isReady = true
            mMediaPlayer?.start()
        }
        mMediaPlayer?.setOnErrorListener { _, _, _ -> false }

        return if (!isReady) {
            mMediaPlayer?.prepareAsync()
            true
        } else {
            if (mMediaPlayer?.isPlaying as Boolean) {
                mMediaPlayer?.pause()
                false
            } else {
                mMediaPlayer?.start()
                true
            }
        }
    }

    override fun stop(): Boolean {
        return if (mMediaPlayer?.isPlaying as Boolean || isReady) {
            mMediaPlayer?.stop()
            isReady = false
            false
        } else { true }
    }
}

interface AudioPlayerInterface {
    fun playFile(file: File): Boolean
    fun stop(): Boolean
}