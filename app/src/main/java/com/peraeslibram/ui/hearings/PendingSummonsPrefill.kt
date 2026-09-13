package com.peraeslibram.ui.hearings

import com.peraeslibram.domain.ocr.ParsedSummons
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Prenosi rezultat OCR skeniranja poziva iz CaseDetail u HearingForm bez provlačenja
 * slobodnog teksta (sud, napomena, ćirilica...) kroz URL navigacione argumente, gde bi
 * specijalni znakovi lako pravili probleme s enkodiranjem/dužinom. Jednokratno: [take]
 * čita i odmah briše, tako da se stari predlog ne pojavi pri sledećem ručnom "Dodaj ročište".
 */
@Singleton
class PendingSummonsPrefill @Inject constructor() {
    private var value: ParsedSummons? = null

    fun set(prefill: ParsedSummons) {
        value = prefill
    }

    fun take(): ParsedSummons? = value.also { value = null }
}
