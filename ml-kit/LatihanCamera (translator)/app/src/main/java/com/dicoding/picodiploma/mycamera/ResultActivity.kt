package com.dicoding.picodiploma.mycamera

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.mycamera.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

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
            translateText(detectedText)
        }
    }

    private fun translateText(detectedText: String?) {
        binding.progressIndicator.visibility = View.VISIBLE
        val translator = TranslatorHelper(
            onTranslatedTextUpdated = { translatedText ->
                binding.progressIndicator.visibility = View.GONE
                binding.translatedText.text = translatedText
            },
            onDownloadModel = {
                showToast(getString(R.string.downloading_model))
            },
            onError = { error ->
                runOnUiThread {
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            }
        )
        translator.translateText(detectedText.toString())
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
    }
}