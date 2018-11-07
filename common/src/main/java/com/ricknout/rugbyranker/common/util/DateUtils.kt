package com.ricknout.rugbyranker.common.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.SimpleTimeZone

object DateUtils {

    fun getCurrentDate(format: String) = getDate(format, System.currentTimeMillis())

    fun getDate(format: String, millis: Long): String {
        val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
        val time = Calendar.getInstance().apply { timeInMillis = millis }.time
        return simpleDateFormat.format(time)
    }

    fun getDate(format: String, millis: Long, gmtOffset: Int): String {
        val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
        simpleDateFormat.timeZone = SimpleTimeZone(gmtOffset, "GMT")
        val time = Calendar.getInstance().apply { timeInMillis = millis }.time
        return simpleDateFormat.format(time)
    }

    fun getYearBeforeDate(format: String, millis: Long): String {
        val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
        val time = Calendar.getInstance().apply {
            timeInMillis = millis
            add(Calendar.YEAR, -1)
        }.time
        return simpleDateFormat.format(time)
    }

    fun getYearAfterDate(format: String, millis: Long): String {
        val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
        val time = Calendar.getInstance().apply {
            timeInMillis = millis
            add(Calendar.YEAR, 1)
        }.time
        return simpleDateFormat.format(time)
    }

    const val DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd"
    const val DATE_FORMAT_HH_MM = "HH:mm"
}
