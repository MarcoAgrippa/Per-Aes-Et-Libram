package com.peraeslibram.domain.repository

import com.peraeslibram.domain.model.Prilog
import kotlinx.coroutines.flow.Flow

interface PrilogRepository {
    fun observeByCase(caseId: Long): Flow<List<Prilog>>
    suspend fun getById(id: Long): Prilog?
    suspend fun save(prilog: Prilog): Long
    suspend fun rename(prilog: Prilog, newNaziv: String)

    /** Briše i DB red i fajl na disku — DB CASCADE ne dira fajlove. */
    suspend fun delete(prilog: Prilog)
}
