package com.peraeslibram.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Generiše baseline profil praćenjem najčešćih korisničkih putanja: sa Dashboard-a na
 * detalje predmeta i formu ročišta, i kroz sve stavke bočne fioke. Cilj je da se klase i
 * metode korišćene pri PRVOJ navigaciji na svaki ekran AOT-kompajliraju, umesto da se
 * interpretiraju/JIT-uju tek kad korisnik prvi put klikne — što je i bio uzrok primetnog
 * kašnjenja izmerenog na debug buildu.
 */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() = rule.collect(
        packageName = "com.peraeslibram",
        includeInStartupProfile = true
    ) {
        pressHome()
        startActivityAndWait()

        // Dashboard -> detalji predmeta (seed podaci uvek imaju bar jednu stavku agende).
        device.wait(Until.hasObject(By.clickable(true)), 5_000)
        device.findObject(By.textContains("Rok"))?.let { agendaItem ->
            agendaItem.click()
            device.waitForIdle()

            // Detalji predmeta -> forma ročišta.
            device.wait(Until.hasObject(By.text("Dodaj ročište")), 5_000)
            device.findObject(By.text("Dodaj ročište"))?.let {
                it.click()
                device.wait(Until.hasObject(By.text("Datum ročišta *")), 5_000)
                device.pressBack()
            }

            device.waitForIdle()
            device.pressBack()
            device.waitForIdle()
        }

        // Sve stavke bočne fioke — svaka je i posebna ruta u NavGraph-u.
        val drawerDestinations = listOf("Predmeti", "Rokovi", "Sudovi", "Javni beležnici", "Podešavanja")
        for (destination in drawerDestinations) {
            device.findObject(By.desc("Meni"))?.click()
            device.wait(Until.hasObject(By.text(destination)), 5_000)
            device.findObject(By.text(destination))?.click()
            device.waitForIdle()
        }
    }
}
