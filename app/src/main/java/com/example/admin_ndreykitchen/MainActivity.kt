package com.example.admin_ndreykitchen

import HomeFragment
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.admin_ndreykitchen.databinding.ActivityMainBinding
import com.example.admin_ndreykitchen.fragment.MenuFragment
import com.example.admin_ndreykitchen.fragment.ProfileFragment
import com.example.admin_ndreykitchen.fragment.RecordFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var add_transaction_btn: FloatingActionButton

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeFragment = HomeFragment()
        val menuFragment = MenuFragment()
        val penjualanFragment = RecordFragment()
        val profileFragment = ProfileFragment()

        // Set the initially selected item programmatically
        binding.bottomNavigationView.selectedItemId = R.id.home

        makeCurrentFragment(homeFragment)

        binding.bottomNavigationView.setOnItemSelectedListener { //when the bottom nav clicked
            when (it.itemId) {
                R.id.home -> makeCurrentFragment(homeFragment)
                R.id.menu -> makeCurrentFragment(menuFragment)
                R.id.penjualan -> makeCurrentFragment(penjualanFragment)
                R.id.profile -> makeCurrentFragment(profileFragment)
            }
            true // Return true to indicate the item selection event is handled
        }

        add_transaction_btn = findViewById(R.id.add_transaction)
        add_transaction_btn.setOnClickListener{
            val intent = Intent(this, AddRecordPemasukanActivity::class.java)
            startActivity(intent)
        }
    }

    private fun makeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            commit()
        }
    }
}
