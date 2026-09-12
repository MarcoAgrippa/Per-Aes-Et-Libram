package com.peraeslibram.domain.model

import java.time.LocalDate

data class NonWorkingDay(
    val id: Long = 0,
    val datum: LocalDate,
    val naziv: String,
    val tip: NonWorkingDayType,
    val izvor: NonWorkingDaySource = NonWorkingDaySource.AUTO_SEED
)
