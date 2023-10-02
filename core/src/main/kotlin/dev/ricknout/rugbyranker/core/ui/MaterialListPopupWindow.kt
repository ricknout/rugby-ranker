package dev.ricknout.rugbyranker.core.ui

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.withStyledAttributes
import com.google.android.material.color.MaterialColors
import com.google.android.material.shape.MaterialShapeDrawable
import dev.ricknout.rugbyranker.core.R

class MaterialListPopupWindow
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = R.attr.listPopupWindowStyle,
        @StyleRes defStyleRes: Int = R.style.Widget_MaterialComponents_ListPopupWindow,
    ) : ListPopupWindow(context, attrs, defStyleAttr, defStyleRes) {
        init {
            val materialShapeDrawable =
                MaterialShapeDrawable(context, attrs, defStyleAttr, defStyleRes).apply {
                    initializeElevationOverlay(context)
                    val surfaceColor = MaterialColors.getColor(context, R.attr.colorSurface, javaClass.canonicalName)
                    fillColor = ColorStateList.valueOf(surfaceColor)
                    context.withStyledAttributes(attrs, R.styleable.MaterialListPopupWindow, defStyleAttr, defStyleRes) {
                        elevation = getDimension(R.styleable.MaterialListPopupWindow_android_popupElevation, 0f)
                    }
                }
            setBackgroundDrawable(materialShapeDrawable)
        }
    }
