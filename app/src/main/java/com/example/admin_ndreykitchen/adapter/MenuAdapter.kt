package com.example.admin_ndreykitchen.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.admin_ndreykitchen.R
import com.example.admin_ndreykitchen.model.MenuModel
import com.example.admin_ndreykitchen.model.RecordModel

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
        val menu = menuList[position]
        holder.nama_menu.text = menu.nama_menu
        holder.harga_menu.text = menu.harga_menu.toString()
    }

    override fun getItemCount(): Int {
        // Return the size of the menuList
        return menuList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image_menu: ImageView = itemView.findViewById(R.id.image_menu)
        val nama_menu: TextView = itemView.findViewById(R.id.nama_menu)
        val harga_menu: TextView = itemView.findViewById(R.id.harga_menu)
    }
}
