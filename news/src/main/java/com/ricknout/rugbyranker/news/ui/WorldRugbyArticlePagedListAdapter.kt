package com.ricknout.rugbyranker.news.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ricknout.rugbyranker.core.glide.GlideApp
import com.ricknout.rugbyranker.core.util.DateUtils
import com.ricknout.rugbyranker.news.R
import com.ricknout.rugbyranker.news.vo.ArticleType
import com.ricknout.rugbyranker.news.vo.WorldRugbyArticle
import kotlinx.android.synthetic.main.list_item_world_rugby_article.view.*

class WorldRugbyArticlePagedListAdapter(
    private val onClick: (worldRugbyArticle: WorldRugbyArticle) -> Unit
) : PagedListAdapter<WorldRugbyArticle, WorldRugbyArticleViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        WorldRugbyArticleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_world_rugby_article, parent, false))

    override fun onBindViewHolder(holder: WorldRugbyArticleViewHolder, position: Int) {
        val worldRugbyArticle = getItem(position) ?: return
        holder.bind(worldRugbyArticle, onClick)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WorldRugbyArticle>() {
            override fun areItemsTheSame(oldItem: WorldRugbyArticle, newItem: WorldRugbyArticle) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: WorldRugbyArticle, newItem: WorldRugbyArticle) = oldItem == newItem
        }
    }
}

class WorldRugbyArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(worldRugbyArticle: WorldRugbyArticle, onClick: (worldRugbyArticle: WorldRugbyArticle) -> Unit) {
        (itemView.imageCardView.layoutParams as ConstraintLayout.LayoutParams).apply {
            when (worldRugbyArticle.type) {
                ArticleType.TEXT -> {
                    width = itemView.resources.getDimensionPixelSize(R.dimen.width_article_image)
                    dimensionRatio = "1:1"
                }
                ArticleType.VIDEO -> {
                    width = itemView.resources.getDimensionPixelSize(R.dimen.width_article_video)
                    dimensionRatio = "16:9"
                }
            }
        }
        itemView.placeHolderImageView.setImageResource(when (worldRugbyArticle.type) {
            ArticleType.TEXT -> R.drawable.ic_image_black_24dp
            ArticleType.VIDEO -> R.drawable.ic_video_black_24dp
        })
        GlideApp.with(itemView)
                .load(worldRugbyArticle.imageUrl)
                .centerCrop()
                .into(itemView.imageView)
        itemView.subtitleTextView.text = worldRugbyArticle.subtitle
        itemView.subtitleTextView.isVisible = worldRugbyArticle.subtitle != null
        val date = DateUtils.getDate(DateUtils.DATE_FORMAT_D_MMM_YYYY, worldRugbyArticle.timeMillis)
        itemView.dateTextView.text = date
        itemView.titleTextView.text = worldRugbyArticle.title
        itemView.summaryTextView.text = worldRugbyArticle.summary
        itemView.setOnClickListener {
            onClick.invoke(worldRugbyArticle)
        }
    }
}
