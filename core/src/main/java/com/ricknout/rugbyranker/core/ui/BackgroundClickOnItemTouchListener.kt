package com.ricknout.rugbyranker.core.ui

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView

class BackgroundClickOnItemTouchListener(context: Context, onBackgroundClick: () -> Unit) : RecyclerView.SimpleOnItemTouchListener() {

    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            onBackgroundClick()
            return super.onSingleTapUp(e)
        }
    }

    private val gestureDetector = GestureDetectorCompat(context, gestureListener)

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        if (rv.findChildViewUnder(e.x, e.y) == null) {
            gestureDetector.onTouchEvent(e)
        }
        return false
    }
}
