package com.example.admin_ndreykitchen

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class RecordDetailPengeluaran : AppCompatActivity() {
    private lateinit var tvDateDetail: TextView
    private lateinit var tvAmountDetail: TextView
    private lateinit var tvTitleDetail: TextView
    private lateinit var tvKategoriDetail: TextView
    private lateinit var tvNoteDetail: TextView
    private lateinit var back_btn: ImageButton

    private lateinit var _id: String
    private lateinit var date: String
    private   var amount: Int = 0
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

        back_btn.setOnClickListener{
            val back_btn_intent = Intent(this@RecordDetailPengeluaran, MainActivity::class.java)
            startActivity((back_btn_intent))
        }
    }

    private fun initializeItems() {
        tvDateDetail = findViewById(R.id.tvDateDetail)
        tvAmountDetail = findViewById(R.id.tvAmountDetail)
        tvTitleDetail = findViewById(R.id.tvTitleDetail)
        tvKategoriDetail = findViewById(R.id.tvKategoriDetail)
        tvNoteDetail = findViewById(R.id.tvCatatanDetail)
        back_btn = findViewById(R.id.back_btn)


        // Retrieve data from intent
        _id = intent.getStringExtra("id_record") ?: ""
        date = intent.getStringExtra("date_record") ?: ""
        amount = intent.getIntExtra("amount_record", 0)
        title = intent.getStringExtra("title_record") ?: ""
        kategori = intent.getStringExtra("category_record") ?: ""
        note = intent.getStringExtra("note_record") ?: ""

        // Set the data to the views
        tvDateDetail.setText(date)
        tvAmountDetail.setText(formatToRupiah(amount))
        tvTitleDetail.setText(title)
        tvKategoriDetail.setText(kategori)
        tvNoteDetail.setText(note)
        tvKategoriDetail.setText(kategori)
    }

    private fun formatToRupiah(value: Int?): String {
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatRupiah.currency = Currency.getInstance("IDR")

        val formattedValue = value?.let { formatRupiah.format(it.toLong()).replace("Rp", "").trim() }

        return "Rp. $formattedValue"
    }
}