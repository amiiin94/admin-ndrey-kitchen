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

class EditCategory : AppCompatActivity() {
    private lateinit var back_btn: ImageView
    private lateinit var etKategori: EditText
    private lateinit var save_btn: com.google.android.material.button.MaterialButton
    private lateinit var kategori: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_category)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeItem()

        back_btn.setOnClickListener {
            val mainActivityIntent = Intent(this@EditCategory, MainActivity::class.java)
            mainActivityIntent.putExtra("selected_tab", R.id.profile)
            startActivity(mainActivityIntent)
        }
        save_btn.setOnClickListener {
            putCategoryById()
        }
    }

    private fun initializeItem() {
        back_btn = findViewById(R.id.back_btn)
        etKategori = findViewById(R.id.etKategori)
        save_btn = findViewById(R.id.save_btn)

        kategori = intent.getStringExtra("name_category") ?: ""

        etKategori.setText(kategori)
    }

    private fun putCategoryById() {

        val id_category = intent.getStringExtra("id_category")
        val category = etKategori.text.toString()

        val urlEndPoints =
            "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/putCategoryById?_id=$id_category&category=$category"

        val sr = StringRequest(
            Request.Method.PUT,
            urlEndPoints,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)

                    // Check if the response contains an error field
                    if (jsonResponse.has("error")) {
                        val errorMessage = jsonResponse.getString("error")
                        // Display toast with the error message
                        Toast.makeText(this@EditCategory, errorMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        // Registration successful
                        Toast.makeText(
                            this@EditCategory,
                            "Menu has been updated",
                            Toast.LENGTH_SHORT
                        ).show()
                        val mainActivityIntent = Intent(this@EditCategory, CategoryActivity::class.java)

                        startActivity(mainActivityIntent)

                    }
                } catch (e: JSONException) {
                    // Handle JSON parsing error
                    e.printStackTrace()
                    Toast.makeText(this@EditCategory, "failed", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                // Handle Volley error
                error.printStackTrace()
                Toast.makeText(
                    this@EditCategory,
                    "Failed: " + error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
        // Add the request to the Volley request queue
        Volley.newRequestQueue(this@EditCategory).add(sr)
    }
}