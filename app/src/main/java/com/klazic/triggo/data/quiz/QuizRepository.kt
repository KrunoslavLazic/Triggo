package com.klazic.triggo.data.quiz

import android.content.Context
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlin.random.Random

class QuizRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }
    private val questionsCache = mutableMapOf<String, List<QuizQuestion>>()

    private fun questions(categoryId: String, difficulty: Difficulty? = null): List<QuizQuestion> {
        val all = questionsCache.getOrPut(categoryId) {
            val text = context.assets.open("questions/$categoryId.json").bufferedReader()
                .use { it.readText() }
            json.decodeFromString(ListSerializer(QuizQuestion.serializer()), text)
        }
        return difficulty?.let { d -> all.filter { it.difficulty == d } } ?: all
    }

    fun countsByDifficulty(categoryId: String): Map<Difficulty, Int> =
        Difficulty.entries.associateWith { d -> questions(categoryId, d).size }

    fun createSession(
        categoryId: String,
        difficulty: Difficulty,
        size: Int = 10,
        random: Random = Random.Default
    ): List<QuizQuestion> {
        val pool = questions(categoryId, difficulty).shuffled(random)
        return pool.take(size).map { it.shuffledChoices(random) }
    }
}

private fun QuizQuestion.shuffledChoices(random: Random): QuizQuestion {
    val order = choicesLatex.indices.shuffled(random)
    val newChoices = order.map { choicesLatex[it] }
    val newCorrect = order.indexOf(correctIndex)
    return copy(choicesLatex = newChoices, correctIndex = newCorrect)
}