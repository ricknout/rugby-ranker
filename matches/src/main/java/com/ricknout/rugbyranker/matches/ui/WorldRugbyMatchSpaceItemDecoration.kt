package com.ricknout.rugbyranker.matches.ui

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.recyclerview.widget.RecyclerView
import com.ricknout.rugbyranker.matches.R

class WorldRugbyMatchSpaceItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val paddingTop: Int
    private val paddingHorizontal: Int

    init {
        val attrs = context.obtainStyledAttributes(
                R.style.RugbyRankerWorldRugbyMatchSpaceItemDecoration,
                R.styleable.WorldRugbyMatchSpaceItemDecoration
        )
        paddingTop = attrs.getDimensionPixelSizeOrThrow(R.styleable.WorldRugbyMatchSpaceItemDecoration_android_paddingTop)
        paddingHorizontal = attrs.getDimensionPixelSizeOrThrow(R.styleable.WorldRugbyMatchSpaceItemDecoration_paddingHorizontal)
        attrs.recycle()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.set(paddingHorizontal, paddingTop, paddingHorizontal, 0)
    }
}
