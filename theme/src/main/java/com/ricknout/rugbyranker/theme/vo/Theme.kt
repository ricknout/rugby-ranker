package com.ricknout.rugbyranker.theme.vo

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import com.ricknout.rugbyranker.theme.R

enum class Theme(val mode: Int, @StringRes val titleResId: Int) {
    LIGHT(AppCompatDelegate.MODE_NIGHT_NO, R.string.theme_light),
    DARK(AppCompatDelegate.MODE_NIGHT_YES, R.string.theme_dark),
    SET_BY_BATTERY_SAVER(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY, R.string.theme_set_by_battery_saver),
    SYSTEM_DEFAULT(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, R.string.theme_system_default)
}
