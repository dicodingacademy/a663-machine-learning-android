package com.dicoding.latihantensorflowliteprediction

import android.content.Context
import android.graphics.Movie
import android.util.Log
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import com.google.android.gms.tflite.java.TfLite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.InterpreterApi
import org.tensorflow.lite.gpu.GpuDelegateFactory
import java.io.IOException
import java.nio.ByteBuffer
import java.util.HashMap

class PredictionHelper(
    val modelName: String = "rice_stock.tflite",
    val context: Context,
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit,
) {

    private var interpreter: InterpreterApi? = null

    init {
        TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { gpuAvailable ->
            val optionsBuilder = TfLiteInitializationOptions.builder()
            if (gpuAvailable) {
                optionsBuilder.setEnableGpuDelegateSupport(true)
            }
            TfLite.initialize(context, optionsBuilder.build())
        }.addOnSuccessListener {
            loadLocalModel()
        }.addOnFailureListener {
            onError(context.getString(R.string.tflite_is_not_initialized_yet))
        }
    }

    private fun loadLocalModel() {
//        return withContext(Dispatchers.IO) {
            try {
                val buffer: ByteBuffer = Utils.loadModelFile(
                    context.assets, modelName
                )
                initializeInterpreter(buffer)
                Log.v(TAG, "TFLite model loaded.")
            } catch (ioException: IOException) {
                ioException.printStackTrace()
            }
//        }
    }

    private fun initializeInterpreter(buffer: ByteBuffer) {
        val options = InterpreterApi.Options()
            .setRuntime(InterpreterApi.Options.TfLiteRuntime.FROM_SYSTEM_ONLY)
            .addDelegateFactory(GpuDelegateFactory())

        try {
            interpreter = InterpreterApi.create(buffer, options)
        } catch (e: IllegalStateException) {
            onError(e.message.toString())
            Log.e(TAG, e.message.toString())
        }
    }

    fun predict(input: Int): Int {
//        return withContext(Dispatchers.Default) {
        val output = 0
        try {
            interpreter?.run(input, output)
        } catch (e: Exception){
            onError(context.getString(R.string.no_tflite_interpreter_loaded))
            Log.e(TAG, e.message.toString())
        }
//        }
        return output
    }

    companion object {
        private const val TAG = "PredictionHelper"
    }
}