package com.peraeslibram.domain.repository

import com.peraeslibram.domain.model.Case
import kotlinx.coroutines.flow.Flow

interface CaseRepository {
    fun observeAll(): Flow<List<Case>>
    fun observeById(id: Long): Flow<Case?>
    suspend fun getById(id: Long): Case?
    suspend fun save(case: Case): Long
    suspend fun delete(case: Case)
}
