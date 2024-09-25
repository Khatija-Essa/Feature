package com.example.nobrainer2.OwnQuiz

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Dao
interface QuizDao {
    @Query("SELECT * FROM quizzes")
    fun getAllQuizzes(): Flow<List<Quiz>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuiz(quiz: Quiz)

    @Query("SELECT * FROM quizzes WHERE id = :quizId")
    suspend fun getQuizById(quizId: Int): Quiz?

    @Query("UPDATE quizzes SET score = :score WHERE id = :quizId")
    suspend fun updateQuizScore(quizId: Int, score: Int)

    @Delete
    suspend fun deleteQuiz(quiz: Quiz)
}

@Database(entities = [Quiz::class], version = 2) // Increment version from 1 to 2
@TypeConverters(Converters::class)
abstract class QuizDatabase : RoomDatabase() {
    abstract fun quizDao(): QuizDao

    companion object {
        private var instance: QuizDatabase? = null

        fun getInstance(context: android.content.Context): QuizDatabase {
            return instance ?: synchronized(this) {
                instance ?: androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    QuizDatabase::class.java,
                    "quiz_database"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { instance = it }
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromString(value: String): List<Question> {
        return value.split("|").map { questionString ->
            val parts = questionString.split("~")
            Question(
                text = parts[0],
                options = parts[1].split(","),
                correctOptionIndex = parts[2].toInt()
            )
        }
    }

    @TypeConverter
    fun toString(questions: List<Question>): String {
        return questions.joinToString("|") { question ->
            "${question.text}~${question.options.joinToString(",")}~${question.correctOptionIndex}"
        }
    }
}