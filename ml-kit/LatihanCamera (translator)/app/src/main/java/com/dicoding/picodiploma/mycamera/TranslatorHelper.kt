package com.dicoding.picodiploma.mycamera

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class TranslatorHelper(
    private val onTranslatedTextUpdated: (String) -> Unit,
    private val onDownloadModel: () -> Unit,
    private val onError: (String) -> Unit
) {

    private var availableModels = listOf<String>()

    fun translateText(detectedText: String) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.INDONESIAN)
            .build()
        val indonesianEnglishTranslator = Translation.getClient(options)

        val modelManager = RemoteModelManager.getInstance()
        modelManager.getDownloadedModels(TranslateRemoteModel::class.java)
            .addOnSuccessListener { models ->
                availableModels = models.sortedBy { it.language }.map { it.language }

                if (availableModels.contains("id")) {
                    indonesianEnglishTranslator.translate(detectedText)
                        .addOnSuccessListener { translatedText ->
                            onTranslatedTextUpdated(translatedText)
                            indonesianEnglishTranslator.close()
                        }
                        .addOnFailureListener { exception ->
                            onError(exception.message.toString())
                            print(exception.stackTrace)
                            indonesianEnglishTranslator.close()
                        }
                } else {
                    onDownloadModel()
                    val indonesianModel =
                        TranslateRemoteModel.Builder(TranslateLanguage.INDONESIAN).build()
                    val conditions = DownloadConditions.Builder()
                        .requireWifi()
                        .build()
                    modelManager.download(indonesianModel, conditions)
                        .addOnSuccessListener {
                            translateText(detectedText)
                        }
                        .addOnFailureListener { exception ->
                            onError(exception.message.toString())
                        }
                }
            }
    }
}