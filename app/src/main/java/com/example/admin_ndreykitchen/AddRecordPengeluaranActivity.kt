package com.example.admin_ndreykitchen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.example.admin_ndreykitchen.model.CategoryModel
import com.example.admin_ndreykitchen.model.MenuModel
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
        val currentDate = Calendar.getInstance().time // take current date without time
        etDate.setText(sdf.format(currentDate)) // set the current date without time to EditText

// Initialize date value to current date without time
        date = currentDate.time

// Set OnClickListener for the EditText to open the DatePickerDialog
        etDate.setOnClickListener {
            clickDatePicker()
        }


        //save record
        save_btn = findViewById(R.id.save_btn)
        save_btn.setOnClickListener{
            val judulEditText = findViewById<EditText>(R.id.judul_edittext)
            val jumlahPengeluaranEditText = findViewById<EditText>(R.id.jumlah_pengeluaran_editext)
            val etCategory = findViewById<AutoCompleteTextView>(R.id.expense_category)
            val etDate = findViewById<EditText>(R.id.tanggal_editext)

            val judul = judulEditText.text.toString().trim()
            val jumlahPengeluaran = jumlahPengeluaranEditText.text.toString().trim()
            val selectedCategory = etCategory.text.toString().trim()
            val selectedDate = etDate.text.toString().trim()

            if (judul.isEmpty() || jumlahPengeluaran.isEmpty() || selectedCategory.isEmpty() || selectedDate.isEmpty()) {
                Toast.makeText(this@AddRecordPengeluaranActivity, "Input tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                savePengeluaranRecord()
            }

        }

        expense_category.setOnClickListener {
            // Show dropdown list when clicked
            expense_category.showDropDown()
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
                        val categoryJson = jsonArray.getJSONObject(i)

                        val id_category = categoryJson.getString("_id")
                        val name_category = categoryJson.getString("category")

                        categoryList.add(name_category)
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

        val dpd = DatePickerDialog(
            this,
            R.style.MyDatePickerDialogStyle, // Apply custom style here
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->

                val selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
                etDate.setText(selectedDate)

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                val theDate = sdf.parse(selectedDate)
                date = theDate!!.time // Convert date to milliseconds

                // Show TimePickerDialog after selecting the date
                clickTimePicker()
            },
            year,
            month,
            day
        )

        // Set the max date to today's date to prevent selecting future dates
        dpd.datePicker.maxDate = System.currentTimeMillis()

        dpd.show()
    }

    private fun clickTimePicker() {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            R.style.MyTimePickerDialogStyle, // Apply custom style here
            { _, selectedHour, selectedMinute ->
                // Handle selected time
                val selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
                // Concatenate selected date and time
                val selectedDateTime = "${etDate.text} $selectedTime"
                // Set the concatenated value to the date EditText
                etDate.setText(selectedDateTime)

                // Optionally, you can also parse the selected date and time here
                // and convert it to milliseconds if needed
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }




    fun savePengeluaranRecord() {
        val judulEditText = findViewById<EditText>(R.id.judul_edittext)
        val jumlahPengeluaranEditText = findViewById<EditText>(R.id.jumlah_pengeluaran_editext)
        val catatanEditText = findViewById<EditText>(R.id.catatan_edittext)
        val etCategory = findViewById<AutoCompleteTextView>(R.id.expense_category)
        val etDate = findViewById<EditText>(R.id.tanggal_editext)

        val judul = judulEditText.text.toString().trim()
        val jumlahPengeluaran = jumlahPengeluaranEditText.text.toString().trim()
        var catatan = catatanEditText.text.toString().trim()
        val selectedCategory = etCategory.text.toString().trim()
        val selectedDate = etDate.text.toString().trim()

        if(catatan == "") {
            catatan = "Tidak ada catatan"
        }

        // Validate that all required fields are filled


        // Proceed with saving the record
        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/PostPengeluaran?title=" +
                judul + "&amount=" +
                jumlahPengeluaran + "&note=" +
                catatan + "&date=" +
                selectedDate + "&category=" +
                selectedCategory

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
                            "Pengeluaran telah ditambah",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@AddRecordPengeluaranActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } catch (e: JSONException) {
                    // Handle JSON parsing error
                    e.printStackTrace()
                    Toast.makeText(this@AddRecordPengeluaranActivity, "Failed to save the record", Toast.LENGTH_SHORT).show()
                }
            },
            {
                Toast.makeText(this@AddRecordPengeluaranActivity, "Registration failed", Toast.LENGTH_SHORT).show()
            }
        )

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(sr)
    }

}
