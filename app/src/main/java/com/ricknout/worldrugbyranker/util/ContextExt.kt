package com.ricknout.worldrugbyranker.util

import android.content.Context
import android.util.TypedValue
import androidx.annotation.ColorInt

@ColorInt
fun Context.getColorPrimary(): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
    return typedValue.data
}
