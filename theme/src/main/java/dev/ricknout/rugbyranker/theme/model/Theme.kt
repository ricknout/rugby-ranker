package dev.ricknout.rugbyranker.theme.model

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import dev.ricknout.rugbyranker.theme.R

enum class Theme(val mode: Int, @StringRes val titleResId: Int) {
    LIGHT(AppCompatDelegate.MODE_NIGHT_NO, R.string.light),
    DARK(AppCompatDelegate.MODE_NIGHT_YES, R.string.dark),
    SET_BY_BATTERY_SAVER(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY, R.string.set_by_battery_saver),
    SYSTEM_DEFAULT(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, R.string.system_default)
}
