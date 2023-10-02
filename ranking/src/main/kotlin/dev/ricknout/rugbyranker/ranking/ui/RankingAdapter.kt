package dev.ricknout.rugbyranker.ranking.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import dev.ricknout.rugbyranker.core.model.Ranking
import dev.ricknout.rugbyranker.ranking.databinding.ListItemRankingBinding

class RankingAdapter : ListAdapter<Ranking, RankingViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ) = RankingViewHolder(
        ListItemRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    )

    override fun onBindViewHolder(
        holder: RankingViewHolder,
        position: Int,
    ) {
        val ranking = getItem(position)
        holder.bind(ranking)
    }

    companion object {
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Ranking>() {
                override fun areItemsTheSame(
                    oldItem: Ranking,
                    newItem: Ranking,
                ) = oldItem.teamId == newItem.teamId

                override fun areContentsTheSame(
                    oldItem: Ranking,
                    newItem: Ranking,
                ) = oldItem == newItem
            }
    }
}
