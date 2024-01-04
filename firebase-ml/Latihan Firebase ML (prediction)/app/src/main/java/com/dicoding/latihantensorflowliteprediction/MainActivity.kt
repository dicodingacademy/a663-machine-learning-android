package com.dicoding.latihantensorflowliteprediction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dicoding.latihantensorflowliteprediction.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPredict.isEnabled = false

        val predictionHelper = PredictionHelper(
            context = this,
            onResult = { result ->
                binding.tvResult.text = result
            },
            onError = { errorMessage ->
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            },
            onDownloadSuccess = {
                binding.btnPredict.isEnabled = true
            }
        )

        binding.btnPredict.setOnClickListener {
            val input = binding.edSales.text.toString()
            predictionHelper.predict(input)
        }
    }
}