package com.example.admin_ndreykitchen.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin_ndreykitchen.CategoryActivity
import com.example.admin_ndreykitchen.EditProfile
import com.example.admin_ndreykitchen.LoginActivity
import com.example.admin_ndreykitchen.ModalAwalActivity
import com.example.admin_ndreykitchen.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var logout: LinearLayout
    private lateinit var picture: TextView
    private lateinit var flModalAwal: FrameLayout
    private lateinit var resetTransaction: LinearLayout
    private lateinit var llEditKategori: LinearLayout
    private lateinit var name_textview: TextView
    private lateinit var email_textview: TextView
    private lateinit var llEditProfil: LinearLayout

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
        // Initialize sharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)


        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        picture = view.findViewById(R.id.picture)

        llEditProfil = view.findViewById(R.id.llEditProfil)
        llEditProfil.setOnClickListener{
            val intent = Intent(requireContext(), EditProfile::class.java)
            startActivity(intent)
        }

        llEditKategori = view.findViewById(R.id.llEditKategori)
        llEditKategori.setOnClickListener {
            val intent = Intent(requireContext(), CategoryActivity::class.java)
            startActivity(intent)
        }

        logout = view.findViewById(R.id.logout)
        logout.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Confirmation")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Yes") { dialog, which ->
                    // Proceed with the action
                    logout()
                }
                .setNegativeButton("No") { dialog, which ->

                }
                .show()
        }

        resetTransaction = view.findViewById(R.id.resetTransaction)
        resetTransaction.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Confirmation")
                .setMessage("Apakah Anda yakin ingin menghapus semua transaksi?")
                .setPositiveButton("Yes") { dialog, which ->
                    // Proceed with the action
                    deleteAllRecord()
                }
                .setNegativeButton("No") { dialog, which ->

                }
                .show()
        }


        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get username from SharedPreferences
        val username = sharedPreferences.getString("username", "")
        val email = sharedPreferences.getString("email", "")
        name_textview = view.findViewById(R.id.name_textview)
        email_textview = view.findViewById(R.id.email_textview)

        name_textview.text = username.toString()
        email_textview.text = email.toString()


        // Set the first letter of the username as the text of the picture TextView
        if (!username.isNullOrEmpty()) {
            picture.text = username[0].toString().uppercase()
        } else {
            // Handle the case where username is empty or null
            picture.text = ""
        }

        // Modal Awal
        flModalAwal = view.findViewById(R.id.flModalAwal)
        flModalAwal.setOnClickListener {
            val intent = Intent(requireContext(), ModalAwalActivity::class.java)
            startActivity(intent)
        }

    }


    private fun logout() {
        val sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun deleteAllRecord() {
        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/deleteAllRecord"
        val sr = StringRequest(
            Request.Method.DELETE,
            urlEndPoints,
            { response ->
                Toast.makeText(requireContext(), "Records deleted successfully", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Toast.makeText(requireContext(), "Error deleting records: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        val requestQueue = Volley.newRequestQueue(requireContext().applicationContext)
        requestQueue.add(sr)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}