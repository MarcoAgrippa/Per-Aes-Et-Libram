package com.peraeslibram.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.peraeslibram.R

// "Classical" par: Cormorant Garamond (naslovi) / Lora (telo) — vidi
// _ds/classical.../readme.md. Naslovi ne idu preko poluboldа (--font-heading-weight: 600);
// veći tekst ide LAKŠE, ne teže — display veličine su na regular rezu.
@OptIn(ExperimentalTextApi::class)
private val HeadingFontFamily = FontFamily(
    Font(R.font.cormorant_garamond, FontWeight.Normal, variationSettings = FontVariation.Settings(FontVariation.weight(400))),
    Font(R.font.cormorant_garamond, FontWeight.Medium, variationSettings = FontVariation.Settings(FontVariation.weight(500))),
    Font(R.font.cormorant_garamond, FontWeight.SemiBold, variationSettings = FontVariation.Settings(FontVariation.weight(600)))
)

@OptIn(ExperimentalTextApi::class)
private val BodyFontFamily = FontFamily(
    Font(R.font.lora, FontWeight.Normal, variationSettings = FontVariation.Settings(FontVariation.weight(400))),
    Font(R.font.lora, FontWeight.Medium, variationSettings = FontVariation.Settings(FontVariation.weight(500))),
    Font(R.font.lora, FontWeight.SemiBold, variationSettings = FontVariation.Settings(FontVariation.weight(600)))
)

val PerAesEtLibramTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = HeadingFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 42.sp,
        letterSpacing = (-0.015).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = HeadingFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.01).sp
    ),
    // Naslovi stavki/ekrana u mock-u su ručno stilizovani div-ovi bez font-weight override-a,
    // pa nasleđuju body-jev regular (400) — "što je tekst veći, to je lakši", ne polubold
    // (polubold je rezervisan za .btn/.card-title/h1-h6 — vidi labelLarge/Medium ispod).
    titleLarge = TextStyle(
        fontFamily = HeadingFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 21.sp,
        lineHeight = 27.sp,
        letterSpacing = (-0.01).sp
    ),
    titleMedium = TextStyle(
        fontFamily = HeadingFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 23.sp
    ),
    titleSmall = TextStyle(
        fontFamily = HeadingFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 21.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 17.sp
    ),
    labelLarge = TextStyle(
        fontFamily = HeadingFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = HeadingFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.12.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = HeadingFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 0.1.sp,
        lineHeight = 15.sp
    )
)
