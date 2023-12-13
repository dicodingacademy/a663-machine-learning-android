package com.example.mytextclassification

import android.content.Context
import android.os.SystemClock
import android.util.Log
import com.google.mediapipe.tasks.components.containers.Classifications
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.text.textclassifier.TextClassifier
import java.util.concurrent.ScheduledThreadPoolExecutor

class TextClassifierHelper(
    val modelName: String = "bert_classifier.tflite",
    val context: Context,
    var classifierListener: ClassifierListener? = null,
) {

    private var textClassifier: TextClassifier? = null
    private var executor: ScheduledThreadPoolExecutor? = null

    init {
        initClassifier()
    }

    private fun initClassifier() {
        try {
            val optionsBuilder = TextClassifier.TextClassifierOptions.builder()

            val baseOptionsBuilder = BaseOptions.builder()
                .setModelAssetPath(modelName)
            optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

            textClassifier = TextClassifier.createFromOptions(context, optionsBuilder.build())
        } catch (e: IllegalStateException) {
            classifierListener?.onError(context.getString(R.string.text_classifier_failed))
            Log.e(TAG, e.message.toString())
        }
    }

    fun classify(inputText: String) {
        if (textClassifier == null) {
            initClassifier()
        }

        executor = ScheduledThreadPoolExecutor(1)

        executor?.execute {
            var inferenceTime = SystemClock.uptimeMillis()
            val results = textClassifier?.classify(inputText)
            inferenceTime = SystemClock.uptimeMillis() - inferenceTime

            classifierListener?.onResults(results?.classificationResult()?.classifications(), inferenceTime)
        }
    }

    companion object {
        private const val TAG = "TextClassifierHelper"
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: List<Classifications>?,
            inferenceTime: Long
        )
    }
}
