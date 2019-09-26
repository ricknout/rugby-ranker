package com.ricknout.rugbyranker.core.util

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

    fun isDayCurrentDay(millis: Long): Boolean {
        val day = Calendar.getInstance().apply { timeInMillis = millis }.get(Calendar.DAY_OF_YEAR)
        val year = Calendar.getInstance().apply { timeInMillis = millis }.get(Calendar.YEAR)
        val currentMillis = System.currentTimeMillis()
        val currentDay = Calendar.getInstance().apply { timeInMillis = currentMillis }.get(Calendar.DAY_OF_YEAR)
        val currentYear = Calendar.getInstance().apply { timeInMillis = currentMillis }.get(Calendar.YEAR)
        return day == currentDay && year == currentYear
    }

    const val DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd"
    const val DATE_FORMAT_D_MMM_YYYY = "d MMM, yyyy"
    const val DATE_FORMAT_D_MMM = "d MMM"
    const val DATE_FORMAT_E_YYYY = "E, yyyy"
    const val DATE_FORMAT_HH_MM = "HH:mm"

    const val DAY_MILLIS = 1000L * 60L * 60L * 24L
    const val HOUR_MILLIS = 1000L * 60L * 60L
    const val MINUTE_MILLIS = 1000L * 60L
    const val MINUTE_SECS = 60
}
