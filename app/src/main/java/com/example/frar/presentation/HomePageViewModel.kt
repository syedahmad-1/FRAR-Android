package com.example.frar.presentation

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.viewModelScope
import com.example.frar.core.NetworkResult
import com.example.frar.data.models.Recommendation
import com.example.frar.data.network.ImageUploadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject


@HiltViewModel
class HomePageViewModel @Inject constructor(application: Application, private val imageUploadRepository: ImageUploadRepository):AndroidViewModel(application) {
    var isInitialized: Boolean = false
    private val _predictionResultLiveData = MutableLiveData<NetworkResult<Recommendation>>()
    val predictionResponseLiveData get() = _predictionResultLiveData


    var imageUri: Uri? = null

    private val _imageUriLiveData = MutableLiveData<Uri>()
    val imageUriLiveData get() = _imageUriLiveData



    fun uploadImage(image:MultipartBody.Part) =viewModelScope.launch(Dispatchers.IO) {
        uploadImageToPredict(image)
    }

    fun setImageUriVm(uri:Uri){
        Log.d("PhotoPicker", "Selected URI: $uri")
        _imageUriLiveData.value = uri
        isInitialized = true
    }

    private suspend fun uploadImageToPredict(image:MultipartBody.Part){

        withContext(Dispatchers.Main){
            _predictionResultLiveData.value = NetworkResult.Loading()
        }

        try {
            val response = imageUploadRepository.uploadImageToPredict(image)
            if (response.isSuccessful){
                Log.d("PREDICTION", "uploadImageToPredict: ${response.body()}")
                withContext(Dispatchers.Main){
                    _predictionResultLiveData.value = handleUploadImage(response)
                }

            }
        }catch (e:Exception){
            withContext(Dispatchers.Main){
                _predictionResultLiveData.value = NetworkResult.Error("API Error encountered")
            }

        }
    }

    private fun handleUploadImage(response: Response<Recommendation>): NetworkResult<Recommendation> {
        return when (response.code()) {
            200 -> {
                val body = response.body()
                if (body != null) {
                    NetworkResult.Success(body)
                } else {
                    NetworkResult.Error("Response body is null")
                }
            }
            400 -> NetworkResult.Error("Bad request")
            401 -> NetworkResult.Error("Unauthorized")
            403 -> NetworkResult.Error("Forbidden")
            404 -> NetworkResult.Error("Not found")
            500 -> NetworkResult.Error("Internal server error")
            else -> NetworkResult.Error("Unexpected error: ${response.code()}")
        }
    }


}