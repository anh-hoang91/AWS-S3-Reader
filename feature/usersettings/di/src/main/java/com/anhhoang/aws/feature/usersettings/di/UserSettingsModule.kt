package com.anhhoang.aws.feature.usersettings.di

import com.anhhoang.aws.core.nav.NavSubGraph
import com.anhhoang.aws.feature.usersettings.api.data.UserSettingsRepository
import com.anhhoang.aws.feature.usersettings.impl.data.UserSettingsRepositoryImpl
import com.anhhoang.aws.feature.usersettings.ui.impl.UserSettingsNavigation
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.IntoSet

@InstallIn(SingletonComponent::class)
@Module
interface UserSettingsModule {
    @Binds
    fun bindUserSettingsRepository(impl: UserSettingsRepositoryImpl): UserSettingsRepository

    @Binds
    @IntoSet
    fun bindNavGraph(impl: UserSettingsNavigation): NavSubGraph
}
