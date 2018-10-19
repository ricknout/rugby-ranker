package com.ricknout.rugbyranker.common.util

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat

@ColorInt
fun Context.getColorPrimary() = getAttrColor(android.R.attr.colorPrimary)

@ColorInt
private fun Context.getAttrColor(@AttrRes attrColor: Int) = TypedValue().let { typedValue ->
    theme.resolveAttribute(attrColor, typedValue, true)
    return@let if (typedValue.resourceId != 0) {
        ContextCompat.getColor(this, typedValue.resourceId)
    } else {
        typedValue.data
    }
}
