package com.anhhoang.aws.feature.usersettings.ui.impl

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.anhhoang.aws.core.nav.NavSubGraph
import com.anhhoang.aws.feature.s3reader.ui.api.S3ReaderGraphDestination
import com.anhhoang.aws.feature.usersettings.ui.api.UserSettingsDestination
import com.anhhoang.aws.feature.usersettings.ui.api.UserSettingsGraph
import com.anhhoang.aws.feature.usersettings.ui.settings.UserSettingsScreen
import javax.inject.Inject

class UserSettingsNavigation @Inject internal constructor() : NavSubGraph {
    override fun addNavigation(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.navigation<UserSettingsGraph>(startDestination = UserSettingsDestination) {
            composable<UserSettingsDestination> {
                UserSettingsScreen {
                    navController.navigate(S3ReaderGraphDestination) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }
}