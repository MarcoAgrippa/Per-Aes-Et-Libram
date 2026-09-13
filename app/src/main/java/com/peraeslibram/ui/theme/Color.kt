package com.peraeslibram.ui.theme

import androidx.compose.ui.graphics.Color

// "Classical" tokeni (Claude Design, sistem "classical-b89e1c0d...") — editorijalski, mono-akcent
// (bronza), bez punjenih površina. Vidi _ds/classical.../styles.css i readme.md za izvor.

// Neutralna rampa (OKLCH, ista percepciona svetlina po koraku)
val Neutral100 = Color(0xFFF8F4F4)
val Neutral200 = Color(0xFFEAE7E7)
val Neutral300 = Color(0xFFD7D3D3)
val Neutral400 = Color(0xFFBAB6B6)
val Neutral500 = Color(0xFF9B9797)
val Neutral600 = Color(0xFF7D7979)
val Neutral700 = Color(0xFF605D5D)
val Neutral800 = Color(0xFF444141)
val Neutral900 = Color(0xFF2D2B2B)

// Akcentna rampa (bronza — jedini akcent u sistemu, mono šema)
val Accent100 = Color(0xFFFFF3E4)
val Accent200 = Color(0xFFFFE3BF)
val Accent300 = Color(0xFFFACB8D)
val Accent400 = Color(0xFFE1AD66)
val Accent500 = Color(0xFFC28D41)
val Accent600 = Color(0xFFA06F24)
val Accent700 = Color(0xFF7D5411)
val Accent800 = Color(0xFF5A3B0A)
val Accent900 = Color(0xFF3A270D)

val ColorText = Color(0xFF201F1D)
val ColorBg = Color(0xFFF3F2F2)
val ColorSurface = Color(0xFFEAE9E9)

// Svetla tema
val PrimaryLight = Accent500
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Accent100
val OnPrimaryContainerLight = Accent800

val SecondaryLight = Accent600
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Neutral100
val OnSecondaryContainerLight = Neutral800

val TertiaryLight = Accent600
val OnTertiaryLight = Color(0xFFFFFFFF)
val TertiaryContainerLight = Accent200
val OnTertiaryContainerLight = Accent900

// Nema crvene u "classical" tokenima (hitnost se signalizira akcentnom značkom) — ovo je
// namerno pridržana, blago zatoplja crvena samo za destruktivne akcije (brisanje).
val ErrorLight = Color(0xFFA23B2E)
val OnErrorLight = Color(0xFFFFFFFF)
val ErrorContainerLight = Color(0xFFF7E3DD)
val OnErrorContainerLight = Color(0xFF5C2A1E)

val BackgroundLight = ColorBg
val OnBackgroundLight = ColorText
val SurfaceLight = ColorSurface
val OnSurfaceLight = ColorText
val SurfaceVariantLight = Neutral100
val OnSurfaceVariantLight = Neutral700
val OutlineLight = Color(0x66201F1D) // ~40% — ivice/border koje moraju biti dovoljno vidljive
val OutlineVariantLight = Color(0x29201F1D) // ~16% — --color-divider

// Tamna tema (nije u mock-u; smišljena kao dosledan pandan istom akcentu)
val PrimaryDark = Accent400
val OnPrimaryDark = Accent900
val PrimaryContainerDark = Accent800
val OnPrimaryContainerDark = Accent100

val SecondaryDark = Accent400
val OnSecondaryDark = Accent900
val SecondaryContainerDark = Neutral800
val OnSecondaryContainerDark = Neutral200

val TertiaryDark = Accent400
val OnTertiaryDark = Accent900
val TertiaryContainerDark = Accent800
val OnTertiaryContainerDark = Accent100

val ErrorDark = Color(0xFFE0897A)
val OnErrorDark = Color(0xFF3A140D)
val ErrorContainerDark = Color(0xFF5C2A1E)
val OnErrorContainerDark = Color(0xFFF7E3DD)

val BackgroundDark = Color(0xFF1C1B1A)
val OnBackgroundDark = Neutral200
val SurfaceDark = Color(0xFF242322)
val OnSurfaceDark = Neutral200
val SurfaceVariantDark = Color(0xFF322F2D)
val OnSurfaceVariantDark = Neutral400
val OutlineDark = Color(0x66EAE7E7)
val OutlineVariantDark = Color(0x29EAE7E7)
