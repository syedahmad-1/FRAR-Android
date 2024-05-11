package com.example.frar.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.frar.core.NetworkResult
import com.example.frar.databinding.FragmentHomePageBinding
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream


@AndroidEntryPoint
class HomePageFragment : Fragment() {

    private lateinit var binding: FragmentHomePageBinding
    private var imageUri: Uri? = null
    private var isInitialized = false

    private val homePageViewModel by viewModels<HomePageViewModel>()

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                imageUri = uri
                binding.imgDisplay.setImageURI(uri)
                isInitialized = true
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomePageBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        binding.btnAddImage.setOnClickListener {
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            imageUri?.let { it1 ->
                requireContext().contentResolver.takePersistableUriPermission(
                    it1,
                    flag
                )
            }
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

            homePageViewModel.predictionResponseLiveData.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        Log.d("UPLOADIMAGESUCCESS", "uploadImage: ${result.data}")
                    }

                    is NetworkResult.Error -> {
                        Log.d("UPLOADIMAGEERROR", "uploadImage: ${result.message}")

                    }

                    is NetworkResult.Loading -> {
                        Log.d("UPLOADIMAGELOADING", "uploadImage: LOADING...")
                    }
                }
            }


        }

        binding.btnUploadImage.setOnClickListener {
            if (isInitialized && imageUri != null) {
                uploadImage()
            } else {
                Toast.makeText(requireContext(), "Select the image", Toast.LENGTH_SHORT).show()
            }

        }

        return binding.root
    }

    private fun uploadImage() {
        val file = File(requireContext().filesDir, "image.png")
        val inputStream = requireContext().contentResolver.openInputStream(imageUri!!)
        val outputStream = FileOutputStream(file)
        inputStream!!.copyTo(outputStream)

        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val part: MultipartBody.Part =
            MultipartBody.Part.createFormData("image", file.name, requestBody)

        homePageViewModel.uploadImage(part)


    }
}