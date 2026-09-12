package com.peraeslibram.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.peraeslibram.data.local.AppDatabase
import com.peraeslibram.data.local.dao.CaseDao
import com.peraeslibram.data.local.dao.CourtDao
import com.peraeslibram.data.local.dao.DeadlineDao
import com.peraeslibram.data.local.dao.DeadlineRuleDao
import com.peraeslibram.data.local.dao.HearingDao
import com.peraeslibram.data.local.dao.NonWorkingDayDao
import com.peraeslibram.data.local.dao.PrilogDao
import com.peraeslibram.data.local.dao.ReminderDao
import com.peraeslibram.data.local.migration.MIGRATION_1_2
import com.peraeslibram.data.local.migration.MIGRATION_2_3
import com.peraeslibram.data.local.seed.DatabaseSeeder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        seederProvider: Provider<DatabaseSeeder>
    ): AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
        .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                seederProvider.get().seedOnCreate()
            }
        })
        .build()

    @Provides
    fun provideCaseDao(db: AppDatabase): CaseDao = db.caseDao()

    @Provides
    fun provideHearingDao(db: AppDatabase): HearingDao = db.hearingDao()

    @Provides
    fun provideDeadlineDao(db: AppDatabase): DeadlineDao = db.deadlineDao()

    @Provides
    fun provideDeadlineRuleDao(db: AppDatabase): DeadlineRuleDao = db.deadlineRuleDao()

    @Provides
    fun provideNonWorkingDayDao(db: AppDatabase): NonWorkingDayDao = db.nonWorkingDayDao()

    @Provides
    fun provideReminderDao(db: AppDatabase): ReminderDao = db.reminderDao()

    @Provides
    fun providePrilogDao(db: AppDatabase): PrilogDao = db.prilogDao()

    @Provides
    fun provideCourtDao(db: AppDatabase): CourtDao = db.courtDao()
}
