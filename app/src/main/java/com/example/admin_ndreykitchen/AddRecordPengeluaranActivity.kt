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
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddRecordPengeluaranActivity : AppCompatActivity() {
    private lateinit var pemasukan_button: Button
    private lateinit var expense_category: AutoCompleteTextView
    private lateinit var etDate : EditText
    private var categoryList = mutableListOf<String>()
    private var date: Long = 0
    private lateinit var back_btn : ImageView
    private lateinit var save_btn: com.google.android.material.button.MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_addtransaction_pengeluaran)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        back_btn = findViewById(R.id.back_btn)
        back_btn.setOnClickListener{
            val back_btn_intent = Intent(this@AddRecordPengeluaranActivity, MainActivity::class.java)
            startActivity(back_btn_intent)
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
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH)
        val currentDate = sdf.parse(sdf.format(System.currentTimeMillis())) //take current date
        date = currentDate!!.time //initialized date value to current date as the default value
        etDate.setOnClickListener {
            clickDatePicker()
        }

        //save record
        save_btn = findViewById(R.id.save_btn)
        save_btn.setOnClickListener{
            savePengeluaranRecord()
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
                etDate.setText(selectedDate)

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

    fun savePengeluaranRecord() {
        val judulEditText = findViewById<EditText>(R.id.judul_edittext)
        val jumlahPengeluaranEditText = findViewById<EditText>(R.id.jumlah_pengeluaran_editext)
        val catatanEditText = findViewById<EditText>(R.id.catatan_edittext)

        val judul = judulEditText.text.toString()
        val jumlahPengeluaran = jumlahPengeluaranEditText.text.toString()
        val catatan = catatanEditText.text.toString()

        val dateFromEditText = etDate.text.toString() // Get the selected date from etDate EditText

        val sdf = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        val currentTime = sdf.format(Calendar.getInstance().time) // Get the current time

        val date = "$dateFromEditText $currentTime" // Combine date from EditText and current time


        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/PostPengeluaran?title=" +
                judul + "&amount=" +
                jumlahPengeluaran + "&note=" +
                catatan + "&date=" +
                date

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
                        Toast.makeText(this@AddRecordPengeluaranActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        // Registration successful
                        Toast.makeText(
                            this@AddRecordPengeluaranActivity,
                            "Record has been added",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@AddRecordPengeluaranActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } catch (e: JSONException) {
                    // Handle JSON parsing error
                    e.printStackTrace()
                    Toast.makeText(this@AddRecordPengeluaranActivity, "failed", Toast.LENGTH_SHORT).show()
                }
            }
        ) { Toast.makeText(this@AddRecordPengeluaranActivity, "Registration failed", Toast.LENGTH_SHORT).show() }

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(sr)
    }
}
