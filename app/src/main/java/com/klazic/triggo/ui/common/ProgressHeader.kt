package com.klazic.triggo.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.klazic.triggo.R
import com.klazic.triggo.data.progress.Progress
import kotlin.math.max

@Composable
fun GlobalProgressHeader(global: Progress) {
    val attempted = max(0, global.attempted)
    val correct = max(0, global.correct)
    val masteryPct =
        if (attempted <= 0) 0
        else ((100f * correct / attempted).toInt()).coerceIn(0, 100)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.your_progress),
            style = MaterialTheme.typography.titleMedium
        )

        LinearProgressIndicator(
            progress = { masteryPct / 100f },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$correct / $attempted",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "$masteryPct%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun ProgressBarMini(label: String, percent: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.width(8.dp))
        LinearProgressIndicator(
            progress = { percent.coerceIn(0, 100) / 100f },
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(8.dp))
        Text("${percent.coerceIn(0, 100)}%", style = MaterialTheme.typography.bodySmall)
    }
}