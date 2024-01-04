package com.dicoding.bertqa.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.bertqa.BertQAHelper
import com.dicoding.bertqa.DataSetClient
import com.dicoding.bertqa.R
import com.dicoding.bertqa.adapters.ChatHistoryAdapter
import com.dicoding.bertqa.adapters.QuestionSuggestionsAdapter
import com.dicoding.bertqa.databinding.FragmentQABinding
import com.dicoding.bertqa.models.Message
import org.tensorflow.lite.task.text.qa.QaAnswer

class QAFragment : Fragment() {

    private lateinit var binding: FragmentQABinding

    private lateinit var bertQAHelper: BertQAHelper

    private lateinit var chatAdapter: ChatHistoryAdapter

    private val args: QAFragmentArgs by navArgs()
    private var topicContent: String = ""
    private var topicSuggestedQuestions: List<String> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentQABinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            String.format(getString(R.string.fragment_qa_title), args.topicTitle)

        val client = DataSetClient(requireActivity())
        client.loadJsonData()?.let {
            topicContent = it.getContents()[args.topicID]
            topicSuggestedQuestions = it.questions[args.topicID]
        }

        initChatHistoryRecyclerView()
        initQuestionSuggestionsRecyclerView()
        initBertQAModel()

        binding.tietQuestion.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // pass
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Tombol send hanya aktif jikalau terdapat pertanyaan pada TextInputEditText
                val shouldSendButtonActive: Boolean = s.isNullOrEmpty()
                binding.ibSend.isClickable = !shouldSendButtonActive
            }

            override fun afterTextChanged(s: Editable?) {
                // pass
            }

        })

        binding.ibSend.setOnClickListener {
            if (it.isClickable && (binding.tietQuestion.text?.isNotEmpty() == true)) {
                with(binding.tietQuestion) {

                    binding.progressBar.visibility = View.VISIBLE

                    val question = this.text.toString()
                    this.text?.clear()

                    chatAdapter.addMessage(Message(question, true))

                    Handler(Looper.getMainLooper()).post {
                        bertQAHelper.getQuestionAnswer(topicContent, question)
                        binding.progressBar.visibility = View.GONE
                    }

                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Harap masukkan sebuah pertanyaan terlebih dahulu",
                    Toast.LENGTH_SHORT
                ).show()
            }

            val imm = requireContext().getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)

        }

        binding.tvInferenceTime.text = String.format(
            requireContext().getString(R.string.tv_inference_time_label),
            0L
        )

    }

    private fun initChatHistoryRecyclerView() {
        val historyLayoutManager = LinearLayoutManager(context)
        binding.rvChatHistory.layoutManager = historyLayoutManager

        chatAdapter = ChatHistoryAdapter()
        binding.rvChatHistory.adapter = chatAdapter

        chatAdapter.addMessage(Message(topicContent, false))
    }

    private fun initQuestionSuggestionsRecyclerView() {

        if (topicSuggestedQuestions.isNotEmpty()) {
            val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            with(binding.rvQuestionSuggestions) {
                adapter = QuestionSuggestionsAdapter(
                    topicSuggestedQuestions,
                    object : QuestionSuggestionsAdapter.OnOptionClicked {
                        override fun onOptionClicked(optionID: Int) {
                            setQuestion(optionID)
                        }

                    })
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                addItemDecoration(decoration)
            }
        } else {
            binding.tvSuggestion.visibility = View.GONE
            binding.rvQuestionSuggestions.visibility = View.GONE
        }

    }

    private fun initBertQAModel() {

        bertQAHelper = BertQAHelper(requireContext(), object : BertQAHelper.ResultAnswerListener {

            override fun onError(error: String) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }

            override fun onResults(results: List<QaAnswer>?, inferenceTime: Long) {
                results?.first()?.let {
                    chatAdapter.addMessage(Message(it.text, false))
                    binding.rvChatHistory.scrollToPosition(chatAdapter.itemCount - 1)
                }

                binding.tvInferenceTime.text = String.format(
                    requireContext().getString(R.string.tv_inference_time_label),
                    inferenceTime
                )
            }

        })

    }

    private fun setQuestion(position: Int) {
        binding.tietQuestion.setText(
            topicSuggestedQuestions[position]
        )
    }

    override fun onDestroyView() {
        binding.tietQuestion.addTextChangedListener(null)
        super.onDestroyView()
    }

    override fun onDestroy() {
        bertQAHelper.clearBertQuestionAnswerer()
        super.onDestroy()
    }

}