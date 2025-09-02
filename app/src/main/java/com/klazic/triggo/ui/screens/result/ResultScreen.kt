package com.klazic.triggo.ui.screens.result

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.klazic.triggo.R
import com.klazic.triggo.data.prefs.UserPrefs
import com.klazic.triggo.data.progress.Progress
import com.klazic.triggo.data.quiz.Difficulty
import com.klazic.triggo.di.LocalDailyStreak
import com.klazic.triggo.di.LocalProgressStore
import com.klazic.triggo.ui.common.AvatarAssets
import com.klazic.triggo.ui.common.LessonAssets
import com.klazic.triggo.ui.common.UserBadge
import kotlin.math.roundToInt
import com.klazic.triggo.ui.theme.GreenDifficulty
import com.klazic.triggo.ui.theme.YellowDifficulty
import com.klazic.triggo.ui.theme.RedDifficulty

@Composable
fun ResultRoute(
    categoryId: String,
    difficultyStr: String,
    correct: Int,
    total: Int,
    onBackHome: () -> Unit,
    onRetry: (catId: String, diff: String) -> Unit
) {
    val store = LocalProgressStore.current
    val diff = remember(difficultyStr) {
        runCatching { Difficulty.valueOf(difficultyStr) }.getOrDefault(Difficulty.EASY)
    }

    val vm: ResultViewModel =
        viewModel(factory = ResultViewModelFactory(store, categoryId, diff, correct, total))
    val progress by vm.progress.collectAsStateWithLifecycle(initialValue = Progress())
    val context = LocalContext.current
    val prefs = remember(context) { UserPrefs(context) }
    val userName by prefs.name.collectAsStateWithLifecycle(initialValue = "")
    val avatarIndex by prefs.avatar.collectAsStateWithLifecycle(initialValue = 0)

    val streak = LocalDailyStreak.current
    LaunchedEffect(Unit) {
        streak.markActiveToday()
    }

    ResultScreen(
        categoryId = categoryId,
        difficulty = diff,
        correct = correct,
        total = total,
        progress = progress,
        onBackHome = onBackHome,
        onRetry = { onRetry(categoryId, difficultyStr) },
        userName = userName,
        avatarRes = AvatarAssets.at(avatarIndex)
    )
}

@Composable
private fun ResultScreen(
    categoryId: String,
    difficulty: Difficulty,
    correct: Int,
    total: Int,
    progress: Progress,
    onBackHome: () -> Unit,
    onRetry: () -> Unit,
    userName: String,
    @DrawableRes avatarRes: Int
) {
    val pct = if (total > 0) correct.toFloat() / total.toFloat() else 0f
    val pctInt = (pct * 100).roundToInt()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = { ResultHeader(categoryId, difficulty) },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp)
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onBackHome,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(

                    )
                ) { Text(stringResource(R.string.home)) }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onRetry,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                ) { Text(stringResource(R.string.retry)) }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp) // matches Quiz
        ) {
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                UserBadge(
                    name = userName,
                    avatarRes = avatarRes,
                    modifier = Modifier.weight(1f),
                    size = 140.dp
                )

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(140.dp),
                        progress = { pct.coerceIn(0f, 1f) },
                        strokeWidth = 12.dp,
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    )
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "$pctInt",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.this_session),
                    value = "$correct / $total",
                    subtitle = stringResource(R.string.correct_answers)
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.best_score),
                    value = "${progress.bestScorePct}",
                    subtitle = stringResource(R.string.all_time)
                )
            }

            Spacer(Modifier.height(12.dp))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.elevatedCardElevation(4.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor =
                        isSystemInDarkTheme()
                            .let { dark ->
                                if (dark)
                                    lerp(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        MaterialTheme.colorScheme.secondaryContainer,
                                        0.18f
                                    )
                                else
                                    MaterialTheme.colorScheme.surface
                            }
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        stringResource(R.string.mastery),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(999.dp)),
                        progress = { (progress.masteryPct / 100f).coerceIn(0f, 1f) },
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "${progress.masteryPct}% - ${progress.correct}/${progress.attempted}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ResultHeader(
    categoryId: String,
    difficulty: Difficulty
) {
    val overlay = if (isSystemInDarkTheme()) 0.30f else 0.15f
    val gradient = Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.primary.copy(overlay),
            MaterialTheme.colorScheme.secondary.copy(overlay),
            MaterialTheme.colorScheme.tertiary.copy(overlay)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .clip(RoundedCornerShape(20.dp))
            .padding(16.dp, 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(gradient)
                .padding(16.dp, 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(LessonAssets.iconRes(categoryId)),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(LessonAssets.titleRes(categoryId)),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

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
                }
            )
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.result),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier,
    title: String,
    value: String,
    subtitle: String? = null
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isSystemInDarkTheme())
                lerp(
                    MaterialTheme.colorScheme.surfaceVariant,
                    MaterialTheme.colorScheme.secondaryContainer,
                    0.22f
                )
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            if (subtitle != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview
@Composable
private fun ResultHeaderPreview() {
    ResultHeader("grafovi_trigonometrijskih_funkcija", Difficulty.HARD)
}