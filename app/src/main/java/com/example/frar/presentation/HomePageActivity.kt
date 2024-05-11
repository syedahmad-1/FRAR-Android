package com.example.frar.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.frar.R
import com.example.frar.databinding.ActivityHomePageBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomePageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomePageBinding
    private lateinit var myToolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        myToolbar= findViewById(R.id.myToolbar)
//        setSupportActionBar(this.myToolbar)

        replaceFragment(HomePageFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.home -> replaceFragment(HomePageFragment())
                R.id.profile -> replaceFragment(Profile())
//                R.id.recommendation -> replaceFragment(Recommendation())

                else -> {

                }

            }
            true
        }

    }


    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()

    }
}