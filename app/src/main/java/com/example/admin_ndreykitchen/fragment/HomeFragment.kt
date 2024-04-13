import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin_ndreykitchen.R
import com.example.admin_ndreykitchen.model.ItemModel
import com.example.admin_ndreykitchen.model.RecordModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.security.KeyStore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class HomeFragment : Fragment() {

    private lateinit var pemasukan: TextView
    private lateinit var pengeluaran: TextView
    private lateinit var total_transaksi: TextView
    private lateinit var makanan_terjual: TextView
    private lateinit var selamat_datang: TextView
    private lateinit var tvProfit: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private val recordList = mutableListOf<RecordModel>()
    private val itemList = mutableListOf<ItemModel>()
    private lateinit var timeSpanOption: Spinner

    private var profit: Int = 0
    private var pengeluaranAmount: Int = 0
    private var pemasukanAmount: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        // Initialize sharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize TextViews
        pemasukan = view.findViewById(R.id.pemasukan)
        pengeluaran = view.findViewById(R.id.pengeluaran)
        total_transaksi = view.findViewById(R.id.total_transaksi)
        makanan_terjual = view.findViewById(R.id.makanan_terjual)
        selamat_datang = view.findViewById(R.id.selamatDatangNama)
        tvProfit = view.findViewById(R.id.tvProfit)

        // Initialize Spinner
        timeSpanOption = view.findViewById(R.id.timeSpanSpinner)
        // Get username from SharedPreferences
        // Get username from SharedPreferences
        val username = sharedPreferences.getString("username", "")
        selamat_datang.text = "Selamat Datang, $username"

        // Setup the spinner
        // Setup the spinner
        val timeSpanOptions = arrayOf("All Time", "This Month", "This Week", "Today")
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, timeSpanOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeSpanOption.adapter = adapter

        // Spinner selection listener
        timeSpanOption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedTimeSpan = when (position) {
                    0 -> TimeSpan.ALL_TIME
                    1 -> TimeSpan.THIS_MONTH
                    2 -> TimeSpan.THIS_WEEK
                    3 -> TimeSpan.TODAY
                    else -> TimeSpan.ALL_TIME
                }
                getAllrecords(selectedTimeSpan)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        // Initially fetch data for all time
        getAllrecords(TimeSpan.ALL_TIME)
        chartMenu()
    }



    private fun displayHomeFragmentData(records: List<RecordModel>, items: List<ItemModel>, timeSpan: TimeSpan) {
        val filteredRecords = filterRecordsByTimeSpan(records, timeSpan)

        pemasukanAmount = filteredRecords.filter { it.type_record == "pemasukan" }.sumOf { it.amount_record ?: 0 }
        pengeluaranAmount = filteredRecords.filter { it.type_record == "pengeluaran" }.sumOf { it.amount_record ?: 0 }
        val totalTransaksi = filteredRecords.size

        val makananTerjual = items.filter { item -> filteredRecords.any { it.id_record == item.record_id } }
            .sumOf { it.quantity }
        profit = pemasukanAmount - pengeluaranAmount

        if (profit >= 0) {
            tvProfit.text = "+Rp $profit"
        } else {
            tvProfit.text = "-Rp ${-profit}" // Displaying negative profit as a positive value
            tvProfit.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
        }
        pemasukan.text = "Rp$pemasukanAmount"
        pengeluaran.text = "Rp$pengeluaranAmount"
        total_transaksi.text = "$totalTransaksi pcs"
        makanan_terjual.text = "$makananTerjual pcs"

        setupBarChart()
        setupLineChart()
    }

    private fun getAllrecords(timeSpan: TimeSpan) {
        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/getAllRecord"
        val sr = StringRequest(
            Request.Method.GET,
            urlEndPoints,
            { response ->
                try {
                    val records = JSONArray(response)
                    if (records.length() > 0) {
                        recordList.clear()
                        for (i in 0 until records.length()) {
                            val recordJson = records.getJSONObject(i)
                            val id_record = recordJson.getString("_id")
                            val type_record = recordJson.getString("type")
                            val title_record = if (recordJson.has("title")) {
                                recordJson.optString("title", "")
                            } else {
                                ""
                            }
                            val amount_record = recordJson.getInt("amount")
                            val date_record = recordJson.getString("date")
                            val note_record = recordJson.getString("note")
                            val record = RecordModel(id_record, type_record, title_record, amount_record, date_record, note_record)
                            if (isWithinTimeSpan(parseDate(date_record), timeSpan)) {
                                recordList.add(record)
                            }
                        }
                        // Update UI with new data
                        displayHomeFragmentData(recordList, itemList, timeSpan)
                        // Fetch item list
                        getAllItemListMenu(requireContext())
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(context, error.toString().trim(), Toast.LENGTH_SHORT).show()
            }
        )
        val requestQueue = Volley.newRequestQueue(requireContext().applicationContext)
        requestQueue.add(sr)
    }


    private fun getAllItemListMenu(context: Context) {
        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/getItemMenu"
        val sr = StringRequest(
            Request.Method.GET,
            urlEndPoints,
            { response ->
                try {
                    val records = JSONArray(response)
                    if (records.length() > 0) {
                        itemList.clear()
                        for (i in 0 until records.length()) {
                            val recordJson = records.getJSONObject(i)
                            val _id = recordJson.getString("_id")
                            val record_id = recordJson.getString("record_id")
                            val item = recordJson.getString("item")
                            val quantity = recordJson.getInt("quantity")
                            val record = ItemModel(_id, record_id, item, quantity)
                            itemList.add(record)
                        }
                        // Inside the getAllItemListMenu function
                        displayHomeFragmentData(recordList, itemList, TimeSpan.ALL_TIME)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(context, error.toString().trim(), Toast.LENGTH_SHORT).show()
            }
        )
        val requestQueue = Volley.newRequestQueue(context.applicationContext)
        requestQueue.add(sr)
    }

    enum class TimeSpan {
        ALL_TIME,
        THIS_MONTH,
        THIS_WEEK,
        TODAY
    }

    // Function to parse date string into Date object
    private fun parseDate(dateString: String): Date {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
        return dateFormat.parse(dateString) ?: Date()
    }

    // Function to check if a date is within the specified time span
    private fun isWithinTimeSpan(date: Date, timeSpan: TimeSpan): Boolean {
        val currentDate = Date()
        return when (timeSpan) {
            TimeSpan.ALL_TIME -> true
            TimeSpan.THIS_MONTH -> {
                val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
                val recordMonth = Calendar.getInstance()
                recordMonth.time = date
                currentMonth == recordMonth.get(Calendar.MONTH)
            }
            TimeSpan.THIS_WEEK -> {
                val currentWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
                val recordWeek = Calendar.getInstance()
                recordWeek.time = date
                currentWeek == recordWeek.get(Calendar.WEEK_OF_YEAR)
            }
            TimeSpan.TODAY -> {
                val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
                val recordDay = Calendar.getInstance()
                recordDay.time = date
                currentDay == recordDay.get(Calendar.DAY_OF_YEAR)
            }
        }
    }

    // Function to filter records based on the selected time span
    private fun filterRecordsByTimeSpan(records: List<RecordModel>, timeSpan: TimeSpan): List<RecordModel> {
        return records.filter { record ->
            val recordDate = parseDate(record.date_record ?: "")
            isWithinTimeSpan(recordDate, timeSpan)
        }
    }

    private fun chartMenu() {
        val chartMenuRadio: RadioGroup = requireView().findViewById(R.id.RadioGroup)
        val lineChart: LineChart = requireView().findViewById(R.id.lineChart)
        val barChart: BarChart = requireView().findViewById(R.id.barChart)

        chartMenuRadio.setOnCheckedChangeListener { _, checkedID ->
            if (checkedID == R.id.rbBarChart){
                barChart.visibility = View.VISIBLE
                lineChart.visibility = View.GONE
            }
            if (checkedID == R.id.rbLineChart){
                barChart.visibility = View.GONE
                lineChart.visibility = View.VISIBLE
            }
        }
    }

    private fun setupBarChart() {
        //Bar Chart Library Dependency : https://github.com/PhilJay/MPAndroidChart
        val netAmountRangeDate: TextView = requireView().findViewById(R.id.netAmountRange)
        netAmountRangeDate.text = "$profit" //show the net amount on textview

        val barChart: BarChart = requireView().findViewById(R.id.barChart)

        val barEntries = arrayListOf<BarEntry>()
        barEntries.add(BarEntry(1f, pemasukanAmount.toFloat() ))
        barEntries.add(BarEntry(2f, pengeluaranAmount.toFloat()))

        val xAxisValue= arrayListOf<String>("","Expense", "Income")

        //custom bar chart :
        barChart.animateXY(500, 500) //create bar chart animation
        barChart.description.isEnabled = false
        barChart.setDrawValueAboveBar(true)
        barChart.setDrawBarShadow(false)
        barChart.setPinchZoom(false)
        barChart.isDoubleTapToZoomEnabled = false
        barChart.setScaleEnabled(false)
        barChart.setFitBars(true)
        barChart.legend.isEnabled = false

        barChart.axisRight.isEnabled = false
        barChart.axisLeft.isEnabled = false
        barChart.axisLeft.axisMinimum = 0f


        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValue)

        val barDataSet = BarDataSet(barEntries, "")
        barDataSet.setColors(
            resources.getColor(R.color.orange_mainColor),
            resources.getColor(R.color.orange_pudar)
        )
        barDataSet.valueTextColor = Color.BLACK
        barDataSet.valueTextSize = 15f
        barDataSet.isHighlightEnabled = false

        //setup bar data
        val barData = BarData(barDataSet)
        barData.barWidth = 0.5f

        barChart.data = barData
    }

    private fun setupLineChart() {
        // Line Chart Library Dependency: https://github.com/PhilJay/MPAndroidChart
        val netAmountRangeDate: TextView = requireView().findViewById(R.id.netAmountRange)
        netAmountRangeDate.text = "$profit" // Show the net amount on textview

        val lineChart: LineChart = requireView().findViewById(R.id.lineChart)

        // Assuming your records contain date strings in the format "dd/MM/yyyy HH:mm"
        val dateTotalMap = mutableMapOf<String, Int>()
        var cumulativeSum = 0 // Initialize cumulative sum

// Populate the dateTotalMap with date as key and net amount as value
        recordList.sortedBy { it.date_record }.forEach { record ->
            val date = record.date_record?.split(" ")?.get(0) // Extracting date part safely
            date?.let { // Check if date is not null
                if (record.type_record == "pemasukan") {
                    cumulativeSum += record.amount_record ?: 0 // Add pemasukan amount
                } else {
                    cumulativeSum -= record.amount_record ?: 0 // Subtract pengeluaran amount
                }
                dateTotalMap[it] = cumulativeSum // Store cumulative sum for the date
            }
        }

        // Convert map entries to sorted list of entries
        val lineEntries = dateTotalMap.entries.sortedBy { it.key }
            .mapIndexed { index, entry -> Entry(index.toFloat(), entry.value.toFloat()) }

        // Custom line chart settings:
        lineChart.animateXY(500, 500) // Create line chart animation
        lineChart.description.isEnabled = false
        lineChart.setDrawGridBackground(false)
        lineChart.setDrawBorders(false)
        lineChart.setPinchZoom(false)
        lineChart.isDoubleTapToZoomEnabled = false
        lineChart.setScaleEnabled(false)
        lineChart.legend.isEnabled = false

        val xAxisValue = dateTotalMap.keys.toTypedArray()

        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisValue)

        val yAxisRight = lineChart.axisRight
        yAxisRight.isEnabled = false

        val lineDataSet = LineDataSet(lineEntries, "")
        lineDataSet.color = resources.getColor(R.color.orange_mainColor)
        lineDataSet.setCircleColor(resources.getColor(R.color.orange_mainColor))
        lineDataSet.setDrawValues(true)
        lineDataSet.valueTextColor = Color.BLACK
        lineDataSet.valueTextSize = 15f
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        val lineData = LineData(lineDataSet)

        lineChart.data = lineData
    }
}
