package com.example.admin_ndreykitchen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin_ndreykitchen.adapter.CategoryAdapter
import com.example.admin_ndreykitchen.fragment.SpaceItemDecoration
import com.example.admin_ndreykitchen.model.CategoryModel
import org.json.JSONArray
import org.json.JSONException

class CategoryActivity : AppCompatActivity() {
    private val categoryList = mutableListOf<CategoryModel>()
    private lateinit var rvCategory: RecyclerView
    private lateinit var addCategory: TextView
    private lateinit var back_btn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_category)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        back_btn = findViewById(R.id.back_btn)
        back_btn.setOnClickListener {
            finish()
        }

        addCategory = findViewById(R.id.addCategory)
        addCategory.setOnClickListener {
            val intent = Intent(this, AddCategory::class.java)
            startActivity(intent)
        }

        getAllCategory()
    }

    private fun getAllCategory() {
        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/getAllCategory"
        val sr = StringRequest(
            Request.Method.GET,
            urlEndPoints,
            { response ->
                try {
                    categoryList.clear()
                    val categories = JSONArray(response)
                    for (i in 0 until categories.length()) {
                        val menuJson = categories.getJSONObject(i)

                        val id_category = menuJson.getString("_id")
                        val name_category = menuJson.getString("category")

                        val category = CategoryModel(id_category, name_category)
                        categoryList.add(category)
                    }
                    displayMenu()
                    Log.d("Category", categoryList.toString())

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

    private fun displayMenu() {
        rvCategory = findViewById(R.id.rvCategory)
        rvCategory.layoutManager = LinearLayoutManager(this)

        val horizontalSpace = resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin)
        val verticalSpace = resources.getDimensionPixelSize(R.dimen.activity_vertical_margin)
        rvCategory.addItemDecoration(SpaceItemDecoration(horizontalSpace, verticalSpace))

        val categoryAdapter = CategoryAdapter(categoryList)
        rvCategory.adapter = categoryAdapter
    }
}
