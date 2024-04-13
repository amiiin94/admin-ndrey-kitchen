package com.example.admin_ndreykitchen

import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin_ndreykitchen.adapter.ItemMenuAdapter
import com.example.admin_ndreykitchen.adapter.MenuAdapter
import com.example.admin_ndreykitchen.model.ItemModel
import org.json.JSONArray
import org.json.JSONException
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class RecordDetailPemasukan : AppCompatActivity() {
    private val itemList = mutableListOf<ItemModel>()
    private lateinit var rvItem: RecyclerView
    private lateinit var tvDateDetail: TextView
    private lateinit var tvAmountDetail: TextView
    private lateinit var tvNoteDetail: TextView
    private lateinit var back_btn: ImageButton
    private lateinit var edit_btn: ImageButton
    private lateinit var delete_btn: ImageButton

    private lateinit var _id: String
    private lateinit var date: String
    private var amount: Int = 0
    private lateinit var note: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_record_detail_pemasukan)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rvItem = findViewById(R.id.rvItem)
        rvItem.layoutManager = LinearLayoutManager(this)

        initializeItems()
        getAllItemListMenu()
        showItem()
    }

    private fun initializeItems() {
        rvItem = findViewById(R.id.rvItem)
        tvDateDetail = findViewById(R.id.tvDateDetail)
        tvAmountDetail = findViewById(R.id.tvAmountDetail)
        tvNoteDetail = findViewById(R.id.tvNoteDetail)
        back_btn = findViewById(R.id.back_btn)
        edit_btn = findViewById(R.id.edit_btn)
        delete_btn = findViewById(R.id.delete_btn)


        // Retrieve data from intent
        _id = intent.getStringExtra("id_record") ?: ""
        date = intent.getStringExtra("date_record") ?: ""
        amount = intent.getIntExtra("amount_record", 0)
        title = intent.getStringExtra("title_record") ?: ""
        note = intent.getStringExtra("note_record") ?: ""

        // Set the data to the views
        tvDateDetail.setText(date)
        tvAmountDetail.setText(formatToRupiah(amount))
        tvNoteDetail.setText(note)


    }

    private fun getAllItemListMenu() {
        val urlEndPoints =
            "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/getItemMenu"
        val sr = StringRequest(
            Request.Method.GET,
            urlEndPoints,
            { response ->
                try {
                    val records = JSONArray(response)
                    if (records.length() > 0) {
                        itemList.clear() // Clearing the list here
                        for (i in 0 until records.length()) {
                            val recordJson = records.getJSONObject(i)
                            val record_id = recordJson.getString("record_id")

                            // Check if record_id matches the _id retrieved from intent
                            if (record_id == _id) {
                                val _id = recordJson.getString("_id")
                                val item = recordJson.getString("item")
                                val quantity = recordJson.getInt("quantity")

                                val record = ItemModel(_id, record_id, item, quantity)
                                itemList.add(record)
                            }
                        }
                        // Notify adapter that data set changed
                        showItem()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(this, error.toString().trim { it <= ' ' }, Toast.LENGTH_SHORT)
                    .show()
            }
        )
        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(sr)
    }


    private fun showItem() {
        val itemMenuAdapter = ItemMenuAdapter(itemList)
        rvItem.adapter = itemMenuAdapter
    }

    private fun formatToRupiah(value: Int?): String {
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatRupiah.currency = Currency.getInstance("IDR")

        val formattedValue = value?.let { formatRupiah.format(it.toLong()).replace("Rp", "").trim() }

        return "Rp. $formattedValue"
    }
}