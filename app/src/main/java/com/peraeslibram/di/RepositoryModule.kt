package com.peraeslibram.di

import com.peraeslibram.data.repository.CaseRepositoryImpl
import com.peraeslibram.data.repository.CourtRepositoryImpl
import com.peraeslibram.data.repository.DeadlineRepositoryImpl
import com.peraeslibram.data.repository.DeadlineRuleRepositoryImpl
import com.peraeslibram.data.repository.HearingRepositoryImpl
import com.peraeslibram.data.repository.NonWorkingDayRepositoryImpl
import com.peraeslibram.data.repository.NotaryRepositoryImpl
import com.peraeslibram.data.repository.PrilogRepositoryImpl
import com.peraeslibram.domain.repository.CaseRepository
import com.peraeslibram.domain.repository.CourtRepository
import com.peraeslibram.domain.repository.DeadlineRepository
import com.peraeslibram.domain.repository.DeadlineRuleRepository
import com.peraeslibram.domain.repository.HearingRepository
import com.peraeslibram.domain.repository.NonWorkingDayRepository
import com.peraeslibram.domain.repository.NotaryRepository
import com.peraeslibram.domain.repository.PrilogRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCaseRepository(impl: CaseRepositoryImpl): CaseRepository

    @Binds
    @Singleton
    abstract fun bindHearingRepository(impl: HearingRepositoryImpl): HearingRepository

    @Binds
    @Singleton
    abstract fun bindDeadlineRepository(impl: DeadlineRepositoryImpl): DeadlineRepository

    @Binds
    @Singleton
    abstract fun bindNonWorkingDayRepository(impl: NonWorkingDayRepositoryImpl): NonWorkingDayRepository

    @Binds
    @Singleton
    abstract fun bindDeadlineRuleRepository(impl: DeadlineRuleRepositoryImpl): DeadlineRuleRepository

    @Binds
    @Singleton
    abstract fun bindPrilogRepository(impl: PrilogRepositoryImpl): PrilogRepository

    @Binds
    @Singleton
    abstract fun bindCourtRepository(impl: CourtRepositoryImpl): CourtRepository

    @Binds
    @Singleton
    abstract fun bindNotaryRepository(impl: NotaryRepositoryImpl): NotaryRepository
}
