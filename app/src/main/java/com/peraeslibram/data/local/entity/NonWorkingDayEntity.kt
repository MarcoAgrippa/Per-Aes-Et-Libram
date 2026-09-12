package com.peraeslibram.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.peraeslibram.domain.model.NonWorkingDay
import com.peraeslibram.domain.model.NonWorkingDaySource
import com.peraeslibram.domain.model.NonWorkingDayType
import java.time.LocalDate

@Entity(
    tableName = "non_working_days",
    indices = [Index(value = ["datum"], unique = true)]
)
data class NonWorkingDayEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val datum: LocalDate,
    val godina: Int,
    val naziv: String,
    val tip: NonWorkingDayType,
    val izvor: NonWorkingDaySource
)

fun NonWorkingDayEntity.toDomain() = NonWorkingDay(
    id = id,
    datum = datum,
    naziv = naziv,
    tip = tip,
    izvor = izvor
)

fun NonWorkingDay.toEntity() = NonWorkingDayEntity(
    id = id,
    datum = datum,
    godina = datum.year,
    naziv = naziv,
    tip = tip,
    izvor = izvor
)
