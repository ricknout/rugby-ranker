package com.ricknout.rugbyranker.common.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun getCurrentDate(format: String): String {
        val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
        val time = Calendar.getInstance().time
        return simpleDateFormat.format(time)
    }

    fun getDate(format: String, millis: Long, gmtOffset: Int): String {
        val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
        simpleDateFormat.timeZone = SimpleTimeZone(gmtOffset, "GMT")
        val time = Calendar.getInstance().apply { timeInMillis = millis }.time
        return simpleDateFormat.format(time)
    }

    const val DATE_FORMAT = "yyyy-MM-dd"
}
