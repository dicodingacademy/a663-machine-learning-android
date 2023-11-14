package com.dicoding.mymediaplayer

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.FileOutputStream

class AudioRecorder (
    private val context: Context
): AudioRecorderInterface {
    private var mMediaRecorder: MediaRecorder? = null

    private fun createRecorder(): MediaRecorder {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()
    }

    override fun start(outputFile: File): Boolean {
        createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(FileOutputStream(outputFile).fd)

            prepare()
            start()

            mMediaRecorder = this
            return true
        }
    }

    override fun stop(): Boolean {
        mMediaRecorder?.stop()
        mMediaRecorder?.reset()
        mMediaRecorder = null
        return false
    }
}

interface AudioRecorderInterface {
    fun start(outputFile: File): Boolean
    fun stop(): Boolean
}