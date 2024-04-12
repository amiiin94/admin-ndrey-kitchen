package com.example.admin_ndreykitchen

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RecordDetailPengeluaran : AppCompatActivity() {
    private lateinit var tvDateDetail: TextView
    private lateinit var tvTypeDetail: TextView
    private lateinit var tvAmountDetail: TextView
    private lateinit var tvTitleDetail: TextView
    private lateinit var tvKategoriDetail: TextView
    private lateinit var tvNoteDetail: TextView
    private lateinit var back_btn: ImageButton
    private lateinit var edit_btn: ImageButton
    private lateinit var delete_btn: ImageButton

    private lateinit var _id: String
    private lateinit var date: String
    private lateinit var type: String
    private  lateinit var amount: String
    private lateinit var title: String
    private lateinit var kategori: String
    private lateinit var note: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_record_detail_pengeluaran)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeItems()
    }

    private fun initializeItems() {
        tvDateDetail = findViewById(R.id.tvDateDetail)
        tvTypeDetail = findViewById(R.id.tvTypeDetail)
        tvAmountDetail = findViewById(R.id.tvAmountDetail)
        tvTitleDetail = findViewById(R.id.tvTitleDetail)
        tvKategoriDetail = findViewById(R.id.tvKategoriDetail)
        tvNoteDetail = findViewById(R.id.tvCatatanDetail)
        back_btn = findViewById(R.id.back_btn)
        edit_btn = findViewById(R.id.edit_btn)
        delete_btn = findViewById(R.id.delete_btn)


        // Retrieve data from intent
        _id = intent.getStringExtra("id_record") ?: ""
        date = intent.getStringExtra("date_record") ?: ""
        type = intent.getStringExtra("type_record") ?: ""
        amount = intent.getStringExtra("amount_record") ?: ""
        title = intent.getStringExtra("title_record") ?: ""
        kategori = intent.getStringExtra("kategori_record") ?: ""
        note = intent.getStringExtra("note_record") ?: ""

        // Set the data to the views
        tvDateDetail.setText(date)
        tvTypeDetail.setText((type))
        tvAmountDetail.setText(amount.toString())
        tvTitleDetail.setText(title)
        tvKategoriDetail.setText(kategori)
        tvNoteDetail.setText(note)
    }
}