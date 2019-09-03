package com.ricknout.rugbyranker.news.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ricknout.rugbyranker.core.viewmodel.ScrollableViewModel
import com.ricknout.rugbyranker.news.repository.ArticlesRepository
import com.ricknout.rugbyranker.news.vo.ArticleType
import com.ricknout.rugbyranker.news.work.ArticlesWorkManager
import javax.inject.Inject

open class ArticlesViewModel @Inject constructor(
    private val articleType: ArticleType,
    private val articlesRepository: ArticlesRepository,
    articlesWorkManager: ArticlesWorkManager
) : ScrollableViewModel() {

    init {
        articlesWorkManager.fetchAndStoreLatestWorldRugbyArticles(articleType)
    }

    val latestWorldRugbyArticles = articlesRepository.loadLatestWorldRugbyArticles(articleType)
    val latestWorldRugbyArticlesWorkInfos = articlesWorkManager.getLatestWorldRugbyArticlesWorkInfos(articleType)

    private val _refreshingLatestWorldRugbyArticles = MutableLiveData<Boolean>().apply { value = false }
    val refreshingLatestWorldRugbyArticles: LiveData<Boolean>
        get() = _refreshingLatestWorldRugbyArticles

    fun refreshLatestWorldRugbyArticles(onComplete: (success: Boolean) -> Unit) {
        _refreshingLatestWorldRugbyArticles.value = true
        articlesRepository.fetchAndCacheLatestWorldRugbyArticlesAsync(articleType, viewModelScope) { success ->
            _refreshingLatestWorldRugbyArticles.value = false
            onComplete(success)
        }
    }
}
