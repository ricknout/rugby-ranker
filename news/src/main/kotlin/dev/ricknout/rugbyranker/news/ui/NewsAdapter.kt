package dev.ricknout.rugbyranker.news.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import dev.ricknout.rugbyranker.news.databinding.ListItemNewsBinding
import dev.ricknout.rugbyranker.news.model.News

class NewsAdapter(
    private val onClick: (news: News) -> Unit,
) : PagingDataAdapter<News, NewsViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ) = NewsViewHolder(
        ListItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    )

    override fun onBindViewHolder(
        holder: NewsViewHolder,
        position: Int,
    ) {
        val news = getItem(position) ?: return
        holder.bind(news, onClick)
    }

    companion object {
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<News>() {
                override fun areItemsTheSame(
                    oldItem: News,
                    newItem: News,
                ) = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: News,
                    newItem: News,
                ) = oldItem == newItem
            }
    }
}
