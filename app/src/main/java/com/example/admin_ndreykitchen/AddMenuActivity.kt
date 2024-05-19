package com.example.admin_ndreykitchen

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
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

class AddMenuActivity : AppCompatActivity() {
    private lateinit var etNama: EditText
    private lateinit var etKategori: EditText
    private lateinit var etHarga: EditText
    private lateinit var etDeskripsi: EditText
    private lateinit var etImage: EditText
    private lateinit var back_btn: ImageView
    private lateinit var save_btn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        setContentView(R.layout.activity_add_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeItems()

        save_btn.setOnClickListener {
            saveMenu()

            val intent = Intent(this@AddMenuActivity, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun initializeItems() {
        etNama = findViewById(R.id.etNama)
        etKategori = findViewById(R.id.etKategori)
        etHarga = findViewById(R.id.etHarga)
        etDeskripsi = findViewById(R.id.etDeskripsi)
        etImage = findViewById(R.id.etImage)
        save_btn = findViewById(R.id.save_btn)
        back_btn = findViewById(R.id.back_btn)
    }

    fun saveMenu() {

        val nama = etNama.text.toString()
        val kategori = etKategori.text.toString()
        val harga = etHarga.text.toString()
        val deskripsi = etDeskripsi.text.toString()
        val image = etImage.text.toString()

        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/postMenu?nama=$nama&kategori=$kategori&harga=$harga&deskripsi=$deskripsi&image=$image"

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
                        Toast.makeText(this@AddMenuActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        // Registration successful
                        Toast.makeText(
                            this@AddMenuActivity,
                            "Menu has been added",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    // Handle JSON parsing error
                    e.printStackTrace()
                    Toast.makeText(this@AddMenuActivity, "failed", Toast.LENGTH_SHORT).show()
                }
            }
        ) { Toast.makeText(this@AddMenuActivity, "failed Added Menu", Toast.LENGTH_SHORT).show() }

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(sr)
    }
}