package com.ricknout.rugbyranker.core.util

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ricknout.rugbyranker.core.R
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContextExtTest {

    @Test
    fun getColorPrimary() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.setTheme(R.style.Theme_RugbyRanker)
        val colorPrimary = ContextCompat.getColor(context, R.color.color_primary)
        assertEquals(colorPrimary, context.getColorPrimary())
    }
}
