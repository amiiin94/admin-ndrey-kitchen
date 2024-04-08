package com.example.admin_ndreykitchen.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin_ndreykitchen.AddMenuActivity
import com.example.admin_ndreykitchen.LoginActivity
import com.example.admin_ndreykitchen.MainActivity
import com.example.admin_ndreykitchen.model.MenuModel
import com.example.admin_ndreykitchen.R
import com.example.admin_ndreykitchen.adapter.MenuAdapter
import org.json.JSONArray
import org.json.JSONException

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MenuFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private val menuList = mutableListOf<MenuModel>()
    private lateinit var rv_menu: RecyclerView
    private lateinit var tambah_menu: TextView


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
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_menu = view.findViewById(R.id.rv_menu)
        rv_menu.layoutManager = GridLayoutManager(requireContext(), 1)

        val horizontalSpace = resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin)
        val verticalSpace = resources.getDimensionPixelSize(R.dimen.activity_vertical_margin)
        rv_menu.addItemDecoration(SpaceItemDecoration(horizontalSpace, verticalSpace))

        // Create an empty adapter and set it to the RecyclerView
        val menuAdapter = MenuAdapter(menuList)
        rv_menu.adapter = menuAdapter

        // Tambah Menu
        tambah_menu = view.findViewById(R.id.tambah_menu)
        tambah_menu.setOnClickListener {
            val intent = Intent(requireContext(), AddMenuActivity::class.java)
            startActivity(intent)
        }



        getAllMenus(requireContext())
    }

    private fun getAllMenus(context: Context) {
        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/getAllMenus"
        val sr = StringRequest(
            Request.Method.GET,
            urlEndPoints,
            { response ->
                try {
                    menuList.clear()
                    val menus = JSONArray(response)
                    for (i in 0 until menus.length()) {
                        val menuJson = menus.getJSONObject(i)

                        val id_menu = menuJson.getString("_id")
                        val nama_menu = menuJson.getString("nama")
                        val harga_menu = menuJson.getInt("harga")
                        val images = menuJson.getString("image")
                        val deskripsi_menu = menuJson.getString("deskripsi")
                        val kategori_menu = menuJson.getString("kategori")


                        val menu = MenuModel(id_menu, nama_menu, harga_menu, images, deskripsi_menu, kategori_menu)
                        menuList.add(menu)
                    }
                    Log.d("MenuFragment", "menuList: $menuList")
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
        val menuAdapter = MenuAdapter(menuList)
        rv_menu.adapter = menuAdapter
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}