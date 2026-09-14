package com.peraeslibram.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

/** Postavlja se na root nivou (`MainActivity`) iz [com.peraeslibram.data.settings.AppSettingsRepository]. */
val LocalScript = compositionLocalOf { Script.LATIN }

/**
 * Za tekst koji se ispisuje kroz `value` parametar polja za unos (npr. rezultat izbora iz
 * padajućeg menija) — [Text] wrapper ovde ne pomaže jer `OutlinedTextField`/`ExposedDropdownMenu`
 * ne prikazuju svoj `value` kroz [androidx.compose.material3.Text], već ga renderuju direktno.
 */
@Composable
fun displayText(text: String): String =
    if (LocalScript.current == Script.CYRILLIC) text.toCyrillic() else text
