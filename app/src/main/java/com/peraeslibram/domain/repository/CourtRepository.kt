package com.peraeslibram.domain.repository

import com.peraeslibram.domain.model.Court
import kotlinx.coroutines.flow.Flow

interface CourtRepository {
    fun observeAll(): Flow<List<Court>>
    suspend fun getById(id: Long): Court?
    suspend fun save(court: Court): Long
    suspend fun delete(court: Court)

    /** Ako u bazi još nema nijednog suda, upisuje ugrađeni spisak sudova ([com.peraeslibram.data.local.seed.CourtSeedData]). */
    suspend fun ensureSeeded()
}
