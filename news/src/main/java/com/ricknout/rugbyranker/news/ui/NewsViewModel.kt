package com.ricknout.rugbyranker.news.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ricknout.rugbyranker.core.viewmodel.ScrollableViewModel
import com.ricknout.rugbyranker.news.repository.NewsRepository
import com.ricknout.rugbyranker.news.work.NewsWorkManager
import javax.inject.Inject

class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    newsWorkManager: NewsWorkManager
) : ScrollableViewModel() {

    init {
        newsWorkManager.fetchAndStoreLatestWorldRugbyNews()
    }

    val latestWorldRugbyNews = newsRepository.loadLatestWorldRugbyNews()
    val latestWorldRugbyNewsWorkInfos = newsWorkManager.getLatestWorldRugbyNewsWorkInfos()

    private val _refreshingLatestWorldRugbyNews = MutableLiveData<Boolean>().apply { value = false }
    val refreshingLatestWorldRugbyNews: LiveData<Boolean>
        get() = _refreshingLatestWorldRugbyNews

    fun refreshLatestWorldRugbyNews(onComplete: (success: Boolean) -> Unit) {
        _refreshingLatestWorldRugbyNews.value = true
        newsRepository.fetchAndCacheLatestWorldRugbyNewsAsync(viewModelScope) { success ->
            _refreshingLatestWorldRugbyNews.value = false
            onComplete(success)
        }
    }
}
