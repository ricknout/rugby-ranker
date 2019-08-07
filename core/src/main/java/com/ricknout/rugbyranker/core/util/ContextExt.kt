package com.ricknout.rugbyranker.core.util

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.res.getColorOrThrow
import com.ricknout.rugbyranker.core.R

@ColorInt
fun Context.getColorPrimary() = getAttrColor(R.attr.colorPrimary)

@ColorInt
private fun Context.getAttrColor(@AttrRes attrColor: Int) = theme.obtainStyledAttributes(intArrayOf(attrColor)).getColorOrThrow(0)
