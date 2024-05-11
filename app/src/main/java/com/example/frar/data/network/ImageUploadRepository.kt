package com.example.frar.data.network


import com.example.frar.data.models.Recommendation
import dagger.hilt.android.scopes.ViewModelScoped
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

@ViewModelScoped
class ImageUploadRepository @Inject constructor(private val imageUploadApi: ImageUploadApi) {
    suspend fun uploadImageToPredict(image: MultipartBody.Part): Response<Recommendation>{
        return imageUploadApi.uploadImage(image)
    }
}