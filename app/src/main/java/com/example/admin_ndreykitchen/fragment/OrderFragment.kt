package com.example.admin_ndreykitchen.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin_ndreykitchen.AddMenuActivity
import com.example.admin_ndreykitchen.R
import com.example.admin_ndreykitchen.adapter.MenuAdapter
import com.example.admin_ndreykitchen.adapter.OrderAdapter
import com.example.admin_ndreykitchen.model.MenuModel
import com.example.admin_ndreykitchen.model.OrderItemModel
import com.example.admin_ndreykitchen.model.OrderModel
import org.json.JSONArray
import org.json.JSONException


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val REFRESH_INTERVAL = 30000L // 30 seconds in milliseconds

class OrderFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val orderList = mutableListOf<OrderModel>()
    private val orderItemList = mutableListOf<OrderItemModel>()
    private lateinit var rvOrder: RecyclerView
    private lateinit var ivRefresh: ImageView
    private val handler = Handler(Looper.getMainLooper())



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
        return inflater.inflate(R.layout.fragment_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rvOrder = view.findViewById(R.id.rvOrder)

        rvOrder.layoutManager = GridLayoutManager(requireContext(), 1)

        val horizontalSpace =resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin)
        val verticalSpace = resources.getDimensionPixelSize(R.dimen.activity_vertical_margin)
        rvOrder.addItemDecoration(SpaceItemDecoration(horizontalSpace, verticalSpace))

        getAllOrderList(requireContext())

        ivRefresh = view.findViewById(R.id.ivRefresh)
        ivRefresh.setOnClickListener {
            getAllOrderList(requireContext())
        }

        scheduleRefresh()
    }

    private fun scheduleRefresh() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                getAllOrderList(requireContext())
                handler.postDelayed(this, REFRESH_INTERVAL)
            }
        }, REFRESH_INTERVAL)
    }

    private fun getOrderById(context: Context) {

        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/geAllOrder"
        val sr = StringRequest(
            Request.Method.GET,
            urlEndPoints,
            { response ->
                try {
                    val order = JSONArray(response)
                    if (order.length() > 0) {
                        orderList.clear()
                        for (i in 0 until order.length()) {
                            val orderJson = order.getJSONObject(i)

                            val id_order = orderJson.getString("_id")
                            val id_user = orderJson.getString("id_user")
                            val fullname = orderJson.getString("fullname")
                            val amount_order = orderJson.getInt("amount")
                            val date_order = orderJson.getString("date")
                            val payment_order = orderJson.getString("payment")
                            val status_order = orderJson.getString("status")

                            val orders = OrderModel(
                                id_order,
                                id_user,
                                fullname,
                                amount_order,
                                date_order,
                                payment_order,
                                status_order
                            )
                            orderList.add(orders)
                        }
                        displayRecords()
                        Log.d("RecordFragment", "recordList: $orderList")
                    }
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

    private fun getAllOrderList(context: Context) {
        val urlEndPoints =
            "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/getOrderHistoryMenu"
        val sr = StringRequest(
            Request.Method.GET,
            urlEndPoints,
            { response ->
                try {
                    val orderItems = JSONArray(response)
                    if (orderItems.length() > 0) {
                        orderItemList.clear() // Clearing the list here
                        for (i in 0 until orderItems.length()) {
                            // Adding items to itemMenuList here
                            val orderItemJson = orderItems.getJSONObject(i)

                            val id_orderItem = orderItemJson.getString("_id")
                            val id_order = orderItemJson.getString("id_order")
                            val item_orderItem = orderItemJson.getString("item")
                            val quantity_orderItem = orderItemJson.getInt("quantity")

                            val orderItem = OrderItemModel(id_orderItem, id_order, item_orderItem, quantity_orderItem)
                            orderItemList.add(orderItem)
                        }
                        Log.d("OrderFragment", "recordList: $orderItemList")
                        getOrderById(requireContext())
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
        val orderAdapter = OrderAdapter(orderList, orderItemList)
        rvOrder.adapter = orderAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}