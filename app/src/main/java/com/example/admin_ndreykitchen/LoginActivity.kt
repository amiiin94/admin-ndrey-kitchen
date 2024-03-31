package com.example.admin_ndreykitchen

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

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBtn: Button
    private lateinit var username_edittext: EditText
    private lateinit var password_edittext: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Object Id's
        loginBtn = findViewById(R.id.loginbtn)
        username_edittext = findViewById(R.id.username_edittext)
        password_edittext = findViewById(R.id.password_edittext)

        // Onclick listener
        loginBtn.setOnClickListener{
            login()
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun login() {
        val username = username_edittext.text.toString()
        val password = password_edittext.text.toString()

        // You can use the username and password retrieved above in your API request

        // Example URL with username and password placeholders
        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/getAdminbyUsernameandPassword?username=$username&password=$password"

        val sr = StringRequest(
            Request.Method.GET,
            urlEndPoints,
            { response ->
                try {
                    val userJson = JSONObject(response)

                    val user_id = userJson.getString("_id")
                    val username = userJson.getString("username")
                    val email = userJson.getString("email")
                    val password = userJson.getString("password")

                    // Store user data in SharedPreferences
                    val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("user_id", user_id)
                    editor.putString("username", username)
                    editor.putString("email", email)
                    editor.putString("password", password)
                    editor.apply()

                    Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()

                    val profileIntent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(profileIntent)
                } catch (e: JSONException) {
                    Toast.makeText(this@LoginActivity, "Login Unsuccessful!", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this@LoginActivity, error.networkResponse?.statusCode.toString(), Toast.LENGTH_SHORT).show()
            }
        )

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(sr)
    }


}