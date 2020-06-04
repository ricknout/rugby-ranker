package dev.ricknout.rugbyranker.news.ui

import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.google.android.material.color.MaterialColors
import dev.ricknout.rugbyranker.core.util.DateUtils
import dev.ricknout.rugbyranker.news.R
import dev.ricknout.rugbyranker.news.databinding.ListItemNewsBinding
import dev.ricknout.rugbyranker.news.model.News
import kotlin.math.floor

class NewsViewHolder(private val binding: ListItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(news: News, onClick: (news: News) -> Unit) {
        binding.apply {
            image.load(news.imageUrl) { crossfade(true) }
            subtitle.text = news.subtitle
            val isCurrentDay = DateUtils.isDayCurrentDay(news.timeMillis)
            label.text = if (isCurrentDay) {
                root.context.getString(R.string.today)
            } else {
                DateUtils.getDate(DateUtils.DATE_FORMAT_D_MMM_YYYY, news.timeMillis)
            }
            title.text = news.title
            body.text = news.summary
            root.setOnClickListener { onClick(news) }
            val imagesBackgroundColor = AppCompatResources.getColorStateList(
                itemView.context, R.color.on_surface_16
            ).defaultColor
            images.setBackgroundColor(imagesBackgroundColor)
            val spanCount = itemView.context.resources.getInteger(R.integer.span_count_grid)
            val row = floor(absoluteAdapterPosition / spanCount.toFloat()).toInt()
            val backgroundColor = when {
                (spanCount % 2 != 0 || row % 2 == 0) && absoluteAdapterPosition % 2 == 0 -> {
                    AppCompatResources.getColorStateList(itemView.context, R.color.on_surface_5).defaultColor
                }
                (spanCount % 2 == 0 && row % 2 != 0) && absoluteAdapterPosition % 2 != 0 -> {
                    AppCompatResources.getColorStateList(itemView.context, R.color.on_surface_5).defaultColor
                }
                else -> MaterialColors.getColor(root, R.attr.colorSurface)
            }
            root.setBackgroundColor(backgroundColor)
        }
    }
}
