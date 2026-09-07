package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = VibrantGreen,
  onPrimary = Color.Black,
  primaryContainer = ActiveTabPill,
  onPrimaryContainer = VibrantGreen,
  secondary = AccentGreen,
  onSecondary = Color.Black,
  secondaryContainer = DarkSurfaceVariant,
  onSecondaryContainer = TextPrimary,
  tertiary = VibrantGreen,
  onTertiary = Color.Black,
  background = AmoledBackground,
  onBackground = TextPrimary,
  surface = AmoledBackground,
  onSurface = TextPrimary,
  surfaceVariant = SearchBarBackground,
  onSurfaceVariant = TextSecondary,
  outline = BorderDivider,
  outlineVariant = BorderDivider
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force AMOLED dark theme as requested
  dynamicColor: Boolean = false, // Keep original AMOLED & Green theme consistent
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content
  )
}
