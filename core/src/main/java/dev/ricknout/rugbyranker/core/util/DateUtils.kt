package dev.ricknout.rugbyranker.core.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateUtils {

    fun getCurrentDate(format: String) = getDate(format, System.currentTimeMillis())

    fun getDayAfterCurrentDate(format: String) = getDayAfterDate(format, System.currentTimeMillis())

    fun getDate(format: String, millis: Long): String {
        val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
        val time = Calendar.getInstance().apply { timeInMillis = millis }.time
        return simpleDateFormat.format(time)
    }

    fun getDayAfterDate(format: String, millis: Long): String {
        val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
        val time = Calendar.getInstance().apply {
            timeInMillis = millis
            add(Calendar.DATE, 1)
        }.time
        return simpleDateFormat.format(time)
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
    const val DATE_FORMAT_E_D_MMM_YYYY = "E, d MMM, yyyy"
    const val DATE_FORMAT_HH_MM = "HH:mm"

    const val MINUTE_MILLIS = 1000L * 60L
    const val MINUTE_SECS = 60
}
