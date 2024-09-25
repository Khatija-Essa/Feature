package com.example.nobrainer2.OwnQuiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nobrainer2.R

class QuizListAdapter(private val onItemClicked: (Quiz) -> Unit,
                      private val onDeleteClicked: (Quiz) -> Unit
) : ListAdapter<Quiz, QuizListAdapter.QuizViewHolder>(QuizDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quiz, parent, false)
        return QuizViewHolder(view, onItemClicked, onDeleteClicked)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class QuizViewHolder(itemView: View, private val onItemClicked: (Quiz) -> Unit, private val onDeleteClicked: (Quiz) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val quizTitleTextView: TextView = itemView.findViewById(R.id.text_quiz_title)
        private val questionCountTextView: TextView = itemView.findViewById(R.id.text_question_count)
        private val scoreTextView: TextView = itemView.findViewById(R.id.text_score)

        private var currentQuiz: Quiz? = null
        private val deleteButton: Button = itemView.findViewById(R.id.button_delete_quiz)

        init {
            itemView.setOnClickListener {
                currentQuiz?.let {
                    onItemClicked(it)
                }
            }
            deleteButton.setOnClickListener {
                currentQuiz?.let {
                    onDeleteClicked(it)
                }
            }
        }

        fun bind(quiz: Quiz) {
            currentQuiz = quiz
            quizTitleTextView.text = quiz.title
            questionCountTextView.text = "${quiz.questions.size} question(s)"
            scoreTextView.text = "Score: ${quiz.score}/${quiz.questions.size}"
        }
    }



    companion object {
        private val QuizDiffCallback = object : DiffUtil.ItemCallback<Quiz>() {
            override fun areItemsTheSame(oldItem: Quiz, newItem: Quiz): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Quiz, newItem: Quiz): Boolean {
                return oldItem == newItem
            }
        }
    }


}