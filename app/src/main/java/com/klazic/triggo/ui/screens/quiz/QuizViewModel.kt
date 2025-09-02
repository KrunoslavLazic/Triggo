package com.klazic.triggo.ui.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.klazic.triggo.data.quiz.Difficulty
import com.klazic.triggo.data.quiz.QuizQuestion
import com.klazic.triggo.data.quiz.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class QuizUiState(
    val loading: Boolean = true,
    val questions: List<QuizQuestion> = emptyList(),
    val index: Int = 0,
    val selected: Int? = null,
    val correctCount: Int = 0,
    val finished: Boolean = false,
    val correctIds: Set<String> = emptySet()
){
    val total get() = questions.size
    val current get() = questions.getOrNull(index)
}

class QuizViewModel(
    private val repo: QuizRepository,
    private val categoryId: String,
    private val difficulty: Difficulty,
    private val sessionSize: Int = 10,
    private val random: Random = Random.Default
): ViewModel(){

    private val _state = MutableStateFlow(QuizUiState())
    val state: StateFlow<QuizUiState> = _state

    init{
        viewModelScope.launch {
            val qs = repo.createSession(categoryId, difficulty, sessionSize, random)
            _state.value = QuizUiState(loading = false, questions = qs)
        }
    }

    fun select(i: Int){
        _state.value = _state.value.copy(selected = i)
    }

    fun submitOrNext(){
        val s = _state.value
        val q = s.current ?: return
        val wasCorrect = (s.selected == q.correctIndex)
        val newCorrect = if (wasCorrect) s.correctCount + 1 else s.correctCount
        val newIds = if (wasCorrect) s.correctIds + q.id else s.correctIds

        if (s.index >= s.total - 1){
            _state.value = s.copy(correctCount = newCorrect, correctIds = newIds, finished = true)
        } else{
            _state.value = s.copy(
                correctCount = newCorrect,
                correctIds = newIds,
                index = s.index + 1,
                selected = null
            )
        }
    }
}

class QuizViewModelFactory(
    private val repo: QuizRepository,
    private val categoryId: String,
    private val difficulty: Difficulty,
    private val sessionSize: Int = 10,
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizViewModel(repo, categoryId, difficulty, sessionSize) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}