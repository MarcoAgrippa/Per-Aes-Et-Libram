package com.peraeslibram.data.local.converters

import androidx.room.TypeConverter
import com.peraeslibram.domain.model.CaseStatus
import com.peraeslibram.domain.model.DataSource
import com.peraeslibram.domain.model.DeadlineStatus
import com.peraeslibram.domain.model.HearingStatus
import com.peraeslibram.domain.model.NonWorkingDaySource
import com.peraeslibram.domain.model.NonWorkingDayType
import com.peraeslibram.domain.model.TipPostupka
import com.peraeslibram.domain.model.TipRadnje
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class Converters {

    @TypeConverter
    fun epochDayToLocalDate(value: Long?): LocalDate? = value?.let(LocalDate::ofEpochDay)

    @TypeConverter
    fun localDateToEpochDay(date: LocalDate?): Long? = date?.toEpochDay()

    @TypeConverter
    fun epochMillisToInstant(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun instantToEpochMillis(instant: Instant?): Long? = instant?.toEpochMilli()

    @TypeConverter
    fun epochMillisToLocalDateTime(value: Long?): LocalDateTime? =
        value?.let { LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()) }

    @TypeConverter
    fun localDateTimeToEpochMillis(dateTime: LocalDateTime?): Long? =
        dateTime?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()

    @TypeConverter
    fun toTipPostupka(value: String?): TipPostupka? = value?.let(TipPostupka::valueOf)

    @TypeConverter
    fun fromTipPostupka(value: TipPostupka?): String? = value?.name

    @TypeConverter
    fun toTipRadnje(value: String): TipRadnje = TipRadnje.valueOf(value)

    @TypeConverter
    fun fromTipRadnje(value: TipRadnje): String = value.name

    @TypeConverter
    fun toCaseStatus(value: String): CaseStatus = CaseStatus.valueOf(value)

    @TypeConverter
    fun fromCaseStatus(value: CaseStatus): String = value.name

    @TypeConverter
    fun toHearingStatus(value: String): HearingStatus = HearingStatus.valueOf(value)

    @TypeConverter
    fun fromHearingStatus(value: HearingStatus): String = value.name

    @TypeConverter
    fun toDeadlineStatus(value: String): DeadlineStatus = DeadlineStatus.valueOf(value)

    @TypeConverter
    fun fromDeadlineStatus(value: DeadlineStatus): String = value.name

    @TypeConverter
    fun toDataSource(value: String): DataSource = DataSource.valueOf(value)

    @TypeConverter
    fun fromDataSource(value: DataSource): String = value.name

    @TypeConverter
    fun toNonWorkingDayType(value: String): NonWorkingDayType = NonWorkingDayType.valueOf(value)

    @TypeConverter
    fun fromNonWorkingDayType(value: NonWorkingDayType): String = value.name

    @TypeConverter
    fun toNonWorkingDaySource(value: String): NonWorkingDaySource = NonWorkingDaySource.valueOf(value)

    @TypeConverter
    fun fromNonWorkingDaySource(value: NonWorkingDaySource): String = value.name
}
