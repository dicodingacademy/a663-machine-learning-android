package com.dicoding.smartreply

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.smartreply.utils.getProfileIcon

class ChatHistoryAdapter : RecyclerView.Adapter<ChatHistoryAdapter.MessageViewHolder>() {

    private val chatHistory = ArrayList<Message>()

    var pretendingAsAnotherUser = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatHistoryAdapter.MessageViewHolder {
        val messageItemView =
            LayoutInflater.from(parent.context).inflate(viewType, parent, false) as ViewGroup
        return MessageViewHolder(messageItemView)
    }

    override fun onBindViewHolder(holder: ChatHistoryAdapter.MessageViewHolder, position: Int) {

        val userProfile = holder.itemView.findViewById<ImageView>(R.id.iv_userProfile)
        val userMessageText = holder.itemView.findViewById<TextView>(R.id.tv_userMessageText)

        val message = chatHistory[position]

        userProfile.setImageDrawable(getProfileIcon(userProfile.context, message.isLocalUser))
        userMessageText.text = message.text

    }

    override fun getItemViewType(position: Int): Int {

        return if (
            chatHistory[position].isLocalUser && !pretendingAsAnotherUser ||
            !chatHistory[position].isLocalUser && pretendingAsAnotherUser
        ) {
            R.layout.item_message_local
        } else {
            R.layout.item_message_another_user
        }

    }

    override fun getItemCount(): Int {
        return chatHistory.size
    }

    fun setChatHistory(messages: List<Message>) {
        chatHistory.clear()
        chatHistory.addAll(messages)
        notifyDataSetChanged()
    }

}