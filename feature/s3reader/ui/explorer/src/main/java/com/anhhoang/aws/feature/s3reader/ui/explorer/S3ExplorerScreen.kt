package com.anhhoang.aws.feature.s3reader.ui.explorer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.anhhoang.aws.feature.s3reader.api.data.model.FileData

@Composable
fun S3ExplorerScreen(
    onNavigateToLogin: () -> Unit
) {
    val viewModel = hiltViewModel<S3ExplorerViewModel>()
    val parentKey = viewModel.parentKey
    val files = viewModel.files.collectAsLazyPagingItems()
    val hasAccess by viewModel.hasAccess.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val hasError by viewModel.hasError.collectAsState()

    S3ExplorerScreen(
        parentKey = parentKey ?: "",
        files = files,
        hasAccess = hasAccess,
        isSyncing = isSyncing,
        hasError = hasError,
        onNavigateToLogin = onNavigateToLogin,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun S3ExplorerScreen(
    parentKey: String,
    files: LazyPagingItems<FileData>,
    hasAccess: Boolean,
    isSyncing: Boolean,
    hasError: Boolean,
    onNavigateToLogin: () -> Unit = {},
    onNavigateToFolder: (String) -> Unit = {},
    onSync: () -> Unit = {},
) {
    HandleLoginState(hasAccess, onNavigateToLogin)

    val pullToRefreshState = rememberPullToRefreshState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = parentKey) },
            )
        }
    ) { pv ->

        Box(
            modifier = Modifier
                .padding(pv)
                .fillMaxSize()
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                ErrorView(hasError = hasError, onRetryClick = onSync)
                FilesList(file = files, onItemClicked = onNavigateToFolder)
            }
        }
    }
}

@Composable
fun HandleLoginState(hasAccess: Boolean, onNavigateToLogin: () -> Unit) {
    if (!hasAccess) {
        LaunchedEffect(Unit) {
            onNavigateToLogin()
        }
    }
}