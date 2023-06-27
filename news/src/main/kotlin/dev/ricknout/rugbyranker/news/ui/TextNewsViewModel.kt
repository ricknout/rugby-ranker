package dev.ricknout.rugbyranker.news.ui

import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ricknout.rugbyranker.news.data.NewsRepository
import dev.ricknout.rugbyranker.news.model.Type
import javax.inject.Inject

@HiltViewModel
class TextNewsViewModel @Inject constructor(
    repository: NewsRepository,
) : NewsViewModel(Type.TEXT, repository)
