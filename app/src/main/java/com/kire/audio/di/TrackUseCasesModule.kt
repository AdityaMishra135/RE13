package com.kire.audio.di

import com.kire.audio.domain.use_case.util.ITrackUseCases
import com.kire.audio.domain.use_case.util.TrackUseCases
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TrackUseCasesModule {

    @Binds
    @Singleton
    abstract fun provideTrackUseCases(trackUseCases: TrackUseCases): ITrackUseCases
}