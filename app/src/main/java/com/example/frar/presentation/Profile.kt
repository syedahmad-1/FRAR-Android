package com.example.frar.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.frar.databinding.ProfileBinding
import com.google.firebase.auth.FirebaseAuth

class Profile : Fragment() {

    private lateinit var binding: ProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ProfileBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null) {
            firebaseAuth.currentUser?.let {
                binding.soUserEmail.text = it.email
            }
        }
        binding.btnSignOut.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(
                Intent(requireContext(), LogInActivity::class.java)
            )
            activity?.finishAffinity()
        }
        return binding.root!!
    }
}