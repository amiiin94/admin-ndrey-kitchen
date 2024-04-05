package com.example.admin_ndreykitchen.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.admin_ndreykitchen.R
import com.example.admin_ndreykitchen.model.ItemListMenuModel

class ItemMenuAdapter(private val itemMenuList: List<ItemListMenuModel>) :
    RecyclerView.Adapter<ItemMenuAdapter.ViewHolder>() {

    private lateinit var context: Context
    var recordId: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val menuView = inflater.inflate(R.layout.item_list_menu_pemasukan, parent, false)
        return ViewHolder(menuView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemMenuList[position]
        holder.item.text = item.item
    }

    override fun getItemCount(): Int = itemMenuList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item: TextView = itemView.findViewById(R.id.item)
    }
}
