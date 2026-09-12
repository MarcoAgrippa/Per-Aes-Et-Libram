package com.peraeslibram.domain.usecase

import com.peraeslibram.domain.calculator.DeadlineCalculationResult
import com.peraeslibram.domain.calculator.DeadlineCalculator
import com.peraeslibram.domain.repository.NonWorkingDayRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Povezuje čist [DeadlineCalculator] sa stvarnim podacima o praznicima iz baze.
 * Uzima praznike za sve godine koje obračun potencijalno može da dotakne (uključujući
 * godinu posle "sirovog" krajnjeg datuma, zbog mogućeg kaskadnog pomeranja preko
 * višednevnog praznika na prelazu godine).
 */
class CalculateDeadlineDueDateUseCase @Inject constructor(
    private val calculator: DeadlineCalculator,
    private val nonWorkingDayRepository: NonWorkingDayRepository
) {
    suspend operator fun invoke(triggerDate: LocalDate, brojDana: Int): DeadlineCalculationResult {
        val approximateRawDueDate = triggerDate.plusDays(brojDana.toLong())
        val relevantYears = (triggerDate.year..(approximateRawDueDate.year + 1)).toSet()

        val seededYears = nonWorkingDayRepository.getSeededYears()
        val nonWorkingDays = relevantYears
            .flatMap { year -> nonWorkingDayRepository.getForYear(year) }
            .map { it.datum }
            .toSet()

        return calculator.calculateDueDate(
            triggerDate = triggerDate,
            brojDana = brojDana,
            nonWorkingDays = nonWorkingDays,
            yearsWithHolidayData = relevantYears.intersect(seededYears)
        )
    }
}
