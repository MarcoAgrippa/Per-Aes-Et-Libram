package com.peraeslibram.domain.usecase

import com.peraeslibram.domain.model.AgendaItem
import com.peraeslibram.domain.model.DeadlineStatus
import com.peraeslibram.domain.model.HearingStatus
import com.peraeslibram.domain.repository.CaseRepository
import com.peraeslibram.domain.repository.DeadlineRepository
import com.peraeslibram.domain.repository.HearingRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/** Spaja zakazana ročišta i aktivne rokove u jednu hronološku agendu za Dashboard. */
class GetUpcomingAgendaUseCase @Inject constructor(
    private val hearingRepository: HearingRepository,
    private val deadlineRepository: DeadlineRepository,
    private val caseRepository: CaseRepository
) {
    operator fun invoke(): Flow<List<AgendaItem>> = combine(
        hearingRepository.observeAll(),
        deadlineRepository.observeAll(),
        caseRepository.observeAll()
    ) { hearings, deadlines, cases ->
        val caseById = cases.associateBy { it.id }

        val hearingItems = hearings
            .filter { it.status == HearingStatus.ZAKAZANO }
            .map { AgendaItem.HearingItem(it, caseById[it.caseId]?.naziv, caseById[it.caseId]?.brojPredmeta) }

        val deadlineItems = deadlines
            .filter { it.status == DeadlineStatus.AKTIVAN }
            .map { AgendaItem.DeadlineItem(it, caseById[it.caseId]?.naziv, caseById[it.caseId]?.brojPredmeta) }

        (hearingItems + deadlineItems).sortedBy { it.dateTime }
    }
}
