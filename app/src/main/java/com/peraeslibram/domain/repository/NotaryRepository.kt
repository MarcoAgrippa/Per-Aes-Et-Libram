package com.peraeslibram.domain.repository

import com.peraeslibram.domain.model.Notary
import kotlinx.coroutines.flow.Flow

interface NotaryRepository {
    fun observeAll(): Flow<List<Notary>>
    suspend fun getById(id: Long): Notary?
    suspend fun save(notary: Notary): Long
    suspend fun delete(notary: Notary)

    /** Ako u bazi još nema nijednog beležnika, upisuje ugrađeni spisak ([com.peraeslibram.data.local.seed.NotarySeedData]). */
    suspend fun ensureSeeded()
}
