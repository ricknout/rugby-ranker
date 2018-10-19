package com.ricknout.rugbyranker.common.util

import androidx.core.content.ContextCompat
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.ricknout.rugbyranker.common.R
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertEquals

@RunWith(AndroidJUnit4::class)
class ContextExtTest {

    @Test
    fun getColorPrimary() {
        val context = InstrumentationRegistry.getTargetContext()
        context.setTheme(R.style.RugbyRankerTheme)
        val colorPrimary = ContextCompat.getColor(context, R.color.color_primary)
        assertEquals(colorPrimary, context.getColorPrimary())
    }
}
