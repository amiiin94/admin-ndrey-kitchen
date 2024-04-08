package com.example.admin_ndreykitchen.model

data class MenuModel(
    val id_menu: String,
    val nama_menu: String,
    val harga_menu: Int,
    val image_menu: String,
    val deskripsi_menu: String,
    val kategori_menu: String,
    var quantity: Int = 0 // Quantity of the menu item, initially set to 0

)
