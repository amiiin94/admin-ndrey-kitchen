package com.example.admin_ndreykitchen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
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
import com.example.admin_ndreykitchen.adapter.ItemOrderAdapter
import com.example.admin_ndreykitchen.model.ItemModel
import com.example.admin_ndreykitchen.model.OrderItemModel
import com.example.admin_ndreykitchen.model.OrderModel
import org.json.JSONArray
import org.json.JSONException
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class OrderDetail : AppCompatActivity() {
    private val itemList = mutableListOf<OrderItemModel>()
    private lateinit var rvItem: RecyclerView
    private lateinit var tvStatus: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvNamaPembeli: TextView
    private lateinit var back_btn: ImageView
    private lateinit var tvTotalHarga: TextView
    private lateinit var tvPayment: TextView
    private lateinit var btn_pesanan_dapat_diambil: com.google.android.material.button.MaterialButton

    private lateinit var id_order: String
    private lateinit var status: String
    private lateinit var date: String
    private var amount: Int = 0
    private lateinit var namaPembeli: String
    private lateinit var payment: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.item_order_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeItems()

        rvItem.layoutManager = LinearLayoutManager(this)

        getAllItemOrder()
        showItem()

        back_btn.setOnClickListener{
            val back_btn_intent = Intent(this@OrderDetail, MainActivity::class.java)
            startActivity((back_btn_intent))
        }

        btn_pesanan_dapat_diambil.setOnClickListener {
            putStatusOrder()
        }
    }

    private fun initializeItems() {
        rvItem = findViewById(R.id.rvItem)
        tvStatus = findViewById(R.id.tvStatus)
        tvDate = findViewById(R.id.tvDate)
        tvNamaPembeli = findViewById(R.id.tvNamaPembeli)
        back_btn = findViewById(R.id.back_btn)
        tvPayment = findViewById(R.id.tvPayment)
        btn_pesanan_dapat_diambil = findViewById(R.id.btn_pesanan_dapat_diambil)
        tvTotalHarga = findViewById(R.id.tvTotalHarga)


        // Retrieve data from intent
        id_order = intent.getStringExtra("id_order") ?: ""
        date = intent.getStringExtra("date_order") ?: ""
        amount = intent.getIntExtra("amount_order", 0)
        status = intent.getStringExtra("status_order") ?: ""
        payment = intent.getStringExtra("payment_order") ?: ""
        namaPembeli = intent.getStringExtra("fullname_order") ?: ""


        // Set the data to the views
        tvDate.setText(date)
        tvTotalHarga.setText(formatToRupiah(amount))
        tvStatus.setText(status)
        tvNamaPembeli.setText(namaPembeli)
        tvPayment.setText(payment)


    }

    private fun getAllItemOrder() {
        val urlEndPoints =
            "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/getItemOrder"
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
                            val id_order = recordJson.getString("id_order")

                            // Check if record_id matches the _id retrieved from intent
                            if (id_order == this@OrderDetail.id_order) {
                                val _id = recordJson.getString("_id")
                                val item = recordJson.getString("item")
                                val quantity = recordJson.getInt("quantity")

                                val order = OrderItemModel(_id, id_order, item, quantity)
                                itemList.add(order)
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
        val orderItemModel = ItemOrderAdapter(itemList)
        rvItem.adapter = orderItemModel
    }

    private fun formatToRupiah(value: Int?): String {
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatRupiah.currency = Currency.getInstance("IDR")

        val formattedValue = value?.let { formatRupiah.format(it.toLong()).replace("Rp", "").trim() }

        return "Rp. $formattedValue"
    }

    fun putStatusOrder() {
        val status = "Pesanan dapat diambil"

        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/putStatusOrder?_id=$id_order&status=$status"

        val sr = StringRequest(
            Request.Method.PUT,
            urlEndPoints,
            { response ->
                if (response == "\"Status Updated.\"") {
                    // Update successful
//                    Toast.makeText(this@OrderDetail, "Amount Updated", Toast.LENGTH_SHORT).show()

                    // Redirect to the main activity
                    val mainActivityIntent = Intent(this@OrderDetail, MainActivity::class.java)
                    mainActivityIntent.putExtra("selected_tab", R.id.menu)
                    startActivity(mainActivityIntent)
                } else {
                    // Display toast with the response message
                    Toast.makeText(this@OrderDetail, response, Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                // Handle Volley error
                error.printStackTrace()
                Toast.makeText(this@OrderDetail, "Failed: " + error.message, Toast.LENGTH_SHORT).show()
            }
        )

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(sr)
    }
}