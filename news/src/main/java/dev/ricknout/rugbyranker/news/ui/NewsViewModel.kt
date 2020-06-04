package dev.ricknout.rugbyranker.news.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dev.ricknout.rugbyranker.news.data.NewsRepository
import dev.ricknout.rugbyranker.news.model.Type

open class NewsViewModel(
    type: Type,
    repository: NewsRepository
) : ViewModel() {

    val news = repository.loadNews(type).cachedIn(viewModelScope).asLiveData()
}
