package com.klazic.triggo.ui.screens.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.klazic.triggo.R
import com.klazic.triggo.data.quiz.Difficulty
import com.klazic.triggo.ui.theme.GreenDifficulty
import com.klazic.triggo.ui.theme.RedDifficulty
import com.klazic.triggo.ui.theme.YellowDifficulty


@Composable
fun ContinueCard(
    title: String,
    difficulty: Difficulty,
    onContinue: () -> Unit
) {
    Surface(
        onClick = onContinue,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.continue_label),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.weight(1f)
                )
                AssistChip(
                    onClick = {},
                    label = {
                        Text(difficulty.name.lowercase().replaceFirstChar { it.titlecase() })
                    },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    when (difficulty) {
                                        Difficulty.EASY -> GreenDifficulty
                                        Difficulty.MEDIUM -> YellowDifficulty
                                        Difficulty.HARD -> RedDifficulty
                                    }
                                )
                        )
                    },
                    enabled = false
                )
            }
            Text(title, style = MaterialTheme.typography.headlineSmall)

            Text(
                stringResource(R.string.continue_subtitle),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
