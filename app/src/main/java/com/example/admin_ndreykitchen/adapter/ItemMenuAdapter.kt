package com.example.admin_ndreykitchen.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.admin_ndreykitchen.R
import com.example.admin_ndreykitchen.model.ItemModel


class ItemMenuAdapter(private val itemList: List<ItemModel>) :
    RecyclerView.Adapter<ItemMenuAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.item_item_menu, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvItem: TextView = itemView.findViewById(R.id.tvItem)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)

        fun bind(item: ItemModel) {
            tvItem.text = item.item
            tvQuantity.text = "(" + item.quantity.toString() + "x)"
        }
    }
}