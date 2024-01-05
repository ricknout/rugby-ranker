package dev.ricknout.rugbyranker.core.ui.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun RugbyRankerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkRugbyRankerColorScheme else LightRugbyRankerColorScheme
    val rippleIndication = rememberRipple(bounded = false)
    CompositionLocalProvider(LocalIndication provides rippleIndication) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = RugbyRankerTypography,
            shapes = RugbyRankerShapes,
            content = content,
        )
    }
}
