package com.peraeslibram.data.repository

import com.peraeslibram.data.local.dao.NonWorkingDayDao
import com.peraeslibram.data.local.entity.toDomain
import com.peraeslibram.data.local.entity.toEntity
import com.peraeslibram.data.local.seed.HolidaySeedData
import com.peraeslibram.domain.model.NonWorkingDay
import com.peraeslibram.domain.repository.NonWorkingDayRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NonWorkingDayRepositoryImpl @Inject constructor(
    private val dao: NonWorkingDayDao
) : NonWorkingDayRepository {

    override fun observeAll(): Flow<List<NonWorkingDay>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getForYear(year: Int): List<NonWorkingDay> =
        dao.getForYear(year).map { it.toDomain() }

    override suspend fun getSeededYears(): Set<Int> = dao.getSeededYears().toSet()

    override suspend fun ensureSeededForYear(year: Int) {
        if (year !in getSeededYears()) {
            dao.insertAll(HolidaySeedData.forYear(year).map { it.toEntity() })
        }
    }

    override suspend fun addManual(day: NonWorkingDay) {
        dao.insert(day.toEntity())
    }

    override suspend fun delete(day: NonWorkingDay) {
        dao.delete(day.toEntity())
    }
}
