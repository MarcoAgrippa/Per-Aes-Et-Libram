package com.peraeslibram.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.peraeslibram.data.local.converters.Converters
import com.peraeslibram.data.local.dao.CaseDao
import com.peraeslibram.data.local.dao.DeadlineDao
import com.peraeslibram.data.local.dao.DeadlineRuleDao
import com.peraeslibram.data.local.dao.HearingDao
import com.peraeslibram.data.local.dao.NonWorkingDayDao
import com.peraeslibram.data.local.dao.PrilogDao
import com.peraeslibram.data.local.dao.ReminderDao
import com.peraeslibram.data.local.entity.CaseEntity
import com.peraeslibram.data.local.entity.DeadlineEntity
import com.peraeslibram.data.local.entity.DeadlineRuleEntity
import com.peraeslibram.data.local.entity.HearingEntity
import com.peraeslibram.data.local.entity.NonWorkingDayEntity
import com.peraeslibram.data.local.entity.PrilogEntity
import com.peraeslibram.data.local.entity.ReminderEntity

@Database(
    entities = [
        CaseEntity::class,
        HearingEntity::class,
        DeadlineEntity::class,
        DeadlineRuleEntity::class,
        NonWorkingDayEntity::class,
        ReminderEntity::class,
        PrilogEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun caseDao(): CaseDao
    abstract fun hearingDao(): HearingDao
    abstract fun deadlineDao(): DeadlineDao
    abstract fun deadlineRuleDao(): DeadlineRuleDao
    abstract fun nonWorkingDayDao(): NonWorkingDayDao
    abstract fun reminderDao(): ReminderDao
    abstract fun prilogDao(): PrilogDao

    companion object {
        const val DATABASE_NAME = "per_aes_et_libram.db"
    }
}
