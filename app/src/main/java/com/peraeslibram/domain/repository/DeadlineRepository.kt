package com.peraeslibram.domain.repository

import com.peraeslibram.domain.model.Deadline
import kotlinx.coroutines.flow.Flow

interface DeadlineRepository {
    fun observeAll(): Flow<List<Deadline>>
    fun observeByCase(caseId: Long): Flow<List<Deadline>>
    suspend fun getById(id: Long): Deadline?

    /** Vidi [HearingRepository.save] za invarijantu cancel-pre-reschedule koja važi i ovde. */
    suspend fun save(deadline: Deadline, reminderMinutesBefore: List<Long>): Long

    suspend fun delete(deadline: Deadline)
}
