package com.example.admin_ndreykitchen.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin_ndreykitchen.R
import com.example.admin_ndreykitchen.adapter.RecordAdapter
import com.example.admin_ndreykitchen.model.ItemModel
import com.example.admin_ndreykitchen.model.RecordModel
import org.json.JSONArray
import org.json.JSONException
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecordFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private val recordList = mutableListOf<RecordModel>()
    private val itemList = mutableListOf<ItemModel>()
    private lateinit var rv_record: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_record = view.findViewById(R.id.rv_record)
        rv_record.layoutManager = GridLayoutManager(requireContext(), 1)

        val horizontalSpace = resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin)
        val verticalSpace = resources.getDimensionPixelSize(R.dimen.activity_vertical_margin)
        rv_record.addItemDecoration(SpaceItemDecoration(horizontalSpace, verticalSpace))


        getAllItemListMenu(requireContext())
        getAllrecords(requireContext())
    }

    private fun getAllrecords(context: Context) {
        val urlEndPoints =
            "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/getAllRecord"
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
                                recordJson.optString(
                                    "title",
                                    ""
                                ) // Use optString to avoid JSONException
                            } else {
                                "" // Provide default value if "title" field is missing
                            }
                            val amount_record = recordJson.getInt("amount")
                            val date_record = recordJson.getString("date")
                            val note_record = recordJson.getString("note")

                            val formattedHarga: String = formatToRupiah(amount_record)

                            val record = RecordModel(
                                id_record,
                                type_record,
                                title_record,
                                formattedHarga,
                                date_record,
                                note_record
                            )
                            recordList.add(record)
                        }
                        displayRecords()
                        Log.d("RecordFragment", "recordList: $recordList")

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(context, error.toString().trim { it <= ' ' }, Toast.LENGTH_SHORT)
                    .show()
            }
        )
        val requestQueue = Volley.newRequestQueue(context.applicationContext)
        requestQueue.add(sr)
    }

    private fun displayRecords() {
        val recordAdapter = RecordAdapter(recordList, itemList)
        rv_record.adapter = recordAdapter
    }


    private fun formatToRupiah(value: Int): String {
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatRupiah.currency = Currency.getInstance("IDR")

        val formattedValue = formatRupiah.format(value).replace("Rp", "").trim()

        return "Rp. $formattedValue"
    }

    private fun getAllItemListMenu(context: Context) {
        val urlEndPoints =
            "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/getItemMenu"
        val sr = StringRequest(
            Request.Method.GET,
            urlEndPoints,
            { response ->
                try {
                    val records = JSONArray(response)
                    if (records.length() > 0) {
                        itemList.clear() // Clearing the list here
                        for (i in 0 until records.length()) {
                            // Adding items to itemMenuList here
                            val recordJson = records.getJSONObject(i)
                            val _id = recordJson.getString("_id")
                            val record_id = recordJson.getString("record_id")
                            val item = recordJson.getString("item")
                            val quantity = recordJson.getInt("quantity")

                            val record = ItemModel(_id, record_id, item, quantity)
                            itemList.add(record)
                        }

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(context, error.toString().trim { it <= ' ' }, Toast.LENGTH_SHORT)
                    .show()
            }
        )
        val requestQueue = Volley.newRequestQueue(context.applicationContext)
        requestQueue.add(sr)
    }

}