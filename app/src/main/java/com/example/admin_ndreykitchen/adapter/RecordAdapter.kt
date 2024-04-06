package com.example.admin_ndreykitchen.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.admin_ndreykitchen.R
import com.example.admin_ndreykitchen.model.ItemModel
import com.example.admin_ndreykitchen.model.RecordModel

class RecordAdapter(private val recordList: List<RecordModel>, private val itemList: List<ItemModel>) :
    RecyclerView.Adapter<RecordAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val recordView = when (viewType) {
            TYPE_PEMASUKAN -> inflater.inflate(R.layout.item_transaksi_pemasukan, parent, false)
            TYPE_PENGELUARAN -> inflater.inflate(R.layout.item_transaksi_pengeluaran, parent, false)
            else -> throw IllegalArgumentException("Invalid view type")
        }
        return ViewHolder(recordView)
    }

    // Inside onBindViewHolder method of RecordAdapter
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = recordList[position]
        holder.id_record.text = record.id_record
        holder.titleRecord.text = record.title_record
        holder.amountRecord.text = record.amount_record
        holder.dateRecord.text = record.date_record

        // Find the corresponding ItemModel based on the record_id
        val correspondingItem = itemList.find { it.record_id == record.id_record }
        holder.item?.text = correspondingItem?.item ?: "Item not found"
    }


    override fun getItemCount(): Int {
        return recordList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val id_record: TextView = itemView.findViewById(R.id.id_record)
        val titleRecord: TextView = itemView.findViewById(R.id.title_record)
        val amountRecord: TextView = itemView.findViewById(R.id.amount_record)
        val dateRecord: TextView = itemView.findViewById(R.id.date_record)
        val item: TextView? = itemView.findViewById(R.id.item) // Use nullable TextView


    }

    companion object {
        const val TYPE_PEMASUKAN = 1
        const val TYPE_PENGELUARAN = 2
    }

    override fun getItemViewType(position: Int): Int {
        val record = recordList[position]
        return if (record.type_record == "pemasukan") {
            TYPE_PEMASUKAN
        } else {
            TYPE_PENGELUARAN
        }
    }

}