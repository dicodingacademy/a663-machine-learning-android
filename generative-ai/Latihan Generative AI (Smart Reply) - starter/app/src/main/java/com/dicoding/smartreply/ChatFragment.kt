package com.dicoding.smartreply

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.smartreply.databinding.FragmentChatBinding

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding

    private lateinit var chatViewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).setSupportActionBar(binding.topAppBar as Toolbar)

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up recycler view untuk chat history
        val historyLayoutManager = LinearLayoutManager(context)
        binding.rvChatHistory.layoutManager = historyLayoutManager

        val chatAdapter = ChatHistoryAdapter()
        binding.rvChatHistory.adapter = chatAdapter

        // Set up recycler view untuk opsi smart reply
        val optionsLayoutManager = LinearLayoutManager(context)
        optionsLayoutManager.orientation = RecyclerView.HORIZONTAL
        binding.rvSmartReplyOptions.layoutManager = optionsLayoutManager

        val replyOptionsAdapter =
            ReplyOptionsAdapter(object : ReplyOptionsAdapter.OnItemClickCallback {
                override fun onOptionClicked(optionText: String) {
                    binding.tietInputTextEditText.setText(optionText)
                }

            })
        binding.rvSmartReplyOptions.adapter = replyOptionsAdapter

        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

        chatViewModel.chatHistory.observe(viewLifecycleOwner) { messages ->
            if (chatViewModel.pretendingAsAnotherUser.value == null) {
                chatAdapter.setChatHistory(messages)
            } else {
                chatAdapter.setChatHistory(messages, chatViewModel.pretendingAsAnotherUser.value!!)
            }

            if (chatAdapter.itemCount > 0) {
                binding.rvChatHistory.smoothScrollToPosition(chatAdapter.itemCount - 1)
            }
        }

        chatViewModel.pretendingAsAnotherUser.observe(viewLifecycleOwner) { isPretendingAsAnotherUser ->
            if (isPretendingAsAnotherUser) {
                binding.tvCurrentUser.text = requireContext().getText(R.string.chatting_as_evans)
                binding.tvCurrentUser.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red
                    )
                )
            } else {
                binding.tvCurrentUser.text = requireContext().getText(R.string.chatting_as_kai)
                binding.tvCurrentUser.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
            }
        }

        chatViewModel.errorMessage.observe(viewLifecycleOwner) {
            if (it != null)
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        binding.rvChatHistory.setOnTouchListener { v, _ ->
            val imm = requireContext().getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
            false
        }

        binding.rvSmartReplyOptions.setOnClickListener { v ->
            val imm = requireContext().getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }

        binding.btnSwitchUser.setOnClickListener {
            chatAdapter.pretendingAsAnotherUser = !chatAdapter.pretendingAsAnotherUser
            chatViewModel.switchUser()
        }

        binding.btnSend.setOnClickListener {
            val input = binding.tietInputTextEditText.text.toString()
            if (input.isNotEmpty()) {
                val imm = requireContext().getSystemService(
                    Context.INPUT_METHOD_SERVICE
                ) as InputMethodManager
                imm.hideSoftInputFromWindow(it.windowToken, 0)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.chat_menu_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.generateBasicChatHistory -> {
                generateBasicChatHistory()
                true
            }

            R.id.generateSensitiveChatHistory -> {
                generateSensitiveChatHistory()
                true
            }

            R.id.clearChatHistory -> {
                chatViewModel.setMessages(ArrayList())
                true
            }

            else -> false
        }
    }

    private fun generateBasicChatHistory() {

    }

    private fun generateSensitiveChatHistory() {

    }

}
