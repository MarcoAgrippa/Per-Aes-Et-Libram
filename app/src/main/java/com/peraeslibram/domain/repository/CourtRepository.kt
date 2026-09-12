package com.peraeslibram.domain.repository

import com.peraeslibram.domain.model.Court
import kotlinx.coroutines.flow.Flow

interface CourtRepository {
    fun observeAll(): Flow<List<Court>>
    suspend fun getById(id: Long): Court?
    suspend fun save(court: Court): Long
    suspend fun delete(court: Court)
}
