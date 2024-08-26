package com.anhhoang.aws.s3reader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.anhhoang.aws.core.nav.NavSubGraph
import com.anhhoang.aws.core.ui.theme.S3ReaderTheme
import com.anhhoang.aws.feature.s3reader.ui.api.S3ReaderGraphDestination
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navGraphs: Set<@JvmSuppressWildcards NavSubGraph>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            S3ReaderTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = S3ReaderGraphDestination) {
                    navGraphs.forEach { navGraph ->
                        navGraph.addNavigation(
                            navGraphBuilder = this,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
