package com.example.nobrainer2.OwnQuiz

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class QuizViewModel(private val quizDao: QuizDao) : ViewModel() {
    val allQuizzes: LiveData<List<Quiz>> = quizDao.getAllQuizzes().asLiveData()

    fun insertQuiz(quiz: Quiz) = viewModelScope.launch {
        quizDao.insertQuiz(quiz)
    }

    suspend fun getQuizById(quizId: Int): Quiz? {
        return quizDao.getQuizById(quizId)
    }

    fun updateQuizScore(quizId: Int, score: Int) = viewModelScope.launch {
        quizDao.updateQuizScore(quizId, score)
    }

    fun deleteQuiz(quiz: Quiz) = viewModelScope.launch {
        quizDao.deleteQuiz(quiz)
    }
}

class QuizViewModelFactory(private val quizDao: QuizDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizViewModel(quizDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}