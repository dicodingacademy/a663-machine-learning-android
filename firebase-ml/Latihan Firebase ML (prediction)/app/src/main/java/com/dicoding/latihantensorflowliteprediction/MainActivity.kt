package com.dicoding.latihantensorflowliteprediction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dicoding.latihantensorflowliteprediction.databinding.ActivityMainBinding
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var predictionHelper: PredictionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        downloadModel()
        binding.btnPredict.isEnabled = false

        binding.btnPredict.setOnClickListener {
            val input = binding.edSales.text.toString()
            predictionHelper.predict(input)
        }
    }

    private fun initPredictionHelper(modelFile: File){
        predictionHelper = PredictionHelper(
            context = this,
            modelFile = modelFile,
            onResult = { result ->
                binding.tvResult.text = result
            },
            onError = { errorMessage ->
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        )
    }

    @Synchronized
    private fun downloadModel(){
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()
        FirebaseModelDownloader.getInstance()
            .getModel("Rice-Stock", DownloadType.LOCAL_MODEL, conditions)
            .addOnSuccessListener { model: CustomModel ->
                try {
                    binding.btnPredict.isEnabled = true
                    initPredictionHelper(model.file!!)
                } catch (e: IOException) {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.model_initialization_failed),
                        Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e: Exception? ->
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.firebaseml_model_download_failed),
                    Toast.LENGTH_SHORT).show()
            }
    }

}