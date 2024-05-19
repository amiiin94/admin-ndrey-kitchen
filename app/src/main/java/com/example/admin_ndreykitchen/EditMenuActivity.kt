package com.example.admin_ndreykitchen

import android.content.Intent
import android.os.Bundle
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

class EditMenuActivity : AppCompatActivity() {
    private lateinit var etNama: EditText
    private lateinit var etKategori: EditText
    private lateinit var etHarga: EditText
    private lateinit var etDeskripsi: EditText
    private lateinit var etImage: EditText
    private lateinit var back_btn: ImageView
    private lateinit var save_btn: Button
    private lateinit var menuId: String
    private lateinit var nama: String
    private var harga: Int = 0
    private lateinit var image: String
    private lateinit var deskripsi: String
    private lateinit var kategori: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeItems()

        save_btn.setOnClickListener {
            putMenuById()
        }

        back_btn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("selected_tab", R.id.menu)
            }
            startActivity(intent)
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

        // Retrieve data from intent
        nama = intent.getStringExtra("nama_menu") ?: ""
        harga = intent.getIntExtra("harga_menu", 0)
        image = intent.getStringExtra("image_menu") ?: ""
        deskripsi = intent.getStringExtra("deskripsi_menu") ?: ""
        kategori = intent.getStringExtra("kategori_menu") ?: ""

        // Set the data to the views
        etNama.setText(nama)
        etKategori.setText(kategori)
        etHarga.setText(harga.toString())
        etDeskripsi.setText(deskripsi)
        etImage.setText(image)
    }

    private fun putMenuById() {

        val id_menu = intent.getStringExtra("menu_id")
        val nama = etNama.text.toString()
        val kategori = etKategori.text.toString()
        val harga = etHarga.text.toString()
        val deskripsi = etDeskripsi.text.toString()
        val image = etImage.text.toString()

        val urlEndPoints =
            "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/putMenuById?_id=$id_menu&nama=$nama&kategori=$kategori&harga=$harga&deskripsi=$deskripsi&image=$image"

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
                        Toast.makeText(this@EditMenuActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        // Registration successful
                        Toast.makeText(
                            this@EditMenuActivity,
                            "Menu has been updated",
                            Toast.LENGTH_SHORT
                        ).show()

                        val mainActivityIntent = Intent(this@EditMenuActivity, MainActivity::class.java)
                        mainActivityIntent.putExtra("selected_tab", R.id.menu)
                        startActivity(mainActivityIntent)

                    }
                } catch (e: JSONException) {
                    // Handle JSON parsing error
                    e.printStackTrace()
                    Toast.makeText(this@EditMenuActivity, "failed", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                // Handle Volley error
                error.printStackTrace()
                Toast.makeText(
                    this@EditMenuActivity,
                    "Failed: " + error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
        // Add the request to the Volley request queue
        Volley.newRequestQueue(this@EditMenuActivity).add(sr)
    }
}
