package com.klazic.triggo.ui.screens.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.klazic.triggo.data.quiz.Difficulty

object MainContract {

    @Immutable
    data class UiState(
        val categories: List<CategoryUi> = emptyList(),
        val continueTarget: ContinueTarget? = null,
        val isLoading: Boolean = false
    )

    @Immutable
    data class ContinueTarget(
        val categoryId: String,
        @StringRes val titleRes: Int,
        val difficulty: Difficulty
    )

    @Immutable
    data class CategoryUi(
        val id: String,
        @StringRes val titleRes: Int,
        val questionCount: Int,
        @DrawableRes val iconRes:Int
    )

    sealed interface UiEvent {
        data class OnCategoryClick(val id: String) : UiEvent
        data class OnContinue(val categoryId: String, val difficulty: Difficulty): UiEvent
        data object OnResetProgress : UiEvent
    }

    sealed interface UiEffect {
        data class ShowMessage(val messageRes: Int) : UiEffect
        data class NavigateToQuiz(val categoryId: String, val difficulty: Difficulty) : UiEffect
    }


}