package com.anhhoang.aws.feature.s3reader.ui.api

import android.util.Log
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.navigation
import com.anhhoang.aws.feature.s3reader.ui.explorer.S3ExplorerScreen


/** Compose sub-graphs for the of file explorer screen. */
fun NavGraphBuilder.addS3ReaderNavigation(navController: NavHostController) {
    navigation<S3ReaderGraph>(startDestination = S3FileExplorerDestination()) {
        composable<S3FileExplorerDestination> {
            S3ExplorerScreen(
                onNavigateToLogin = {
                    navController.navigate(SettingsDestination) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToFolder = { navController.navigate(S3FileExplorerDestination(it)) },
                onNavigateBack = dropUnlessResumed { navController.navigateUp() }
            )
        }
        composable<SettingsDestination> {
        }
    }
}