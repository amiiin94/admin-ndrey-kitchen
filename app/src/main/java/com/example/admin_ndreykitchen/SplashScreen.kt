package com.example.admin_ndreykitchen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)

        setContentView(R.layout.splash_screen)

        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)

        Handler().postDelayed({
            if (sharedPreferences.getString("username", null) == null) {
                val loginIntent = Intent(this@SplashScreen, LoginActivity::class.java)
                startActivity(loginIntent)
            } else {
                val profileIntent = Intent(this@SplashScreen, MainActivity::class.java)
                startActivity(profileIntent)
            }

            finish()
        }, 2000)
    }
}
