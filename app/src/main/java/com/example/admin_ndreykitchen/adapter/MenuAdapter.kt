package com.example.admin_ndreykitchen.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin_ndreykitchen.EditMenuActivity
import com.example.admin_ndreykitchen.R
import com.example.admin_ndreykitchen.model.MenuModel
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class MenuAdapter(private val menuList: MutableList<MenuModel>) :
    RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val menuView = inflater.inflate(R.layout.item_menu, parent, false)
        return ViewHolder(menuView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (menuList.isEmpty()) {
            holder.menu_detail.visibility = View.VISIBLE
            holder.tvEmpty.visibility = View.VISIBLE
        } else {
            val menu = menuList[position]
            holder.nama_menu.text = menu.nama_menu
            holder.harga_menu.text = formatToRupiah(menu.harga_menu)
            holder.tvId.text = menu.id_menu
            if (menu.image_menu != "") {
                Picasso.get().load(menu.image_menu).into(holder.image_menu)
            }

            holder.ivEdit.setOnClickListener {
                val intent = Intent(context, EditMenuActivity::class.java).apply {
                    // Pass the menu ID or any other necessary data to EditMenuActivity
                    putExtra("menu_id", menu.id_menu)
                    putExtra("nama_menu", menu.nama_menu)
                    putExtra("harga_menu", menu.harga_menu)
                    putExtra("kategori_menu", menu.kategori_menu)
                    putExtra("deskripsi_menu", menu.deskripsi_menu)
                    putExtra("image_menu", menu.image_menu)
                    // Add more extras if needed
                }
                context.startActivity(intent)
            }

            holder.ivDelete.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Confirmation")
                    .setMessage("Apakah Anda yakin ingin menghapus menu?")
                    .setPositiveButton("Ya") { dialog, which ->
                        // Proceed with the action
                        deleteMenuById(menu.id_menu, position)
                    }
                    .setNegativeButton("Tidak") { dialog, which ->

                    }
                    .show()

            }
        }
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image_menu: ImageView = itemView.findViewById(R.id.image_menu)
        val nama_menu: TextView = itemView.findViewById(R.id.nama_menu)
        val harga_menu: TextView = itemView.findViewById(R.id.harga_menu)
        val kategori_menu: TextView = itemView.findViewById(R.id.kategori_menu)
        val tvId: TextView = itemView.findViewById(R.id.tvId)
        val ivEdit: ImageView = itemView.findViewById(R.id.ivEdit)
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
        val tvEmpty: TextView = itemView.findViewById(R.id.tvEmpty)
        val menu_detail: CardView = itemView.findViewById(R.id.menu_detail)
    }

    private fun deleteMenuById(menuId: String, position: Int) {
        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/deleteMenuById?id=$menuId"
        val sr = StringRequest(
            Request.Method.DELETE,
            urlEndPoints,
            { response ->
                Toast.makeText(context, "Menu deleted successfully", Toast.LENGTH_SHORT).show()
                removeItem(position)
            },
            { error ->
                Toast.makeText(context, "Error deleting menu: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        val requestQueue = Volley.newRequestQueue(context.applicationContext)
        requestQueue.add(sr)
    }

    private fun removeItem(position: Int) {
        menuList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    private fun formatToRupiah(value: Int?): String {
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatRupiah.currency = Currency.getInstance("IDR")

        val formattedValue = value?.let { formatRupiah.format(it.toLong()).replace("Rp", "").trim() }

        return "Rp. $formattedValue"
    }
}