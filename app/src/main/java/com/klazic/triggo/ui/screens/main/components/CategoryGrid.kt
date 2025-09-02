package com.klazic.triggo.ui.screens.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.klazic.triggo.data.quiz.Difficulty
import com.klazic.triggo.data.streak.StreakSummary
import com.klazic.triggo.di.LocalDailyStreak
import com.klazic.triggo.ui.screens.main.MainContract


@Composable
fun CategoryGrid(
    items: List<MainContract.CategoryUi>,
    continueTarget: MainContract.ContinueTarget?,
    onClick: (String, Difficulty) -> Unit
) {
    val streak = LocalDailyStreak.current
    val streakSummary by streak.summary.collectAsStateWithLifecycle(StreakSummary())

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 200.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(key = "continue", span = { GridItemSpan(maxLineSpan) }) {
            continueTarget?.let { t ->
                ContinueCard(
                    title = stringResource(t.titleRes),
                    difficulty = t.difficulty,
                    onContinue = { onClick(t.categoryId, t.difficulty) }
                )
            }
        }
        item(key = "daily", span = { GridItemSpan(maxLineSpan) }) {
            DailyChallenge(
                streak = streakSummary
            )
        }
        items(items, key = { it.id }) { c ->
            CategoryCard(c, onClick)
        }
    }
}
