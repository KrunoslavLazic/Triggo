package com.klazic.triggo.data.quiz

import kotlinx.serialization.Serializable

@Serializable
enum class Difficulty {EASY, MEDIUM, HARD}

@Serializable
data class QuizQuestion(
    val id: String,
    val categoryId: String,
    val difficulty: Difficulty,
    val promptLatex: String,
    val choicesLatex: List<String>,
    val correctIndex: Int
)