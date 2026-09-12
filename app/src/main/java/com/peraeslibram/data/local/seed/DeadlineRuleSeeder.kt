package com.peraeslibram.data.local.seed

import com.peraeslibram.domain.model.DeadlineRule
import com.peraeslibram.domain.model.TipPostupka
import com.peraeslibram.domain.model.TipRadnje

/**
 * Ugrađena pravna pravila za automatski obračun rokova, potvrđena istraživanjem:
 * ZPP (parnični), ZKP (krivični), ZUP (upravni) i Zakon o prekršajima.
 *
 * Produženje roka u naročito složenim krivičnim predmetima (ZKP) je poznat edge case
 * koji V1 ne automatizuje — advokat ga rešava ručnim override-om broja dana u formi roka.
 */
object DeadlineRuleSeeder {

    val defaultRules: List<DeadlineRule> = listOf(
        DeadlineRule(
            tipPostupka = TipPostupka.PARNICNI,
            tipRadnje = TipRadnje.ZALBA_NA_PRESUDU_PARNICNI,
            brojDana = 15,
            nazivPrikaz = "Žalba na presudu (parnični postupak)",
            napomenaPravno = "ZPP — rok od 15 dana od dostavljanja prepisa presude."
        ),
        DeadlineRule(
            tipPostupka = TipPostupka.PARNICNI,
            tipRadnje = TipRadnje.ZALBA_NA_PRESUDU_MENICNI_CEKOVNI,
            brojDana = 8,
            nazivPrikaz = "Žalba na presudu (menični/čekovni spor)",
            napomenaPravno = "ZPP — rok od 8 dana u meničnim i čekovnim sporovima."
        ),
        DeadlineRule(
            tipPostupka = TipPostupka.KRIVICNI,
            tipRadnje = TipRadnje.ZALBA_NA_PRESUDU_KRIVICNI_REDOVNI,
            brojDana = 15,
            nazivPrikaz = "Žalba na presudu (redovni krivični postupak)",
            napomenaPravno = "ZKP — rok od 15 dana od dostavljanja prepisa presude."
        ),
        DeadlineRule(
            tipPostupka = TipPostupka.KRIVICNI,
            tipRadnje = TipRadnje.ZALBA_NA_PRESUDU_KRIVICNI_SKRACENI,
            brojDana = 8,
            nazivPrikaz = "Žalba na presudu (skraćeni krivični postupak)",
            napomenaPravno = "ZKP — rok od 8 dana u skraćenom postupku."
        ),
        DeadlineRule(
            tipPostupka = TipPostupka.UPRAVNI,
            tipRadnje = TipRadnje.ZALBA_NA_RESENJE_UPRAVNI,
            brojDana = 15,
            nazivPrikaz = "Žalba na rešenje (upravni postupak)",
            napomenaPravno = "ZUP — rok od 15 dana od dostavljanja rešenja."
        ),
        DeadlineRule(
            tipPostupka = TipPostupka.PREKRSAJNI,
            tipRadnje = TipRadnje.PRIGOVOR_NA_PREKRSAJNI_NALOG,
            brojDana = 8,
            nazivPrikaz = "Prigovor na prekršajni nalog",
            napomenaPravno = "Zakon o prekršajima — rok od 8 dana od dostavljanja naloga."
        ),
        DeadlineRule(
            tipPostupka = TipPostupka.PREKRSAJNI,
            tipRadnje = TipRadnje.ZALBA_PREKRSAJNI,
            brojDana = 8,
            nazivPrikaz = "Žalba na presudu/rešenje (prekršajni postupak)",
            napomenaPravno = "Zakon o prekršajima — rok od 8 dana od dostavljanja."
        ),
        DeadlineRule(
            tipPostupka = null,
            tipRadnje = TipRadnje.CUSTOM_GENERICKI,
            brojDana = null,
            nazivPrikaz = "Generički rok (broj dana unosi advokat)",
            napomenaPravno = "Bez ugrađenog pravnog pravila — koristi se za slučajeve van gornje liste."
        )
    )
}
