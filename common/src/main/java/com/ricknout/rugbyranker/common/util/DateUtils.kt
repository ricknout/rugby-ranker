package com.ricknout.rugbyranker.common.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateUtils {

    fun getCurrentDate(format: String) = getDate(format, System.currentTimeMillis())

    fun getDate(format: String, millis: Long): String {
        val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
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

    fun getDayMonthYear(millis: Long): Triple<Int, Int, Int> {
        return Calendar.getInstance().apply { timeInMillis = millis }.let { calendar ->
            Triple(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR))
        }
    }

    const val DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd"
    const val DATE_FORMAT_D_MMM_YYYY = "d MMM, yyyy"
    const val DATE_FORMAT_D_MMM = "d MMM"
    const val DATE_FORMAT_YYYY = "yyyy"
    const val DATE_FORMAT_HH_MM = "HH:mm"
}
