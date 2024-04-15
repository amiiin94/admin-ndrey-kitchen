package com.example.admin_ndreykitchen

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class AddCategory : AppCompatActivity() {
    private lateinit var back_btn: ImageView
    private lateinit var etKategori: EditText
    private lateinit var save_btn: com.google.android.material.button.MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_category)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeItem()
        back_btn.setOnClickListener {
            finish()
        }
        save_btn.setOnClickListener {
            postCategory()
        }
    }

    private fun initializeItem() {
        back_btn = findViewById(R.id.back_btn)
        etKategori = findViewById(R.id.etKategori)
        save_btn = findViewById(R.id.save_btn)
    }

    fun postCategory() {

        val category = etKategori.text.toString()

        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/postCategory?category=$category"

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
                        Toast.makeText(this@AddCategory, errorMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        // Registration successful
                        Toast.makeText(
                            this@AddCategory,
                            "Menu has been added",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this, CategoryActivity::class.java)
                        startActivity(intent)
                    }
                } catch (e: JSONException) {
                    // Handle JSON parsing error
                    e.printStackTrace()

                }
            }
        ) {  }

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(sr)
    }
}