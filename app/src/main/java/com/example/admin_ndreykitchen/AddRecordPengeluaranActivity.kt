package com.example.admin_ndreykitchen

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
import com.example.admin_ndreykitchen.model.MenuModel
import org.json.JSONArray
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddRecordPengeluaranActivity : AppCompatActivity() {
    private lateinit var pemasukan_button: Button
    private lateinit var expense_category: AutoCompleteTextView
    private lateinit var etDate : EditText
    private var categoryList = mutableListOf<String>()
    private var date: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_addtransaction_pengeluaran)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etDate = findViewById(R.id.tanggal_editext)

        pemasukan_button = findViewById(R.id.pemasukan_button)
        pemasukan_button.setOnClickListener{
            val loginIntent = Intent(this@AddRecordPengeluaranActivity, AddRecordPemasukanActivity::class.java)
            startActivity(loginIntent)
        }

        expense_category = findViewById(R.id.expense_category)

        // Call getCategory function to populate the AutoCompleteTextView
        getCategory(this)

        ///---date picker---
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val currentDate = sdf.parse(sdf.format(System.currentTimeMillis())) //take current date
        date = currentDate!!.time //initialized date value to current date as the default value
        etDate.setOnClickListener {
            clickDatePicker()
        }
    }



    private fun getCategory(context: Context) {
        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/getAllCategory"
        val sr = StringRequest(
            Request.Method.GET,
            urlEndPoints,
            { response ->
                try {
                    val jsonArray = JSONArray(response)

                    for (i in 0 until jsonArray.length()) {
                        categoryList.add(jsonArray.getString(i))
                    }
                    // Log the categoryList here
                    Log.d("CategoryList", categoryList.toString())
                    setupAutoCompleteTextView(categoryList)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(context, error.toString().trim { it <= ' ' }, Toast.LENGTH_SHORT).show()
            }
        )
        val requestQueue = Volley.newRequestQueue(context.applicationContext)
        requestQueue.add(sr)
    }

    private fun setupAutoCompleteTextView(categoryList: List<String>) {
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.expense_category)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categoryList)
        autoCompleteTextView.setAdapter(adapter)
    }

    private fun clickDatePicker() {
        val myCalendar = Calendar.getInstance()
        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->

                val selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
                etDate.text = null
                etDate.hint = selectedDate

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                val theDate = sdf.parse(selectedDate)
                date = theDate!!.time //convert date to millisecond

            },
            year,
            month,
            day
        )
        dpd.show()
    }


}
