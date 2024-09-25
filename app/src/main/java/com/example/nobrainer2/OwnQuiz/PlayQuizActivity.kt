package com.example.nobrainer2.OwnQuiz

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.nobrainer2.R
import com.example.nobrainer2.databinding.ActivityPlayQuizBinding
import kotlinx.coroutines.launch

class PlayQuizActivity : AppCompatActivity() {
    private lateinit var quizViewModel: QuizViewModel
    private lateinit var quiz: Quiz
    private var currentQuestionIndex = 0
    private var score = 0
    private lateinit var binding: ActivityPlayQuizBinding
    private lateinit var optionViews: List<TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        optionViews = listOf(
            binding.option1,
            binding.option2,
            binding.option3,
            binding.option4
        )

        val quizDao = QuizDatabase.getInstance(application).quizDao()
        quizViewModel = ViewModelProvider(this, QuizViewModelFactory(quizDao))
            .get(QuizViewModel::class.java)

        val quizId = intent.getIntExtra("QUIZ_ID", -1)
        if (quizId == -1) {
            finish()
            return
        }

        lifecycleScope.launch {
            quiz = quizViewModel.getQuizById(quizId) ?: run {
                finish()
                return@launch
            }
            displayQuestion()
        }

        binding.buttonNext.setOnClickListener {
            if (currentQuestionIndex < quiz.questions.size - 1) {
                currentQuestionIndex++
                displayQuestion()
            } else {
                showFinalScore()
            }
        }
    }

    private fun displayQuestion() {
        val question = quiz.questions[currentQuestionIndex]

        binding.textQuestion.apply {
            text = question.text
            setTextColor(ContextCompat.getColor(this@PlayQuizActivity, R.color.white))
        }

        optionViews.forEachIndexed { index, textView ->
            textView.apply {
                text = question.options[index]
                setBackgroundColor(ContextCompat.getColor(this@PlayQuizActivity, android.R.color.white))
                setOnClickListener {
                    checkAnswer(index, question.correctOptionIndex)
                }
            }
        }

        binding.buttonNext.isEnabled = false
    }

    private fun checkAnswer(selectedIndex: Int, correctIndex: Int) {
        val selectedView = optionViews[selectedIndex]
        val correctView = optionViews[correctIndex]

        if (selectedIndex == correctIndex) {
            selectedView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
            score++
        } else {
            selectedView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
            correctView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
            Toast.makeText(this, "Incorrect. The correct answer was: ${quiz.questions[currentQuestionIndex].options[correctIndex]}", Toast.LENGTH_LONG).show()
        }

        optionViews.forEach { it.isClickable = false }
        binding.buttonNext.isEnabled = true
    }

    private fun showFinalScore() {
        val message = "Quiz completed! Your score: $score out of ${quiz.questions.size}"
        AlertDialog.Builder(this)
            .setTitle("Quiz Result")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ ->
                saveScore()
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun saveScore() {
        quizViewModel.updateQuizScore(quiz.id, score)
    }
}