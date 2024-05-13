package com.example.frar.presentation

import android.content.Intent
import android.graphics.Color
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.frar.MyAdapter
import com.example.frar.core.NetworkResult
import com.example.frar.data.models.FaceShapeResult
import com.example.frar.data.models.RecommendationType
import com.example.frar.databinding.FragmentHomePageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream


@AndroidEntryPoint
class HomePageFragment : Fragment() {

    private lateinit var binding: FragmentHomePageBinding

    private val homePageViewModel: HomePageViewModel by viewModels()
    private val myAdapter by lazy { MyAdapter() }

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                homePageViewModel.setImageUriVm(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomePageBinding.inflate(layoutInflater)


        homePageViewModel.changeAddButtonState(false)
        // Inflate the layout for this fragment
        binding.btnAddImage.setOnClickListener {

            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            homePageViewModel.imageUriLiveData.observe(viewLifecycleOwner) { uri ->
                requireContext().contentResolver.takePersistableUriPermission(uri, flag)

            }
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        }


        binding.apply {
            rcvRecommend.adapter = myAdapter
            rcvRecommend.layoutManager = LinearLayoutManager(requireContext())
        }

        binding.btnUploadImage.setOnClickListener {
            if (homePageViewModel.isInitialized && homePageViewModel.imageUriLiveData.value != null) {
                uploadImage()
            } else {
                Toast.makeText(requireContext(), "Select the image", Toast.LENGTH_SHORT).show()
            }

        }


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                homePageViewModel.isAddButtonClicked.observe(viewLifecycleOwner){ isClicked->
                    if (isClicked){
                        binding.imgGuideline.visibility = View.GONE
                    }
                    if (!isClicked){
                        binding.imgGuideline.visibility = View.VISIBLE
                    }

                }


                homePageViewModel.imageUriLiveData.observe(viewLifecycleOwner) { uri ->
                    if (uri != null) {
                        binding.imgDisplay.load(uri){
                            crossfade(100)
                                .transformations(RoundedCornersTransformation(20f))
                        }
                        binding.imgGuideline.visibility = View.GONE
                    }
                }

                homePageViewModel.predictionResponseLiveData.observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            Log.d("UPLOAD_IMAGE_SUCCESS", "uploadImage: ${result.data}")

                            binding.apply {
                                rcvRecommend.visibility = View.VISIBLE
                                textView9.visibility = View.VISIBLE
                                sliderLayout.visibility = View.VISIBLE
                                btnLayout.visibility = View.VISIBLE
                                binding.imgGuideline.visibility = View.GONE
                            }
                            result.data?.let { setUpVisibility(it.result) }
                            myAdapter.submitList(result.data?.recommendation?.maleRecommendation)

                            binding.apply {
                                maleBtn.setOnClickListener {
                                    homePageViewModel.switchButtonState(false)
                                    toggleButtons(result.data?.recommendation)
                                }
                                femaleBtn.setOnClickListener {
                                    homePageViewModel.switchButtonState(true)
                                    toggleButtons(result.data?.recommendation)
                                }
                            }

                            if (homePageViewModel.isFemaleBtnSelected.value==true){
                                myAdapter.submitList(result.data?.recommendation?.femaleRecommendation)
                            }

                        }

                        is NetworkResult.Error -> {
                            Log.d("UPLOAD_IMAGE_ERROR", "uploadImage: ${result.message}")
                            binding.apply {
                                rcvRecommend.visibility = View.GONE
                                textView9.visibility = View.GONE
                                sliderLayout.visibility = View.GONE
                                btnLayout.visibility = View.GONE

                            }


                        }

                        is NetworkResult.Loading -> {
                            Log.d("UPLOAD_IMAGE_LOADING", "uploadImage: LOADING...")
                            binding.apply {
                                rcvRecommend.visibility = View.GONE
                                textView9.visibility = View.GONE
                                sliderLayout.visibility = View.GONE
                                btnLayout.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }

        return binding.root
    }

    private fun toggleButtons(recommendation: RecommendationType?) {
        homePageViewModel.isFemaleBtnSelected.observe(viewLifecycleOwner){bool->
            if(bool){
                    binding.apply {

                        myAdapter.submitList(recommendation?.femaleRecommendation)

                        femaleBtn.setBackgroundColor(Color.BLACK)
                        femaleBtn.setTextColor(Color.WHITE)

                        maleBtn.setBackgroundColor(Color.WHITE)
                        maleBtn.setTextColor(Color.BLACK)
                    }
                }
                else{
                    binding.apply {
                        myAdapter.submitList(recommendation?.maleRecommendation)
                        femaleBtn.setBackgroundColor(Color.WHITE)
                        femaleBtn.setTextColor(Color.BLACK)

                        maleBtn.setBackgroundColor(Color.BLACK)
                        maleBtn.setTextColor(Color.WHITE)
                    }
                }

            }
        }

    private fun setUpVisibility(data: FaceShapeResult) {
        binding.apply {
            heartSl.progress = data.Heart.toInt()
            var string = "${data.Heart.toInt()}%"
            heartPercentTv.text = string


            roundSl.progress = data.Round.toInt()
            string = "${data.Round.toInt()}%"
            roundPercentTv.text = string

            string = "${data.Oblong.toInt()}%"

            oblongSl.progress = data.Oblong.toInt()
            oblongPercentTv.text = string

            string = "${data.Oval.toInt()}%"

            ovalSl.progress = data.Oval.toInt()
            ovalPercentTv.text = string


            string = "${data.Square.toInt()}%"

            squareSl.progress = data.Square.toInt()
            squarePercentTv.text = string

            resultTv.text = data.result
        }
    }

    private fun uploadImage() {
        val file = File(requireContext().filesDir, "image.png")
        val inputStream =
            requireContext().contentResolver.openInputStream(homePageViewModel.imageUriLiveData.value!!)
        val outputStream = FileOutputStream(file)
        inputStream!!.copyTo(outputStream)

        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val part: MultipartBody.Part =
            MultipartBody.Part.createFormData("image", file.name, requestBody)

        homePageViewModel.uploadImage(part)


    }
}