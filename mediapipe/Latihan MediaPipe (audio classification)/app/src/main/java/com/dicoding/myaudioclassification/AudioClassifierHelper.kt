package com.dicoding.myaudioclassification

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.os.SystemClock
import android.util.Log
import com.google.mediapipe.tasks.audio.audioclassifier.AudioClassifier
import com.google.mediapipe.tasks.audio.audioclassifier.AudioClassifierResult
import com.google.mediapipe.tasks.audio.core.RunningMode
import com.google.mediapipe.tasks.components.containers.AudioData
import com.google.mediapipe.tasks.components.containers.AudioData.AudioDataFormat
import com.google.mediapipe.tasks.components.containers.Classifications
import com.google.mediapipe.tasks.core.BaseOptions
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class AudioClassifierHelper(
    val threshold: Float = 0.1f,
    val maxResults: Int = 3,
    val modelName: String = "yamnet.tflite",
    val runningMode: RunningMode = RunningMode.AUDIO_STREAM,
    val overlap: Float = 0.5f,
    val context: Context,
    var classifierListener: ClassifierListener? = null,
) {

    private var audioClassifier: AudioClassifier? = null
    private var recorder: AudioRecord? = null
    private var executor: ScheduledThreadPoolExecutor? = null

    init {
        initClassifier()
    }

    private fun initClassifier() {
        try {
            val optionsBuilder = AudioClassifier.AudioClassifierOptions.builder()
                .setScoreThreshold(threshold)
                .setMaxResults(maxResults)
                .setRunningMode(runningMode)

            if (runningMode == RunningMode.AUDIO_STREAM) {
                optionsBuilder
                    .setResultListener(this::streamAudioResultListener)
                    .setErrorListener(this::streamAudioErrorListener)
            }

            val baseOptionsBuilder = BaseOptions.builder()
                .setModelAssetPath(modelName)
            optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

            audioClassifier = AudioClassifier.createFromOptions(context, optionsBuilder.build())

            if (runningMode == RunningMode.AUDIO_STREAM) {
                recorder = audioClassifier?.createAudioRecord(
                    AudioFormat.CHANNEL_IN_DEFAULT,
                    SAMPLING_RATE_IN_HZ,
                    BUFFER_SIZE_IN_BYTES.toInt()
                )
            }
        } catch (e: IllegalStateException) {
            classifierListener?.onError(context.getString(R.string.audio_classifier_failed))
            Log.e(TAG, "MP task failed to load with error: " + e.message)
        } catch (e: RuntimeException) {
            classifierListener?.onError(context.getString(R.string.audio_classifier_failed))
            Log.e(TAG, "MP task failed to load with error: " + e.message)
        }
    }

    fun startAudioClassification() {
        if (audioClassifier == null) {
            initClassifier()
        }

        if (recorder?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
            return
        }

        recorder?.startRecording()
        executor = ScheduledThreadPoolExecutor(1)

        val classifyRunnable = Runnable {
            recorder?.let { classifyAudioAsync(it) }
        }

        // Each model will expect a specific audio recording length. This formula calculates that
        // length using the input buffer size and tensor format sample rate.
        // For example, YAMNET expects 0.975 second length recordings.
        // This needs to be in milliseconds to avoid the required Long value dropping decimals.
        val lengthInMilliSeconds = ((REQUIRE_INPUT_BUFFER_SIZE * 1.0f) / SAMPLING_RATE_IN_HZ) * 1000
        val interval = (lengthInMilliSeconds * (1 - overlap)).toLong()

        executor?.scheduleAtFixedRate(
            classifyRunnable,
            0,
            interval,
            TimeUnit.MILLISECONDS
        )
    }

    private fun classifyAudioAsync(audioRecord: AudioRecord) {
        val audioData = AudioData.create(
            AudioDataFormat.create(recorder?.format), SAMPLING_RATE_IN_HZ
        )
        audioData.load(audioRecord)

        val inferenceTime = SystemClock.uptimeMillis()
        audioClassifier?.classifyAsync(audioData, inferenceTime)
    }

    fun stopAudioClassification() {
        executor?.shutdownNow()
        audioClassifier?.close()
        audioClassifier = null
        recorder?.stop()
    }

    private fun streamAudioResultListener(resultListener: AudioClassifierResult) {
        classifierListener?.onResults(
            resultListener.classificationResults().first().classifications(),
            resultListener.timestampMs()
        )
    }

    private fun streamAudioErrorListener(e: RuntimeException) {
        classifierListener?.onError(e.message.toString())
    }

    companion object {
        private const val TAG = "AudioClassifierHelper"

        private const val SAMPLING_RATE_IN_HZ = 16000
        private const val EXPECTED_INPUT_LENGTH = 0.975F
        private const val REQUIRE_INPUT_BUFFER_SIZE = SAMPLING_RATE_IN_HZ * EXPECTED_INPUT_LENGTH
        private const val BUFFER_SIZE_FACTOR: Int = 2
        private const val BUFFER_SIZE_IN_BYTES =
            REQUIRE_INPUT_BUFFER_SIZE * Float.SIZE_BYTES * BUFFER_SIZE_FACTOR
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: List<Classifications>,
            inferenceTime: Long
        )
    }
}
