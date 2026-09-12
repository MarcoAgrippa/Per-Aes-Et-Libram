package com.peraeslibram.domain.repository

import com.peraeslibram.domain.model.NonWorkingDay
import kotlinx.coroutines.flow.Flow

interface NonWorkingDayRepository {
    fun observeAll(): Flow<List<NonWorkingDay>>
    suspend fun getForYear(year: Int): List<NonWorkingDay>
    suspend fun getSeededYears(): Set<Int>

    /** Ako godina još nije u bazi, generiše je preko HolidaySeedData i upisuje. */
    suspend fun ensureSeededForYear(year: Int)

    suspend fun addManual(day: NonWorkingDay)
    suspend fun delete(day: NonWorkingDay)
}
