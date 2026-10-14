package com.example.notebook.ui.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class DecorationAdapter(private val spacing: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val layoutParams = view.layoutParams as? StaggeredGridLayoutManager.LayoutParams ?: return
        val spanIndex = layoutParams.spanIndex
        if (spanIndex == 0) {
            outRect.right = spacing
        } else {
            outRect.left = spacing
        }
    }
}