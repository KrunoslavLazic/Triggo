package com.klazic.triggo.ui.screens.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.klazic.triggo.R
import com.klazic.triggo.data.streak.StreakSummary

@Composable
fun DailyChallenge(
    streak: StreakSummary
) {
    val dots = 8
    val filled = minOf(streak.current, dots)

    Surface(tonalElevation = 3.dp, shape = MaterialTheme.shapes.large) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.daily_challenge), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.weight(1f))
                Text("🔥 ${streak.current}", style = MaterialTheme.typography.titleMedium)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(dots) { idx ->
                    val hit = idx < filled
                    Box(
                        Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(
                                if (hit) MaterialTheme.colorScheme.secondary
                                else MaterialTheme.colorScheme.outlineVariant
                            )
                            .semantics { contentDescription = if (hit) "Filled ${idx+1}" else "Empty ${idx+1}" }
                    )
                }
            }

            Text(
                if (streak.todayActive) stringResource(R.string.streak_kept)
                else stringResource(R.string.streak_message),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
