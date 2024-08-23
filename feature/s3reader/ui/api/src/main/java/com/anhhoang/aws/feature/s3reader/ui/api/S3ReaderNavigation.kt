package com.anhhoang.aws.feature.s3reader.ui.api

import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.anhhoang.aws.feature.s3reader.ui.explorer.S3ExplorerScreen
import com.anhhoang.aws.feature.usersettings.ui.api.UserSettingsGraph

/** Compose sub-graphs for the of file explorer screen. */
fun NavGraphBuilder.addS3ReaderNavigation(navController: NavHostController) {
    navigation<S3ReaderGraph>(startDestination = S3FileExplorerDestination()) {
        composable<S3FileExplorerDestination> {
            S3ExplorerScreen(
                onNavigateToLogin = {
                    navController.navigate(UserSettingsGraph) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToFolder = { navController.navigate(S3FileExplorerDestination(it)) },
                onNavigateBack = dropUnlessResumed { navController.navigateUp() }
            )
        }
    }
}