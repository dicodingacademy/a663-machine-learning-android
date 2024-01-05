package com.dicoding.bertqa.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.bertqa.R
import com.dicoding.bertqa.models.Message

import com.dicoding.bertqa.utils.getProfileIcon

class ChatHistoryAdapter : RecyclerView.Adapter<ChatHistoryAdapter.MessageViewHolder>() {

    private val chatHistory = ArrayList<Message>()

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val messageItemView =
            LayoutInflater.from(parent.context).inflate(viewType, parent, false) as ViewGroup
        return MessageViewHolder(messageItemView)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val userProfile = holder.itemView.findViewById<ImageView>(R.id.iv_userProfile)
        val userMessageText = holder.itemView.findViewById<TextView>(R.id.tv_userMessageText)

        val message = chatHistory[position]

        userProfile.setImageDrawable(getProfileIcon(userProfile.context, message.isFromUser))
        userMessageText.text = message.text
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatHistory[position].isFromUser) {
            R.layout.item_message_local
        } else {
            R.layout.item_message_another_user
        }
    }

    override fun getItemCount(): Int {
        return chatHistory.size
    }

    fun addMessage(message: Message) {
        chatHistory.add(message)
        notifyDataSetChanged()
    }

}