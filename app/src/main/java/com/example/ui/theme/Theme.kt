package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppThemeMode {
    DARK, OLED, LIGHT
}

private val DarkVidKingColors = darkColorScheme(
    primary = RoyalGold,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF3B2D06),
    onPrimaryContainer = RoyalGoldLight,
    secondary = CrimsonRed,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF451118),
    onSecondaryContainer = CrimsonRedLight,
    tertiary = ElectricBlue,
    onTertiary = Color.Black,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DividerColor
)

private val OledVidKingColors = darkColorScheme(
    primary = RoyalGold,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF261D04),
    onPrimaryContainer = RoyalGoldLight,
    secondary = CrimsonRed,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF2B0A0E),
    onSecondaryContainer = CrimsonRedLight,
    tertiary = ElectricBlue,
    onTertiary = Color.Black,
    background = OledBackground,
    onBackground = TextPrimary,
    surface = OledSurface,
    onSurface = TextPrimary,
    surfaceVariant = OledCard,
    onSurfaceVariant = TextSecondary,
    outline = Color(0xFF1E1E1E)
)

private val LightVidKingColors = lightColorScheme(
    primary = Color(0xFFD97706),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEF3C7),
    onPrimaryContainer = Color(0xFF92400E),
    secondary = CrimsonRed,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE4E6),
    onSecondaryContainer = Color(0xFF9F1239),
    tertiary = ElectricBlue,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = Color(0xFFD1D5DB)
)

@Composable
fun VidKingTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        AppThemeMode.DARK -> DarkVidKingColors
        AppThemeMode.OLED -> OledVidKingColors
        AppThemeMode.LIGHT -> LightVidKingColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

