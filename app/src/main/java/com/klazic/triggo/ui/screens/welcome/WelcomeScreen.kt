package com.klazic.triggo.ui.screens.welcome

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.klazic.triggo.R
import com.klazic.triggo.data.prefs.UserPrefs

@Composable
fun WelcomeRoute(onContinueToMain: () -> Unit) {
    val context = LocalContext.current
    val vm: WelcomeViewModel = viewModel(
        factory = WelcomeViewModelFactory(UserPrefs(context))
    )
    val state by vm.state.collectAsStateWithLifecycle()

    WelcomeScreen(
        state = state,
        onNameChange = vm::onNameChange,
        onStart = { vm.saveAndContinue(onContinueToMain) }
    )
}


@Composable
fun WelcomeScreen(
    state: WelcomeUiState,
    onNameChange: (String) -> Unit,
    onStart: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val focus = LocalFocusManager.current

    val bg = Brush.verticalGradient(
        colors = listOf(cs.secondaryContainer, cs.background)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .systemBarsPadding()
            .imePadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        AnimatedBackgroundBlobs(
            color1 = cs.primary.copy(alpha = 0.12f),
            color2 = cs.tertiary.copy(alpha = 0.10f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PulsingLogo()

            Text(
                stringResource(R.string.welcome),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = cs.onBackground
            )
            Text(
                stringResource(R.string.master_trigonometry),
                style = MaterialTheme.typography.bodyMedium,
                color = cs.onSurfaceVariant
            )

            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(containerColor = cs.surface),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = onNameChange,
                        label = { Text(stringResource(R.string.your_name)) },
                        singleLine = true,
                        isError = state.error != null,
                        supportingText = {
                            state.error?.let { Text(it, color = cs.error) }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            focus.clearFocus()
                            onStart()
                        }),
                        modifier = Modifier.fillMaxWidth()
                    )

                    val enabled = !state.isSaving && state.name.isNotBlank()
                    val scale by animateFloatAsState(
                        targetValue = if (enabled) 1f else 0.98f,
                        animationSpec = tween(200, easing = FastOutSlowInEasing), label = "btn-scale"
                    )

                    Button(
                        onClick = {
                            focus.clearFocus()
                            onStart()
                        },
                        enabled = enabled,
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text(if (state.isSaving) stringResource(R.string.saving) else stringResource(R.string.start), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PulsingLogo() {
    val inf = rememberInfiniteTransition(label = "pulse")
    val scale by inf.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    Image(
        painter = painterResource(R.drawable.logo_trigo),
        contentDescription = "Trigo logo",
        modifier = Modifier
            .size(220.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    )
}

@Composable
private fun AnimatedBackgroundBlobs(
    color1: androidx.compose.ui.graphics.Color,
    color2: androidx.compose.ui.graphics.Color
) {
    val inf = rememberInfiniteTransition(label = "bg")
    val shift1 by inf.animateFloat(
        initialValue = -80f,
        targetValue = 80f,
        animationSpec = infiniteRepeatable(tween(6000, easing = EaseInOut), RepeatMode.Reverse),
        label = "s1"
    )
    val shift2 by inf.animateFloat(
        initialValue = 90f,
        targetValue = -90f,
        animationSpec = infiniteRepeatable(tween(7000, easing = EaseInOut), RepeatMode.Reverse),
        label = "s2"
    )
    Canvas(Modifier.fillMaxSize()) {
        drawCircle(
            color = color1,
            radius = size.minDimension * 0.35f,
            center = androidx.compose.ui.geometry.Offset(
                x = size.width * 0.25f + shift1,
                y = size.height * 0.30f
            )
        )
        drawCircle(
            color = color2,
            radius = size.minDimension * 0.42f,
            center = androidx.compose.ui.geometry.Offset(
                x = size.width * 0.75f + shift2,
                y = size.height * 0.70f
            )
        )
    }
}
