package com.lavka.calculator.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.lavka.calculator.data.ThemeMode

private val LightColorScheme = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = CreamSurface,
    primaryContainer = ResultCardLight,
    onPrimaryContainer = TextOnLight,
    secondary = OrangeDark,
    onSecondary = CreamSurface,
    background = CreamBackground,
    onBackground = TextOnLight,
    surface = CreamSurface,
    onSurface = TextOnLight,
    surfaceVariant = ResultCardLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = OrangePrimary.copy(alpha = 0.5f)
)

private val DarkColorScheme = darkColorScheme(
    primary = OrangePrimary,
    onPrimary = TextOnDark,
    primaryContainer = ResultCardDark,
    onPrimaryContainer = TextOnDark,
    secondary = OrangeDark,
    onSecondary = TextOnDark,
    background = DarkGrayBackground,
    onBackground = TextOnDark,
    surface = DarkGraySurface,
    onSurface = TextOnDark,
    surfaceVariant = ResultCardDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = OrangePrimary.copy(alpha = 0.6f)
)

@Composable
fun LavkaCalculatorTheme(
    themeMode: ThemeMode,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
