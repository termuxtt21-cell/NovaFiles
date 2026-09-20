package com.novafiles.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NovaBackground = Color(0xFF05070B)
private val NovaSurface = Color(0xFF111722)
private val NovaPrimary = Color(0xFF4CC9FF)
private val NovaSecondary = Color(0xFF7B61FF)
private val NovaText = Color(0xFFF4F7FB)
private val NovaTextSecondary = Color(0xFF8C98AA)

private val NovaDarkColors = darkColorScheme(
    primary = NovaPrimary,
    onPrimary = Color.Black,

    secondary = NovaSecondary,
    onSecondary = Color.White,

    background = NovaBackground,
    onBackground = NovaText,

    surface = NovaSurface,
    onSurface = NovaText,

    surfaceVariant = Color(0xFF182130),
    onSurfaceVariant = NovaTextSecondary,

    outline = Color(0xFF303A4A)
)

@Composable
fun NovaFilesTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NovaDarkColors,
        content = content
    )
}
