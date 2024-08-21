package com.anhhoang.aws.feature.s3reader.ui.api

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation


/** Compose sub-graphs for the of file explorer screen. */
fun NavGraphBuilder.addS3ReaderNavigation(navController: NavHostController) {
    navigation<S3ReaderGraph>(startDestination = S3FileExplorerDestination()) {
        composable<S3FileExplorerDestination> { backStackEntry -> }
        composable<SettingsDestination> { backStackEntry -> }
    }
}