package com.dicoding.picodiploma.mycamera.data.api

import okhttp3.MultipartBody
import retrofit2.http.*
interface ApiService {
    @Multipart
    @POST("skin-cancer/predict")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): FileUploadResponse
}
