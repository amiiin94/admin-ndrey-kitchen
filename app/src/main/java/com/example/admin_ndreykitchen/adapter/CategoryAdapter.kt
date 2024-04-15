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
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin_ndreykitchen.EditCategory
import com.example.admin_ndreykitchen.EditMenuActivity
import com.example.admin_ndreykitchen.R
import com.example.admin_ndreykitchen.model.CategoryModel
import com.example.admin_ndreykitchen.model.MenuModel
import com.squareup.picasso.Picasso
import java.util.Locale

class CategoryAdapter(private val categoryList: MutableList<CategoryModel>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val menuView = inflater.inflate(R.layout.item_category, parent, false)
        return ViewHolder(menuView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categoryList[position]
        holder.tvCategory.text = category.name_category

        holder.ivEdit.setOnClickListener {
            val intent = Intent(context, EditCategory::class.java).apply {
                // Pass the menu ID or any other necessary data to EditMenuActivity
                putExtra("id_category", category.id_category)
                putExtra("name_category", category.name_category)
            }
            context.startActivity(intent)
        }

        holder.ivDelete.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirmation")
                .setMessage("Apakah Anda yakin ingin menghapus menu?")
                .setPositiveButton("Yes") { dialog, which ->
                    // Proceed with the action
                    deleteCategoryById(category.id_category, position)
                }
                .setNegativeButton("No") { dialog, which ->

                }
                .show()

        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val ivEdit: ImageView = itemView.findViewById(R.id.ivEdit)
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
    }

    private fun deleteCategoryById(categoryId: String, position: Int) {
        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/deleteCategoryById?_id=$categoryId"
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
        categoryList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }
}