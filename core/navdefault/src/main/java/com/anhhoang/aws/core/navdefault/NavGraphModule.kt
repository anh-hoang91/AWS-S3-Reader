package com.anhhoang.aws.core.navdefault

import com.anhhoang.aws.core.nav.NavSubGraph
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet

@InstallIn(SingletonComponent::class)
@Module
object NavGraphModule {

    @Provides
    @ElementsIntoSet
    fun provideNavGraphSet(): Set<NavSubGraph> = setOf()
}