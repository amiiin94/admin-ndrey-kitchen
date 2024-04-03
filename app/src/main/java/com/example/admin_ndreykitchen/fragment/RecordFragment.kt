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
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val recordList = mutableListOf<RecordModel>()
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_record = view.findViewById(R.id.rv_record)
        rv_record.layoutManager = GridLayoutManager(requireContext(), 1)

        val horizontalSpace = 0
        val verticalSpace = 8
        rv_record.addItemDecoration(SpaceItemDecoration(horizontalSpace, verticalSpace))

        // Create an empty adapter and set it to the RecyclerView
        val recordAdapter = RecordAdapter(recordList)
        rv_record.adapter = recordAdapter

        getAllrecords(requireContext())
    }

    private fun getAllrecords(context: Context) {
        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/getAllRecord"
        val sr = StringRequest(
            Request.Method.GET,
            urlEndPoints,
            { response ->
                try {
                    recordList.clear()
                    val records = JSONArray(response)
                    for (i in 0 until records.length()) {
                        val recordJson = records.getJSONObject(i)

                        val id_record = recordJson.getString("_id")
                        val type_record = recordJson.getString("type")
                        val title_record = recordJson.getString("title")
                        val amount_record = recordJson.getInt("amount")
                        val date_record = recordJson.getString("date")
                        val note_record = recordJson.getString("note")
                        val quantity_record = recordJson.getInt("quantity")

                        val formattedHarga: String = formatToRupiah(amount_record)

                        val record = RecordModel(id_record, type_record, title_record, formattedHarga, date_record, note_record, quantity_record)
                        recordList.add(record)
                    }
                    Log.d("RecordFragment", "recordList: $recordList")
                    displayMenu()
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

    private fun displayMenu() {
        val recordAdapter = RecordAdapter(recordList)
        rv_record.adapter = recordAdapter
    }

    private fun formatToRupiah(value: Int): String {
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatRupiah.currency = Currency.getInstance("IDR")

        val formattedValue = formatRupiah.format(value).replace("Rp", "").trim()

        return "Rp. $formattedValue"
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PenjualanFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}