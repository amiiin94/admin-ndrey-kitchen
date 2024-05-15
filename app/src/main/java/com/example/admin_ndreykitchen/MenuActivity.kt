package com.example.admin_ndreykitchen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.privacysandbox.tools.core.model.Method
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import com.example.admin_ndreykitchen.adapter.MenuAdapter
import com.example.admin_ndreykitchen.fragment.SpaceItemDecoration
import com.example.admin_ndreykitchen.model.MenuModel
import org.json.JSONArray
import org.json.JSONException

class MenuActivity : AppCompatActivity() {

    private val menuList = mutableListOf<MenuModel>()
    private lateinit var rv_menu: RecyclerView
    private lateinit var tambah_menu: TextView
    private lateinit var etSearch: EditText
    private lateinit var back_btn: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        rv_menu = findViewById(R.id.rv_menu)
        rv_menu.layoutManager = GridLayoutManager(this, 1)

        val horizontalSpace = resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin)
        val verticalSpace = resources.getDimensionPixelSize(R.dimen.activity_vertical_margin)
        rv_menu.addItemDecoration(SpaceItemDecoration(horizontalSpace, verticalSpace))

        // Create an empty adapter and set it to the RecyclerView
        val menuAdapter = MenuAdapter(menuList)
        rv_menu.adapter = menuAdapter

        // Tambah Menu
        tambah_menu = findViewById(R.id.tambah_menu)
        tambah_menu.setOnClickListener {
            val intent = Intent(this, AddMenuActivity::class.java)
            startActivity(intent)
        }

        // Search
        etSearch = findViewById(R.id.etSearch)
        etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val searchName: String = etSearch.text.toString()
                if (searchName.isNotEmpty()) {
                    getMenuByName(searchName)
                } else {
                    getAllMenus()
                }
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        back_btn = findViewById(R.id.back_btn)
        back_btn.setOnClickListener {
            val mainActivityIntent = Intent(this@MenuActivity, MainActivity::class.java)
            mainActivityIntent.putExtra("selected_tab", R.id.profile)
            startActivity(mainActivityIntent)
        }

        getAllMenus()
    }

    private fun getAllMenus() {
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
                        val deskripsi_menu = menuJson.getString("deskripsi")
                        val kategori_menu = menuJson.getString("kategori")

                        val menu = MenuModel(id_menu, nama_menu, harga_menu, images, deskripsi_menu, kategori_menu)
                        menuList.add(menu)
                    }
                    Log.d("MenuActivity", "menuList: $menuList")
                    displayMenu()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(this, error.toString().trim { it <= ' ' }, Toast.LENGTH_SHORT).show()
            }
        )
        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(sr)
    }

    private fun displayMenu() {
        val menuAdapter = MenuAdapter(menuList)
        rv_menu.adapter = menuAdapter
    }

    private fun getMenuByName(nama: String) {
        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/getMenuByName?nama=$nama"
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
                        val deskripsi_menu = menuJson.getString("deskripsi")
                        val kategori_menu = menuJson.getString("kategori")

                        val menu = MenuModel(id_menu, nama_menu, harga_menu, images, deskripsi_menu, kategori_menu)
                        menuList.add(menu)
                    }
                    Log.d("MenuActivity", "menuList: $menuList")
                    displayMenu()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(this, error.toString().trim { it <= ' ' }, Toast.LENGTH_SHORT).show()
            }
        )
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(sr)
    }
}