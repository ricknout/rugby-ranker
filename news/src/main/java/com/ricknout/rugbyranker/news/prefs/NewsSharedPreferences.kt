package com.ricknout.rugbyranker.news.prefs

import android.content.SharedPreferences
import androidx.core.content.edit

class NewsSharedPreferences(private val sharedPreferences: SharedPreferences) {

    fun setInitialNewsFetched(fetched: Boolean) = sharedPreferences.edit { putBoolean(KEY_INITIAL_NEWS_FETCHED, fetched) }

    fun isInitialNewsFetched() = sharedPreferences.getBoolean(KEY_INITIAL_NEWS_FETCHED, false)

    companion object {
        private const val KEY_INITIAL_NEWS_FETCHED = "initial_news_fetched"
    }
}
