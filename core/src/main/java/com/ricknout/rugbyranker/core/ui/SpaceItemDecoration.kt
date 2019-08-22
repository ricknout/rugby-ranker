package com.ricknout.rugbyranker.core.ui

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.withStyledAttributes
import androidx.recyclerview.widget.RecyclerView
import com.ricknout.rugbyranker.core.R

class SpaceItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private var paddingTop: Int = 0
    private var paddingHorizontal: Int = 0

    init {
        context.withStyledAttributes(
                R.style.ItemDecoration_RugbyRanker_Space,
                R.styleable.SpaceItemDecoration
        ) {
            paddingTop = getDimensionPixelSizeOrThrow(R.styleable.SpaceItemDecoration_android_paddingTop)
            paddingHorizontal = getDimensionPixelSizeOrThrow(R.styleable.SpaceItemDecoration_paddingHorizontal)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.set(paddingHorizontal, paddingTop, paddingHorizontal, 0)
    }
}
