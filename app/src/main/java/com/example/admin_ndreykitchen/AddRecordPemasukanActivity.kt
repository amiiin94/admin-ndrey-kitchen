package com.example.admin_ndreykitchen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin_ndreykitchen.adapter.AddTransactionAdapter
import com.example.admin_ndreykitchen.fragment.SpaceItemDecoration
import com.example.admin_ndreykitchen.model.MenuModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date

class AddRecordPemasukanActivity : AppCompatActivity(), AddTransactionAdapter.QuantityChangeListener {

    private val menuList = mutableListOf<MenuModel>()
    private lateinit var rv_menu: RecyclerView
    private lateinit var back_btn: ImageView
    private lateinit var save_btn: Button
    private var totalHarga = 0 // Declare totalHarga property here
    var isFirstClick = true

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

        back_btn = findViewById(R.id.back_btn)
        back_btn.setOnClickListener{
            val back_btn_intent = Intent(this@AddRecordPemasukanActivity, MainActivity::class.java)
            startActivity((back_btn_intent))
        }

        val pengeluaran_btn: Button = findViewById(R.id.pengeluaran_btn)
        pengeluaran_btn.setOnClickListener{
            val loginIntent = Intent(this@AddRecordPemasukanActivity, AddRecordPengeluaranActivity::class.java)
            startActivity(loginIntent)
        }

        rv_menu = findViewById(R.id.rv_menu_tambahTransaksi)
        rv_menu.layoutManager = GridLayoutManager(this, 1)

        val horizontalSpace = resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin)
        val verticalSpace = resources.getDimensionPixelSize(R.dimen.activity_vertical_margin)
        rv_menu.addItemDecoration(SpaceItemDecoration(horizontalSpace, verticalSpace))

        getAllMenus(this)

        //save record
        save_btn = findViewById(R.id.save_btn)
        save_btn.setOnClickListener {
            putAmount(totalHarga)
            postItemsWithQuantity()
        }
    }

    private fun getAllMenus(context: Context) {
        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/getAllMenus"
        val sr = StringRequest(
            Request.Method.GET,
            urlEndPoints,
            { response ->
                try {
                    menuList.clear()
                    val menus = JSONArray(response)
                    for (i in 0 until menus.length()) {
                        val menuJson = menus.getJSONObject(i)

                        val id_menu = menuJson.getString("_id")
                        val nama_menu = menuJson.getString("nama")
                        val harga_menu = menuJson.getInt("harga")
                        val images = menuJson.getString("image")

                        val menu = MenuModel(id_menu, nama_menu, harga_menu, images)
                        menuList.add(menu)
                    }
                    displayMenu()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(context, error.toString().trim { it <= ' ' }, Toast.LENGTH_SHORT).show()
            }
        )
        val requestQueue = Volley.newRequestQueue(context.applicationContext)
        requestQueue.add(sr)
    }

    private fun displayMenu() {
        val menuAdapter = AddTransactionAdapter(menuList, this, this)
        rv_menu.adapter = menuAdapter
        calculateTotalHarga()
    }

    override fun onQuantityChanged() {
        calculateTotalHarga()
    }

    private fun calculateTotalHarga() {
        totalHarga = 0 // Reset totalHarga before calculating
        for (menu in menuList) {
            totalHarga += menu.harga_menu * menu.quantity
        }
        val totalHargaTextView: TextView = findViewById(R.id.tvTotalHarga)
        totalHargaTextView.text = "Total Harga: Rp $totalHarga"
    }

    fun postIdRecord() {
        val currentDate = SimpleDateFormat("dd/MM/yyyy").format(Date())

        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/postIdPemasukan?date=" +
                currentDate

        val sr = StringRequest(
            Request.Method.POST,
            urlEndPoints,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)

                    // Check if the response contains an error field
                    if (jsonResponse.has("error")) {
                        val errorMessage = jsonResponse.getString("error")
                        // Display toast with the error message
                        Toast.makeText(this@AddRecordPemasukanActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        // Registration successful
                        Toast.makeText(
                            this@AddRecordPemasukanActivity,
                            "record id have been added",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    // Handle JSON parsing error
                    e.printStackTrace()
                    Toast.makeText(this@AddRecordPemasukanActivity, "failed", Toast.LENGTH_SHORT).show()
                }
            }
        ) { Toast.makeText(this@AddRecordPemasukanActivity, "Registration failed", Toast.LENGTH_SHORT).show() }

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(sr)
    }

    fun putAmount(totalHarga: Int) {

        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/putTotalHargaOnLastRecord?amount=$totalHarga"

        val sr = StringRequest(
            Request.Method.PUT,
            urlEndPoints,
            { response ->
                if (response == "\"Total harga updated successfully for the last document.\"") {
                    // Update successful
                    Toast.makeText(this@AddRecordPemasukanActivity, "Amount Updated", Toast.LENGTH_SHORT).show()

                    // Redirect to the main activity
                    val mainActivityIntent = Intent(this@AddRecordPemasukanActivity, MainActivity::class.java)
                    startActivity(mainActivityIntent)
                } else {
                    // Display toast with the response message
                    Toast.makeText(this@AddRecordPemasukanActivity, response, Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                // Handle Volley error
                error.printStackTrace()
                Toast.makeText(this@AddRecordPemasukanActivity, "Failed: " + error.message, Toast.LENGTH_SHORT).show()
            }
        )

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(sr)
    }

    fun postItemMenu(item: String, quantity: Int) {
        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/postItemMenuPemasukan?item=$item&quantity=$quantity"

        val sr = StringRequest(
            Request.Method.POST,
            urlEndPoints,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)

                    // Check if the response contains an error field
                    if (jsonResponse.has("error")) {
                        val errorMessage = jsonResponse.getString("error")
                        // Display toast with the error message
                        Toast.makeText(this@AddRecordPemasukanActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        // Post successful
                        Toast.makeText(this@AddRecordPemasukanActivity, "Item posted successfully", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    // Handle JSON parsing error
                    e.printStackTrace()
                    Toast.makeText(this@AddRecordPemasukanActivity, "Failed to post item", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Toast.makeText(this@AddRecordPemasukanActivity, "Failed to post item", Toast.LENGTH_SHORT).show()
        }

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(sr)
    }

    fun postItemsWithQuantity() {
        for (menu in menuList) {
            if (menu.quantity > 0) {
                postItemMenu(menu.nama_menu, menu.quantity)
            }
        }
    }
}
