package com.klazic.triggo.ui.screens.quiz

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.klazic.triggo.R
import com.klazic.triggo.data.quiz.Difficulty
import com.klazic.triggo.di.LocalProgressStore
import com.klazic.triggo.di.LocalQuizRepository
import com.klazic.triggo.ui.common.LessonAssets
import com.klazic.triggo.ui.common.MathText
import com.klazic.triggo.ui.theme.GreenDifficulty
import com.klazic.triggo.ui.theme.YellowDifficulty
import com.klazic.triggo.ui.theme.RedDifficulty

@Composable
fun QuizRoute(
    categoryId: String,
    difficultyStr: String,
    onFinish: (correct: Int, total: Int) -> Unit
) {
    val repo = LocalQuizRepository.current
    val store = LocalProgressStore.current
    val difficulty = remember(difficultyStr) {
        runCatching { Difficulty.valueOf(difficultyStr) }.getOrDefault(Difficulty.EASY)
    }
    val vm: QuizViewModel = viewModel(
        factory = QuizViewModelFactory(repo, categoryId, difficulty)
    )
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.finished) {
        if (state.finished) {
            store.recordSession(categoryId, difficulty, state.correctCount, state.total)
            store.recordSolved(categoryId, difficulty, state.correctIds)
            onFinish(state.correctCount, state.total)
        }
    }

    QuizScreen(
        categoryId = categoryId,
        state = state,
        onSelect = vm::select,
        onSubmitOrNext = vm::submitOrNext
    )
}

@Composable
private fun QuizScreen(
    categoryId: String,
    state: QuizUiState,
    onSelect: (Int) -> Unit,
    onSubmitOrNext: () -> Unit
) {
    if (state.loading) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }
        return
    }

    val q = state.current ?: return
    val progress = if (state.total > 0) (state.index + 1f) / state.total.toFloat() else 0f

    Scaffold(
        topBar = {
            QuizHeader(
                categoryId = categoryId,
                index = state.index,
                total = state.total,
                difficulty = q.difficulty,
                progress = progress
            )
        },
        bottomBar = {
            Surface(tonalElevation = 3.dp) {
                Button(
                    onClick = onSubmitOrNext,
                    enabled = state.selected != null,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .navigationBarsPadding()
                ) {
                    val isLast = state.index == state.total - 1
                    AnimatedContent(targetState = isLast, label = "btn") {
                        Text(if (it) stringResource(R.string.finish) else stringResource(R.string.next))
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            val questionBg =
                if (isSystemInDarkTheme())
                    lerp(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.colorScheme.secondaryContainer,
                        0.22f
                    )
                else
                    lerp(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.colorScheme.secondaryContainer,
                        0.88f
                    )

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.elevatedCardElevation(4.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = questionBg)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                )
                            )
                        )
                )
                Column(Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(R.string.question),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(12.dp))

                    val longPrompt = q.promptLatex.length > 80
                    MathText(
                        q.promptLatex,
                        displayMode = !longPrompt,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 80.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                q.choicesLatex.forEachIndexed { i, choice ->
                    ChoiceCard(
                        textLatex = choice,
                        selected = state.selected == i,
                        onClick = { onSelect(i) }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QuizHeader(
    categoryId: String,
    index: Int,
    total: Int,
    difficulty: Difficulty,
    progress: Float
) {
    val gradient = Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(start = 16.dp, end = 8.dp, top = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(gradient)
                .padding(16.dp, 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
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
                        Text(
                            difficulty.name.lowercase().replaceFirstChar { it.titlecase() })
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

                Text(
                    text = "${index + 1} / $total",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(999.dp)),
                progress = { progress.coerceIn(0f, 1f) },
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.6f)
            )
        }
    }
}

@Composable
private fun ChoiceCard(
    textLatex: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val dark = isSystemInDarkTheme()
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    val baseBg = if (dark) cs.surfaceVariant else cs.surface
    val selectedBg = if (dark) lerp(baseBg, cs.secondaryContainer, 0.25f) else cs.secondaryContainer

    val container by animateColorAsState(if (selected) selectedBg else baseBg, label = "choiceBg")
    val borderWidth by animateDpAsState(if (selected) 2.dp else 1.dp, label = "choiceBw")
    val borderColor by animateColorAsState(if (selected) cs.secondary else cs.outlineVariant, label = "choiceBc")
    val elevation by animateDpAsState(if (selected || pressed) 8.dp else 2.dp, label = "choiceElevation")
    val scale by animateFloatAsState(if (pressed) 0.98f else 1f, label = "pressScale")

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .border(BorderStroke(borderWidth, borderColor), RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interaction,
                indication = LocalIndication.current,
                role = Role.Button,
                onClick = onClick
            ),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = elevation),
        colors = CardDefaults.elevatedCardColors(containerColor = container),
    ) {
        Box(
            Modifier
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .defaultMinSize(minHeight = 56.dp)
        ) {
            if (selected) {
                Box(
                    Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    cs.secondary.copy(alpha = 0.55f),
                                    cs.secondary.copy(alpha = 0.25f)
                                )
                            )
                        )
                )
            }

            MathText(
                latex = textLatex,
                displayMode = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 44.dp, max = 180.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@Preview
@Composable
private fun QuizHeaderPreview() {
    QuizHeader("osnove_kutova_i_radijana", 1, 10, Difficulty.EASY, 0.1f)
}
