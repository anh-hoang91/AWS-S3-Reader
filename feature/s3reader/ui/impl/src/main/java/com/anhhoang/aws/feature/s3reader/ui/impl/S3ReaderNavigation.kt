package com.anhhoang.aws.feature.s3reader.ui.impl

import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.anhhoang.aws.core.nav.NavSubGraph
import com.anhhoang.aws.feature.s3reader.ui.api.S3FileExplorerDestination
import com.anhhoang.aws.feature.s3reader.ui.api.S3ReaderGraphDestination
import com.anhhoang.aws.feature.s3reader.ui.explorer.S3ExplorerScreen
import com.anhhoang.aws.feature.usersettings.ui.api.UserSettingsGraph
import javax.inject.Inject

/** Navigation sub-graph for the of file explorer screen. */
class S3ReaderNavigation @Inject internal constructor() : NavSubGraph {
    override fun addNavigation(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.navigation<S3ReaderGraphDestination>(startDestination = S3FileExplorerDestination()) {
            composable<S3FileExplorerDestination> {
                S3ExplorerScreen(
                    onNavigateToLogin = {
                        navController.navigate(UserSettingsGraph) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                    onNavigateToFolder = { navController.navigate(S3FileExplorerDestination(it)) },
                    onNavigateBack = dropUnlessResumed { navController.navigateUp() }
                )
            }
        }
    }
}