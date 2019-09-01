package com.ricknout.rugbyranker.news.ui

import com.ricknout.rugbyranker.news.repository.ArticlesRepository
import com.ricknout.rugbyranker.news.vo.ArticleType
import com.ricknout.rugbyranker.news.work.ArticlesWorkManager
import javax.inject.Inject

class VideoArticlesViewModel @Inject constructor(
    articlesRepository: ArticlesRepository,
    articlesWorkManager: ArticlesWorkManager
) : ArticlesViewModel(ArticleType.VIDEO, articlesRepository, articlesWorkManager)
