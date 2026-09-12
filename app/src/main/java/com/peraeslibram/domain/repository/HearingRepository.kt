package com.peraeslibram.domain.repository

import com.peraeslibram.domain.model.Hearing
import kotlinx.coroutines.flow.Flow

interface HearingRepository {
    fun observeAll(): Flow<List<Hearing>>
    fun observeByCase(caseId: Long): Flow<List<Hearing>>
    suspend fun getById(id: Long): Hearing?

    /**
     * Snima ročište i (ponovo) zakazuje podsetnike za ponuđene [reminderMinutesBefore] offsete.
     * Implementacija MORA prvo otkazati postojeće alarme, zatim ukloniti stare [Reminder] redove,
     * pa tek onda kreirati i zakazati nove — nikad obrnuto (rizik od duplih/izgubljenih alarma).
     */
    suspend fun save(hearing: Hearing, reminderMinutesBefore: List<Long>): Long

    /** Otkazuje sve alarme vezane za ročište pre brisanja iz baze. */
    suspend fun delete(hearing: Hearing)
}
