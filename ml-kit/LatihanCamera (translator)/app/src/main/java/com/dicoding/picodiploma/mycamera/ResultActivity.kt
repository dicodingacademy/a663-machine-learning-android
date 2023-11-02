package com.dicoding.picodiploma.mycamera

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.mycamera.databinding.ActivityResultBinding
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private var availableModels = listOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        imageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.resultImage.setImageURI(it)
        }

        val detectedText = intent.getStringExtra(EXTRA_RESULT)
        binding.resultText.text = detectedText

        binding.translateButton.setOnClickListener {
            binding.progressIndicator.visibility = View.VISIBLE
            translateText(detectedText)
        }
    }

    private fun translateText(detectedText: String?) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.INDONESIAN)
            .build()
        val indonesianEnglishTranslator = Translation.getClient(options)

        val modelManager = RemoteModelManager.getInstance()
        modelManager.getDownloadedModels(TranslateRemoteModel::class.java)
            .addOnSuccessListener { models ->
                binding.progressIndicator.visibility = View.GONE

                availableModels = models.sortedBy { it.language }.map { it.language }

                if (availableModels.contains("id")) {
                    indonesianEnglishTranslator.translate(detectedText.toString())
                        .addOnSuccessListener { translatedText ->
                            binding.translatedText.text = translatedText
                            indonesianEnglishTranslator.close()
                        }
                        .addOnFailureListener { exception ->
                            showToast("Gagal menerjemahkan!")
                            print(exception.stackTrace)
                            indonesianEnglishTranslator.close()
                        }
                } else {
                    showToast("Model sedang diunduh. Coba lagi nanti!")
                    val indonesianModel =
                        TranslateRemoteModel.Builder(TranslateLanguage.INDONESIAN).build()
                    val conditions = DownloadConditions.Builder()
                        .requireWifi()
                        .build()
                    modelManager.download(indonesianModel, conditions)
                        .addOnSuccessListener {
                            showToast("Model berhasil diunduh")
                            translateText(detectedText)
                        }
                        .addOnFailureListener { exception ->
                            binding.progressIndicator.visibility = View.GONE
                            showToast("Model gagal diunduh. ${exception.message.toString()}")
                        }
                }
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
    }
}