package dev.ricknout.rugbyranker.ranking.ui

import androidx.recyclerview.widget.RecyclerView
import dev.ricknout.rugbyranker.ranking.databinding.ListItemLabelBinding

class LabelViewHolder(private val binding: ListItemLabelBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(label: String) {
        binding.label.text = label
    }
}
