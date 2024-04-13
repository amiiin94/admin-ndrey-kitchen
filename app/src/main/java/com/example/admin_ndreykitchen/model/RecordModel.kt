package com.example.admin_ndreykitchen.model

class RecordModel (
    val id_record: String? = null,
    val type_record: String? = null,
    val title_record: String? = null,
    val amount_record: Int? = 0,
    val date_record: String? = null,
    val note_record: String? = null,
    val category_recod: String? = null
)

class ItemModel(
    val _id: String,
    val record_id: String,
    val item: String,
    val quantity: Int

)