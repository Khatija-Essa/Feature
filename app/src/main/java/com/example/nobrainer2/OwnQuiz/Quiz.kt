package com.example.nobrainer2.OwnQuiz

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "quizzes")
data class Quiz(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    @TypeConverters(Converters::class)
    val questions: List<Question>,
    var score: Int = 0
)

data class Question(
    val text: String,
    val options: List<String>,
    val correctOptionIndex: Int
)