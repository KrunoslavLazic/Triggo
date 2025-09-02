package com.klazic.triggo.di

import androidx.compose.runtime.staticCompositionLocalOf
import com.klazic.triggo.data.progress.ProgressStore
import com.klazic.triggo.data.quiz.QuizRepository
import com.klazic.triggo.data.streak.DailyStreakScore

val LocalProgressStore = staticCompositionLocalOf<ProgressStore> {
    error("LocalProgressStore not provided")
}
val LocalQuizRepository = staticCompositionLocalOf<QuizRepository> {
    error("LocalQuizRepository not provided")
}
val LocalDailyStreak = staticCompositionLocalOf<DailyStreakScore> {
    error("LocalDailyStreak not provided")
}