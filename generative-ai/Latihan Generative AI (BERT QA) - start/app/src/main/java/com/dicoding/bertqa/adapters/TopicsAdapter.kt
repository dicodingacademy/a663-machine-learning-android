package com.dicoding.bertqa.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.bertqa.databinding.ItemTopicBinding

class TopicsAdapter(
    private val topicsTitle: List<String>,
    private val onItemSelectedCallback: OnItemSelected
) : RecyclerView.Adapter<TopicsAdapter.ViewHolder>() {

    interface OnItemSelected {
        fun onItemClicked(itemID: Int, itemTitle: String)
    }

    inner class ViewHolder(val binding: ItemTopicBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTopicBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvTopicTitle.text = topicsTitle[position]
        holder.itemView.setOnClickListener {
            onItemSelectedCallback.onItemClicked(position, topicsTitle[position])
        }

    }

    override fun getItemCount(): Int {
        return topicsTitle.size
    }


}