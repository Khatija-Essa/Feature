package com.example.nobrainer2.OwnQuiz

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nobrainer2.BaseActivity
import com.example.nobrainer2.databinding.ActivityMyQuizBinding

class MyQuiz : BaseActivity() {
    private lateinit var quizViewModel: QuizViewModel
    private lateinit var binding: ActivityMyQuizBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = QuizListAdapter(
            onItemClicked = { quiz ->
                val intent = Intent(this, PlayQuizActivity::class.java)
                intent.putExtra("QUIZ_ID", quiz.id)
                startActivity(intent)
            },
            onDeleteClicked = { quiz ->
                showDeleteConfirmationDialog(quiz)
            }
        )

        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(this)

        val quizDao = QuizDatabase.getInstance(application).quizDao()
        quizViewModel = ViewModelProvider(this, QuizViewModelFactory(quizDao))
            .get(QuizViewModel::class.java)

        quizViewModel.allQuizzes.observe(this) { quizzes ->
            quizzes?.let { adapter.submitList(it) }
        }
    }

    private fun showDeleteConfirmationDialog(quiz: Quiz) {
        AlertDialog.Builder(this)
            .setTitle("Delete Quiz")
            .setMessage("Are you sure you want to delete this quiz?")
            .setPositiveButton("Yes") { _, _ ->
                quizViewModel.deleteQuiz(quiz)
            }
            .setNegativeButton("No", null)
            .show()
    }
}
