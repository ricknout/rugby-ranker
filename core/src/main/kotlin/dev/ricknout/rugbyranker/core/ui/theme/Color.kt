package dev.ricknout.rugbyranker.core.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val LightGreen = Color(0xFF7BC075)
private val Green = Color(0xFF2D8626)
private val DarkGreen = Color(0xFF005800)
private val LightBlue = Color(0xFF768FFF)
private val Blue = Color(0xFF0000AC)
private val LightRed = Color(0xFFFF5C44)
private val Red = Color(0xFFE51A19)

internal val LightRugbyRankerColorScheme = lightColorScheme(
    primary = Green,
    onPrimary = Color.White,
    secondary = DarkGreen,
    onSecondary = Color.White,
    tertiary = Blue,
    onTertiary = Color.Black,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    surfaceTint = Color.White,
    onSurface = Color.Black,
    inverseSurface = Color.Black,
    inverseOnSurface = Color.White,
    error = Red,
    onError = Color.White,
)

internal val DarkRugbyRankerColorScheme = darkColorScheme(
    primary = LightGreen,
    onPrimary = Color.Black,
    secondary = Green,
    onSecondary = Color.Black,
    tertiary = LightBlue,
    onTertiary = Color.Black,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color.Black,
    surfaceTint = Color.White,
    onSurface = Color.White,
    inverseSurface = Color.White,
    inverseOnSurface = Color.Black,
    error = LightRed,
    onError = Color.Black,
)
