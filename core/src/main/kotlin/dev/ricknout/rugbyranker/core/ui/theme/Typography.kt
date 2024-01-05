package dev.ricknout.rugbyranker.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import dev.ricknout.rugbyranker.core.R

private val GoogleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val RajdhaniGoogleFont = GoogleFont("Rajdhani")

private val RajdhaniSemiBoldFontFamily = FontFamily(
    Font(
        googleFont = RajdhaniGoogleFont,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.SemiBold,
    )
)

internal val RugbyRankerTypography = Typography(
    headlineMedium = TextStyle(
        fontFamily = RajdhaniSemiBoldFontFamily,
        fontSize = 34.sp,
        letterSpacing = 0.00735294118.em,
    ),
    titleLarge = TextStyle(
        fontFamily = RajdhaniSemiBoldFontFamily,
        fontSize = 20.sp,
        letterSpacing = 0.0125.em,
    ),
    bodyLarge = TextStyle(
        fontFamily = RajdhaniSemiBoldFontFamily,
        fontSize = 16.sp,
        letterSpacing = 0.03125.em,
    ),
)
