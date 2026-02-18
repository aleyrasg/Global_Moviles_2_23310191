package com.example.global_moviles_2_23310191.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8B5CF6),
    secondary = Color(0xFFA78BFA),
    tertiary = Color(0xFFD946EF),

    background = Color(0xFF0B1220),
    surface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFF1E293B),

    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color(0xFF0F172A),
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF3B4256),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF8B5CF6),
    secondary = Color(0xFF64748B),
    tertiary = Color(0xFFD946EF),

    background = Color(0xFFF6F7FF),
    surface = Color.White,
    surfaceVariant = Color(0xFFF1EEFF),

    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
)

@Composable
fun Global_Moviles_2_23310191Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
