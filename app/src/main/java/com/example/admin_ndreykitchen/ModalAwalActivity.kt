package com.example.admin_ndreykitchen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
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

class ModalAwalActivity : AppCompatActivity() {
    private lateinit var etModalAwal: EditText
    private lateinit var save_btn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_modal_awal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        showModalAwal()

        save_btn = findViewById(R.id.save_btn)
        save_btn.setOnClickListener {
            putModalAwal()
        }
    }

    private fun showModalAwal() {
        etModalAwal = findViewById(R.id.etModalAwal)
        getModalAwal()

    }

    private fun getModalAwal() {
        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/getModalAwal"
        val sr = StringRequest(
            Request.Method.GET,
            urlEndPoints,
            { response ->
                try {
                    val amount = response.toInt() // Parse the response as an integer
                    etModalAwal.setText(amount.toString()) // Set the amount to EditText
                } catch (e: NumberFormatException) {
                    // Handle parsing error
                    e.printStackTrace()
                    Toast.makeText(this, "Invalid response format", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Error", error.toString())
                Toast.makeText(this, error.toString().trim { it <= ' ' }, Toast.LENGTH_SHORT).show()
            }
        )
        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(sr)
    }

    private fun putModalAwal() {
        val type = "modal awal"
        val amount = etModalAwal.text.toString()

        val urlEndPoints =
            "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/putModalAwalByType?type=$type&amount=$amount"

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
                        Toast.makeText(this@ModalAwalActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        // Registration successful
                        Toast.makeText(
                            this@ModalAwalActivity,
                            "Modal awal has been added",
                            Toast.LENGTH_SHORT
                        ).show()

                        val mainActivityIntent = Intent(this@ModalAwalActivity, MainActivity::class.java)
                        mainActivityIntent.putExtra("selected_tab", R.id.profile)
                        startActivity(mainActivityIntent)

                    }
                } catch (e: JSONException) {
                    // Handle JSON parsing error
                    e.printStackTrace()
                    Toast.makeText(this@ModalAwalActivity, "failed", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                // Handle Volley error
                error.printStackTrace()
                Toast.makeText(
                    this@ModalAwalActivity,
                    "Failed: " + error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
        // Add the request to the Volley request queue
        Volley.newRequestQueue(this@ModalAwalActivity).add(sr)
    }

}