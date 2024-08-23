package com.anhhoang.aws.feature.usersettings.di

import com.anhhoang.aws.feature.usersettings.api.data.UserSettingsRepository
import com.anhhoang.aws.feature.usersettings.impl.data.UserSettingsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
interface UserSettingsModule {
    @Binds
    fun bindUserSettingsRepository(impl: UserSettingsRepositoryImpl): UserSettingsRepository
}
