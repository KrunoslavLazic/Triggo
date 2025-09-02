package com.klazic.triggo.ui.screens.main.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.klazic.triggo.R
import com.klazic.triggo.data.progress.Coverage
import com.klazic.triggo.data.quiz.Difficulty
import com.klazic.triggo.di.LocalProgressStore
import com.klazic.triggo.di.LocalQuizRepository
import com.klazic.triggo.ui.common.ProgressBarMini
import com.klazic.triggo.ui.screens.main.MainContract

@Composable
fun CategoryCard(c: MainContract.CategoryUi, onClick: (String, Difficulty) -> Unit) {
    val store = LocalProgressStore.current
    val repo = LocalQuizRepository.current

    val counts = remember(c.id) { repo.countsByDifficulty(c.id) }
    val totalEasy = counts[Difficulty.EASY] ?: 0
    val totalMedium = counts[Difficulty.MEDIUM] ?: 0
    val totalHard = counts[Difficulty.HARD] ?: 0

    val coverage by store.coverageFlow(c.id).collectAsStateWithLifecycle(Coverage(0, 0, 0))
    val (solvedEasy, solvedMedium, solvedHard) = coverage

    val coverageEasy by remember(
        totalEasy,
        coverage
    ) { derivedStateOf { if (totalEasy == 0) 0 else 100 * solvedEasy / totalEasy } }

    val coverageMedium by remember(
        totalMedium,
        coverage
    ) { derivedStateOf { if (totalMedium == 0) 0 else 100 * solvedMedium / totalMedium } }

    val coverageHard by remember(
        totalHard,
        coverage
    ) { derivedStateOf { if (totalHard == 0) 0 else 100 * solvedHard / totalHard } }


    val next = when {
        coverageEasy < 100 -> Difficulty.EASY
        coverageMedium < 100 -> Difficulty.MEDIUM
        else -> Difficulty.HARD
    }

    val allDone = (coverageEasy == 100 && coverageMedium == 100 && coverageHard == 100)
    val base = if (allDone) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.secondaryContainer
    val accent = if (allDone) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.secondary

    val startColor by animateColorAsState(base, label = "cardStart")
    val endColor by animateColorAsState(
        lerp(base, accent, 0.35f),
        label = "cardEnd"
    )

    val onColor by animateColorAsState(
        if (allDone) MaterialTheme.colorScheme.onTertiaryContainer
        else MaterialTheme.colorScheme.onSecondaryContainer,
        label = "cardOn"
    )

    val brush = Brush.verticalGradient(listOf(startColor, endColor))

    Surface(
        onClick = { onClick(c.id, next) },
        shape = MaterialTheme.shapes.large,
        tonalElevation = 2.dp,
        modifier = Modifier.height(176.dp)
    ) {
        CompositionLocalProvider(LocalContentColor provides onColor) {
            Box(
                Modifier
                    .background(brush)
                    .padding(16.dp)
            ) {
                Box(
                    Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.10f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(painterResource(c.iconRes), null, Modifier.size(28.dp))
                        Text(
                            text = stringResource(c.titleRes),
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        Image(painterResource(c.iconRes), null, Modifier.size(28.dp))
                    }

                    if (allDone) {
                        Text(
                            stringResource(R.string.completed),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Text(text = "${c.questionCount} " + stringResource(R.string.questions), style = MaterialTheme.typography.bodyMedium)
                    }

                    HorizontalDivider()

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        ProgressBarMini(stringResource(R.string.easy), coverageEasy)
                        ProgressBarMini(stringResource(R.string.medium), coverageMedium)
                        ProgressBarMini(stringResource(R.string.hard), coverageHard)
                    }
                }
            }
        }
    }
}
