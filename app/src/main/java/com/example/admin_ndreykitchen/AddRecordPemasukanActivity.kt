package com.example.admin_ndreykitchen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin_ndreykitchen.adapter.MenuAdapter
import com.example.admin_ndreykitchen.adapter.MenuTambahTransaksiAdapter
import com.example.admin_ndreykitchen.fragment.SpaceItemDecoration
import com.example.admin_ndreykitchen.model.MenuModel
import org.json.JSONArray
import org.json.JSONException

class AddRecordPemasukanActivity : AppCompatActivity(), MenuTambahTransaksiAdapter.QuantityChangeListener {

    private val menuList = mutableListOf<MenuModel>()
    private lateinit var rv_menu: RecyclerView
    private var totalHarga: Int = 0

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
        val menuAdapter = MenuTambahTransaksiAdapter(menuList, this)
        rv_menu.adapter = menuAdapter
        calculateTotalHarga()
    }

    override fun onQuantityChanged() {
        calculateTotalHarga()
    }

    private fun calculateTotalHarga() {
        var totalHarga = 0
        for (menu in menuList) {
            totalHarga += menu.harga_menu * menu.quantity
        }
        val totalHargaTextView: TextView = findViewById(R.id.tvTotalHarga)
        totalHargaTextView.text = "Total Harga: Rp $totalHarga"
    }
}