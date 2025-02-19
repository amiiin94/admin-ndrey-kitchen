package com.example.admin_ndreykitchen

import HomeFragment
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.admin_ndreykitchen.databinding.ActivityMainBinding
import com.example.admin_ndreykitchen.fragment.OrderFragment
import com.example.admin_ndreykitchen.fragment.ProfileFragment
import com.example.admin_ndreykitchen.fragment.RecordFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var add_transaction_btn: FloatingActionButton

    // Store the default icons for each menu item
    private val defaultIcons = mutableMapOf<Int, Int>()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeFragment = HomeFragment()
        val orderFragment = OrderFragment()
        val penjualanFragment = RecordFragment()
        val profileFragment = ProfileFragment()

        // Retrieve the selected tab ID from the intent
        val selectedTabId = intent.getIntExtra("selected_tab", -1)


        // Determine which fragment to show based on the selected tab ID
        val initialFragment = when (selectedTabId) {
            R.id.home -> homeFragment
            R.id.menu -> orderFragment
            R.id.penjualan -> penjualanFragment
            R.id.profile -> profileFragment
            else -> homeFragment
        }

        makeCurrentFragment(initialFragment)

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            val selectedFragment = when (item.itemId) {
                R.id.home -> homeFragment
                R.id.menu -> orderFragment
                R.id.penjualan -> penjualanFragment
                R.id.profile -> profileFragment
                else -> homeFragment
            }
            makeCurrentFragment(selectedFragment)
            true
        }

        add_transaction_btn = findViewById(R.id.add_transaction)
        add_transaction_btn.setOnClickListener{
            val intent = Intent(this, AddRecordPemasukanActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        // Revert to default icon when going back
        defaultIcons[binding.bottomNavigationView.selectedItemId]?.let {
            updateNavigationBarIcon(binding.bottomNavigationView.selectedItemId, it)
        }
    }

    private fun makeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            commit()
        }
    }

    private fun updateNavigationBarIcon(itemId: Int, iconId: Int) {
        binding.bottomNavigationView.menu.findItem(itemId).setIcon(iconId)
    }
}