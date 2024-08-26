package com.anhhoang.aws.core.nav

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

interface NavSubGraph {
    fun addNavigation(navGraphBuilder: NavGraphBuilder, navController: NavHostController)
}