package com.anhhoang.aws.feature.usersettings.ui.impl

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.anhhoang.aws.core.nav.NavSubGraph
import com.anhhoang.aws.feature.usersettings.ui.api.UserSettingsDestination
import com.anhhoang.aws.feature.usersettings.ui.api.UserSettingsGraph
import javax.inject.Inject

class UserSettingsNavigation @Inject internal constructor() : NavSubGraph {
    override fun addNavigation(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.navigation<UserSettingsGraph>(startDestination = UserSettingsDestination) {
            composable<UserSettingsDestination> {

            }
        }
    }
}