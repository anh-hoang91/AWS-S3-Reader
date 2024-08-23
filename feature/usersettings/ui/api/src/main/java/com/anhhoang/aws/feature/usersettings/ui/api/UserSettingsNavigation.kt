package com.anhhoang.aws.feature.usersettings.ui.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation


/** Compose sub-graphs for the of User settings. */
fun NavGraphBuilder.addUserSettingsNavigation(navController: NavHostController) {
    navigation<UserSettingsGraph>(startDestination = UserSettingsDestination) {
        composable<UserSettingsDestination> {

        }
    }
}