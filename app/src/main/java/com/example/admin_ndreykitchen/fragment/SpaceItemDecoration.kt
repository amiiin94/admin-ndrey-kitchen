package com.example.admin_ndreykitchen.fragment

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(private val horizontalSpace: Int, private val verticalSpace: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = horizontalSpace
        outRect.right = horizontalSpace
        outRect.bottom = verticalSpace
    }
}
