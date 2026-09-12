package com.peraeslibram.domain.usecase

import com.peraeslibram.domain.model.DeadlineStatus
import com.peraeslibram.domain.model.DeadlineWithCase
import com.peraeslibram.domain.repository.CaseRepository
import com.peraeslibram.domain.repository.DeadlineRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/** Svi aktivni rokovi kroz sve predmete, sa nazivom predmeta — za ekran "Rokovi". */
class GetAllDeadlinesUseCase @Inject constructor(
    private val deadlineRepository: DeadlineRepository,
    private val caseRepository: CaseRepository
) {
    operator fun invoke(): Flow<List<DeadlineWithCase>> = combine(
        deadlineRepository.observeAll(),
        caseRepository.observeAll()
    ) { deadlines, cases ->
        val caseNazivById = cases.associate { it.id to it.naziv }

        deadlines
            .filter { it.status == DeadlineStatus.AKTIVAN }
            .map { DeadlineWithCase(it, caseNazivById[it.caseId]) }
            .sortedBy { it.deadline.izracunatiKrajnjiDatum }
    }
}
