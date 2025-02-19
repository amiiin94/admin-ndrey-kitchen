package com.example.admin_ndreykitchen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.admin_ndreykitchen.AddRecordPemasukanActivity
import com.example.admin_ndreykitchen.R
import com.example.admin_ndreykitchen.model.MenuModel
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class AddTransactionAdapter(
    private val menuList: MutableList<MenuModel>,
    private val quantityChangeListener: QuantityChangeListener,
    private val activity: AddRecordPemasukanActivity
) : RecyclerView.Adapter<AddTransactionAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_id: TextView = itemView.findViewById(R.id.tvId)
        val nama_menu: TextView = itemView.findViewById(R.id.nama_menu)
        val harga_menu: TextView = itemView.findViewById(R.id.harga_menu)
        val minus_btn: ImageView = itemView.findViewById(R.id.minus_btn)
        val plus_btn: ImageView = itemView.findViewById(R.id.plus_btn)
        val quantityTextView: TextView = itemView.findViewById(R.id.quantity)

        init {
            // Plus button click listener
            plus_btn.setOnClickListener {
                var quantity = quantityTextView.text.toString().toInt()
                quantity++
                quantityTextView.text = quantity.toString()
                menuList[adapterPosition].quantity = quantity // Update quantity in the corresponding MenuModel
//                if (activity.isFirstClick) {
//                    activity.postIdRecord()
//                    activity.isFirstClick = false // Update the flag to indicate that the button has been clicked
//                }
                quantityChangeListener.onQuantityChanged() // Notify activity of quantity change
            }

            // Minus button click listener
            minus_btn.setOnClickListener {
                var quantity = quantityTextView.text.toString().toInt()
                if (quantity > 0) {
                    quantity--
                    quantityTextView.text = quantity.toString()
                    menuList[adapterPosition].quantity = quantity // Update quantity in the corresponding MenuModel
                    quantityChangeListener.onQuantityChanged() // Notify activity of quantity change
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val menuView = inflater.inflate(R.layout.item_add_transaksi, parent, false)
        return ViewHolder(menuView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menu = menuList[position]
        holder.tv_id.text = menu.id_menu
        holder.nama_menu.text = menu.nama_menu
        holder.harga_menu.text = formatToRupiah(menu.harga_menu)
        holder.quantityTextView.text = menu.quantity.toString()
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    interface QuantityChangeListener {
        fun onQuantityChanged()
    }

    private fun formatToRupiah(value: Int?): String {
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatRupiah.currency = Currency.getInstance("IDR")

        val formattedValue = value?.let { formatRupiah.format(it.toLong()).replace("Rp", "").trim() }

        return "Rp. $formattedValue"
    }


}