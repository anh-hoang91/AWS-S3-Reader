package com.anhhoang.aws.s3reader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.anhhoang.aws.core.ui.theme.S3ReaderTheme
import com.anhhoang.aws.feature.s3reader.ui.api.S3ReaderGraph
import com.anhhoang.aws.feature.s3reader.ui.api.addS3ReaderNavigation
import com.anhhoang.aws.feature.usersettings.ui.api.addUserSettingsNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            S3ReaderTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = S3ReaderGraph) {
                    addS3ReaderNavigation(navController)
                    addUserSettingsNavigation(navController)
                }
            }
        }
    }
}
