package com.klazic.triggo.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


val GreenPrimary = Color(0xFF1FA37B)
val OrangeAccent = Color(0xFFF5B86B)
val OrangeAccentContainer = Color(0xFFFFE0B2)
val BlueTertiary = Color(0xFF3656E3)


val TrigoLightColors = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFAEE6D7),
    onPrimaryContainer = Color(0xFF073A2B),

    secondary = OrangeAccent,
    onSecondary = Color(0xFF3D2700),
    secondaryContainer = OrangeAccentContainer,
    onSecondaryContainer = Color(0xFF402400),

    tertiary = BlueTertiary,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFDCE1FF),
    onTertiaryContainer = Color(0xFF00164D),

    background = Color(0xFFFEFBF6),
    //background = Color(0xFF1FA37B),
    onBackground = Color(0xFF1D1B1E),
    surface = Color(0xFFFFFBFF),
    onSurface = Color(0xFF1D1B1E),
    surfaceVariant = Color(0xFFEDE5DF),
    onSurfaceVariant = Color(0xFF4B454D),
    outline = Color(0xFF7C757E),
    outlineVariant = Color(0xFFCFC7C1),

    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    inverseSurface = Color(0xFF2E2E31),
    inverseOnSurface = Color(0xFFF6EFF3),
    inversePrimary = Color(0xFF8ADBC5)
)

val TriggoDarkColors = darkColorScheme(
    primary = Color(0xFF8ADBC5),
    onPrimary = Color(0xFF003827),
    primaryContainer = Color(0xFF0B3E31),
    onPrimaryContainer = Color(0xFFAEE6D7),

    secondary = Color(0xFFF0BB7E),
    onSecondary = Color(0xFF402400),
    secondaryContainer = Color(0xFF5D3A00),
    onSecondaryContainer = Color(0xFFFFDCBE),

    tertiary = Color(0xFFB5C3FF),
    onTertiary = Color(0xFF0B285C),
    tertiaryContainer = Color(0xFF243F99),
    onTertiaryContainer = Color(0xFFDCE1FF),

    background = Color(0xFF121314),
    onBackground = Color(0xFFE7E1E5),
    surface = Color(0xFF121314),
    onSurface = Color(0xFFE7E1E5),
    surfaceVariant = Color(0xFF3C383F),
    onSurfaceVariant = Color(0xFFC9C2CA),
    outline = Color(0xFF948F96),
    outlineVariant = Color(0xFF4B454D),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    inverseSurface = Color(0xFFE7E1E5),
    inverseOnSurface = Color(0xFF2E2E31),
    inversePrimary = GreenPrimary
)

@Composable
fun TrigoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val canUseDynamic = dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val colorScheme = when {
        canUseDynamic -> {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }

        darkTheme -> TriggoDarkColors
        else -> TrigoLightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}