package com.klazic.triggo.ui.screens.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.klazic.triggo.data.progress.Progress
import com.klazic.triggo.data.progress.ProgressStore
import com.klazic.triggo.data.quiz.Difficulty
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ResultViewModel(
    private val store: ProgressStore,
    private val cat: String,
    private val diff: Difficulty,
    private val correct: Int,
    private val total: Int
) : ViewModel() {
    val progress =
        store.flow(cat, diff)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Progress())

    init {
        viewModelScope.launch {
            store.recordSession(cat, diff, correct, total)
        }
    }

}

class ResultViewModelFactory(
    private val store: ProgressStore,
    private val categoryId: String,
    private val difficulty: Difficulty,
    private val correct: Int,
    private val total: Int
):ViewModelProvider.Factory{

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResultViewModel::class.java)) {
            return ResultViewModel(store, categoryId, difficulty, correct, total) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}