package com.ricknout.rugbyranker.news.prefs

import android.content.SharedPreferences
import androidx.core.content.edit
import com.ricknout.rugbyranker.news.vo.ArticleType

class ArticlesSharedPreferences(private val sharedPreferences: SharedPreferences) {

    fun setInitialArticlesFetched(articleType: ArticleType, fetched: Boolean) = sharedPreferences.edit {
        putBoolean(getInitialArticlesFetchedKey(articleType), fetched)
    }

    fun isInitialArticlesFetched(articleType: ArticleType) = sharedPreferences.getBoolean(
        getInitialArticlesFetchedKey(articleType), false
    )

    companion object {
        private const val KEY_INITIAL_ARTICLES_FETCHED = "initial_articles_fetched"
        private fun getInitialArticlesFetchedKey(articleType: ArticleType): String {
            return "${KEY_INITIAL_ARTICLES_FETCHED}_$articleType"
        }
    }
}
