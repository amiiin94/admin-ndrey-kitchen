package com.example.admin_ndreykitchen

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddTransaction : AppCompatActivity() {
    lateinit var pengeluaran_btn : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        enableEdgeToEdge()
        setContentView(R.layout.activity_addtransaction_pemasukan)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        pengeluaran_btn = findViewById(R.id.pengeluaran_btn)
        pengeluaran_btn.setOnClickListener{
            val loginIntent = Intent(this@AddTransaction, AddtransactionPengeluaranActivity::class.java)
            startActivity(loginIntent)
        }
    }
}