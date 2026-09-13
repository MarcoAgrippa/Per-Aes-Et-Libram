package com.peraeslibram.data.local.seed

import com.peraeslibram.domain.model.Notary
import java.time.Instant

/**
 * Za razliku od [CourtSeedData] (mali, zvanično fiksiran spisak državnih sudova), javnih
 * beležnika u Srbiji ima nekoliko stotina, reč je o privatnim kancelarijama koje se sele i
 * menjaju kontakt podatke znatno češće, a pouzdan i ažuran spisak adresa i telefona nije bio
 * dostupan u trenutku implementacije. Zato spisak ovde ostaje prazan — korisnik dodaje
 * beležnike ručno dugmetom „+", isto kao što je to bio slučaj sa sudovima pre nego što je
 * ugrađen zvanični spisak adresa.
 */
object NotarySeedData {

    fun defaultNotaries(): List<Notary> = emptyList()
}
