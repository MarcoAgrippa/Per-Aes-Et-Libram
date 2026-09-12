package com.peraeslibram.domain.model

/** Vrsta sudskog/upravnog postupka u kom se predmet vodi. */
enum class TipPostupka {
    PARNICNI,
    KRIVICNI,
    UPRAVNI,
    PREKRSAJNI,
    DRUGO
}

/**
 * Vrsta pravne radnje za koju se računa procesni rok. Svaka vrednost (osim
 * [CUSTOM_GENERICKI]) odgovara jednom redu u seed tabeli [com.peraeslibram.domain.model.DeadlineRule].
 */
enum class TipRadnje {
    ZALBA_NA_PRESUDU_PARNICNI,
    ZALBA_NA_PRESUDU_MENICNI_CEKOVNI,
    ZALBA_NA_PRESUDU_KRIVICNI_REDOVNI,
    ZALBA_NA_PRESUDU_KRIVICNI_SKRACENI,
    ZALBA_NA_RESENJE_UPRAVNI,
    PRIGOVOR_NA_PREKRSAJNI_NALOG,
    ZALBA_PREKRSAJNI,
    CUSTOM_GENERICKI
}

enum class CaseStatus {
    AKTIVAN,
    ZAVRSEN,
    ARHIVIRAN
}

enum class HearingStatus {
    ZAKAZANO,
    ODRZANO,
    ODLOZENO,
    OTKAZANO
}

/**
 * "ISTEKAO" se namerno ne čuva kao vrednost u bazi — izvodi se u UI/domain sloju
 * poređenjem [AKTIVAN] statusa sa današnjim datumom, da ne bi bio potreban
 * pozadinski posao koji svakodnevno ažurira status u bazi.
 */
enum class DeadlineStatus {
    AKTIVAN,
    ISPUNJEN,
    OTKAZAN
}

/** Poreklo podatka o roku/ročištu. V1 podržava samo [MANUELNO]; ostalo su V2+ pravci. */
enum class DataSource {
    MANUELNO,
    SKENIRANJE,
    OCR
}

enum class NonWorkingDayType {
    FIKSNI,
    POKRETNI
}

enum class NonWorkingDaySource {
    AUTO_SEED,
    RUCNI_UNOS
}
