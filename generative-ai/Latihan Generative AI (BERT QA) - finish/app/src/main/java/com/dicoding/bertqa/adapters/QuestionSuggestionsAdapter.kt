package com.dicoding.bertqa.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.bertqa.databinding.ItemQuestionSuggestionBinding

class QuestionSuggestionsAdapter(
    private val suggestedQuestions: List<String>,
    private val onOptionClickedCallback: OnOptionClicked
) : RecyclerView.Adapter<QuestionSuggestionsAdapter.ViewHolder>() {

    interface OnOptionClicked {
        fun onOptionClicked(optionID: Int)
    }

    inner class ViewHolder(val binding: ItemQuestionSuggestionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemQuestionSuggestionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            onOptionClickedCallback.onOptionClicked(position)
        }
        holder.binding.tvQuestionSuggestion.text = suggestedQuestions[position]
    }

    override fun getItemCount(): Int {
        return suggestedQuestions.size
    }

}