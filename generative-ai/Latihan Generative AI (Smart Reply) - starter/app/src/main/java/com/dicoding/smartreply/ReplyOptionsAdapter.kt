package com.dicoding.smartreply

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.smartreply.databinding.ItemOptionsSmartreplyBinding

class ReplyOptionsAdapter(
    private val onItemClickCallback: OnItemClickCallback
) : RecyclerView.Adapter<ReplyOptionsAdapter.ViewHolder>() {

    interface OnItemClickCallback {
        fun onOptionClicked(optionText: String)
    }

    inner class ViewHolder(val binding: ItemOptionsSmartreplyBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemOptionsSmartreplyBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReplyOptionsAdapter.ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 0
    }

}