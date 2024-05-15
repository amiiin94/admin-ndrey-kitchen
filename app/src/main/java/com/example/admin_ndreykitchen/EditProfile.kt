package com.example.admin_ndreykitchen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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

// Ensure proper imports at the beginning

class EditProfile : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var saveBtn: com.google.android.material.button.MaterialButton
    private lateinit var back_btn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeViews()

        saveBtn.setOnClickListener {
            putProfileById()
        }

        back_btn.setOnClickListener {
            val mainActivityIntent = Intent(this@EditProfile, MainActivity::class.java)
            mainActivityIntent.putExtra("selected_tab", R.id.profile)
            startActivity(mainActivityIntent)
        }
    }

    private fun initializeViews() {
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        saveBtn = findViewById(R.id.save_btn)
        back_btn = findViewById(R.id.back_btn)

        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)

        val username = sharedPreferences.getString("username", "")
        val password = sharedPreferences.getString("password", "")

        etUsername.setText(username.toString())
        etPassword.setText(password.toString())
    }

    private fun putProfileById() {
        val userId = sharedPreferences.getString("user_id", "")
        val username = etUsername.text.toString()
        val password = etPassword.text.toString()

        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/putProfileById?_id=$userId&username=$username&password=$password"

        val stringRequest = StringRequest(
            Request.Method.PUT,
            urlEndPoints,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)

                    if (jsonResponse.has("error")) {
                        val errorMessage = jsonResponse.getString("error")
                        Toast.makeText(this@EditProfile, errorMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        val editor = sharedPreferences.edit()
                        editor.putString("username", username)
                        editor.putString("password", password)
                        editor.apply()

                        Toast.makeText(this@EditProfile, "Profile has been updated", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this@EditProfile, "Failed", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                error.printStackTrace()
                Toast.makeText(this@EditProfile, "Failed: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this@EditProfile).add(stringRequest)
    }
}
