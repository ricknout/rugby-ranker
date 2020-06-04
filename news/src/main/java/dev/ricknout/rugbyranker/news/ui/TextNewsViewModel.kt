package dev.ricknout.rugbyranker.news.ui

import androidx.hilt.lifecycle.ViewModelInject
import dev.ricknout.rugbyranker.news.data.NewsRepository
import dev.ricknout.rugbyranker.news.model.Type

class TextNewsViewModel @ViewModelInject constructor(
    repository: NewsRepository
) : NewsViewModel(Type.TEXT, repository)
