package com.example.nobrainer2.OwnQuiz

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.nobrainer2.BaseActivity
import com.example.nobrainer2.databinding.ActivityOwnQuizBinding

class OwnQuizActivity : BaseActivity() {
    private lateinit var quizViewModel: QuizViewModel
    private lateinit var binding: ActivityOwnQuizBinding
    private val questions = mutableListOf<Question>()
    private var currentQuestionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityOwnQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val quizDao = QuizDatabase.getInstance(application).quizDao()
        quizViewModel = ViewModelProvider(this, QuizViewModelFactory(quizDao))
            .get(QuizViewModel::class.java)


        binding.buttonNextQuestion.setOnClickListener {
            saveCurrentQuestion()
            if (currentQuestionIndex < 9) {
                currentQuestionIndex++
                clearInputFields()
            } else {
                Toast.makeText(this, "Maximum 10 questions allowed", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonSave.setOnClickListener {
            saveCurrentQuestion()
            val title = binding.editQuizTitle.text.toString()
            if (title.isNotEmpty() && questions.isNotEmpty()) {
                val quiz = Quiz(title = title, questions = questions)
                quizViewModel.insertQuiz(quiz)
                finish()
            } else {
                Toast.makeText(this, "Please add a title and at least one question", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveCurrentQuestion() {
        val questionText = binding.editQuestion.text.toString()
        val options = listOf(
            binding.editOption1.text.toString(),
            binding.editOption2.text.toString(),
            binding.editOption3.text.toString(),
            binding.editOption4.text.toString()
        )
        val correctAnswer = binding.editCorrectAnswer.text.toString().toIntOrNull() ?: 1

        if (questionText.isNotEmpty() && options.all { it.isNotEmpty() }) {
            val question = Question(questionText, options, correctAnswer - 1)
            if (currentQuestionIndex < questions.size) {
                questions[currentQuestionIndex] = question
            } else {
                questions.add(question)
            }
        }
    }

    private fun clearInputFields() {
        binding.editQuestion.text?.clear()
        binding.editOption1.text?.clear()
        binding.editOption2.text?.clear()
        binding.editOption3.text?.clear()
        binding.editOption4.text?.clear()
        binding.editCorrectAnswer.text?.clear()
    }
}
