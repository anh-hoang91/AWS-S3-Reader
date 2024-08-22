package com.anhhoang.aws.feature.s3reader.ui.explorer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.anhhoang.aws.feature.s3reader.api.data.model.FileData

@Composable
fun S3ExplorerScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToFolder: (String) -> Unit
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
        onNavigateToFolder = onNavigateToFolder,
        onSync = viewModel::syncFiles,
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
                FilesList(files = files, onItemClicked = onNavigateToFolder)
            }

            SyncView(
                modifier = Modifier.align(Alignment.TopCenter),
                isSyncing = isSyncing,
                refreshState = pullToRefreshState,
                onRefresh = onSync,
            )
        }
    }
}

@Composable
private fun FilesList(files: LazyPagingItems<FileData>, onItemClicked: (String) -> Unit) {
    FilesListSimplified(
        count = files.itemCount,
        key = files.itemKey { it.key },
        getItem = { index ->
            files[index]
        },
        onItemClicked = onItemClicked,
    )
}

/**
 * For preview/development purpose so that we don't have to deal with complexity of Pager library
 * just to display a preview.
 */
@Composable
private fun FilesListSimplified(
    count: Int,
    key: (Int) -> Any,
    getItem: (Int) -> FileData?,
    onItemClicked: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(count = count, key = key) { index ->
            val file = getItem(index)
            if (file != null) {
                FileItem(file = file) {
                    onItemClicked(file.key)
                }
                if (index < count - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun FileItem(file: FileData, onItemClicked: () -> Unit) {
    TODO("Not yet implemented")
}

@Composable
private fun ErrorView(hasError: Boolean, onRetryClick: () -> Unit) {
    if (hasError) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.errorContainer)
                .padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.something_went_wrong),
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            TextButton(onClick = { onRetryClick() }) {
                Text(
                    text = stringResource(R.string.retry),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SyncView(
    modifier: Modifier,
    isSyncing: Boolean,
    refreshState: PullToRefreshState,
    onRefresh: () -> Unit
) {
    if (refreshState.isRefreshing) {
        LaunchedEffect(true) {
            onRefresh()
        }
    }
    LaunchedEffect(isSyncing) {
        if (isSyncing) {
            refreshState.startRefresh()
        } else {
            refreshState.endRefresh()
        }
    }

    PullToRefreshContainer(state = refreshState, modifier = modifier)
}

@Composable
private fun HandleLoginState(hasAccess: Boolean, onNavigateToLogin: () -> Unit) {
    if (!hasAccess) {
        LaunchedEffect(Unit) {
            onNavigateToLogin()
        }
    }
}