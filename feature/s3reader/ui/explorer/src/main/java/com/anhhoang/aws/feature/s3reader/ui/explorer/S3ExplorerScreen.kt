package com.anhhoang.aws.feature.s3reader.ui.explorer

import android.text.format.DateUtils
import android.text.format.Formatter
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.anhhoang.aws.core.ui.theme.S3ReaderTheme
import com.anhhoang.aws.feature.s3reader.api.data.model.FileData
import com.anhhoang.aws.feature.s3reader.api.data.model.FileType
import java.time.Instant
import kotlinx.coroutines.flow.MutableStateFlow

/** UI component to display files and folders. */
@Composable
fun S3ExplorerScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToFolder: (String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val viewModel = hiltViewModel<S3ExplorerViewModel>()
    val parentKey = viewModel.parentKey
    val files = viewModel.files.collectAsLazyPagingItems()
    val hasAccess by viewModel.hasAccess.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val hasError by viewModel.hasError.collectAsState()

    S3ExplorerScreen(
        parentKey = getFileDisplayName(parentKey),
        files = files,
        hasAccess = hasAccess,
        isSyncing = isSyncing,
        hasError = hasError,
        onNavigateToLogin = onNavigateToLogin,
        onNavigateToFolder = onNavigateToFolder,
        onNavigateBack = onNavigateBack,
        onSync = viewModel::syncFiles,
        onLogout = viewModel::logout
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
    onNavigateToLogin: () -> Unit,
    onNavigateToFolder: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onSync: () -> Unit,
    onLogout: () -> Unit,
) {
    HandleLoginState(hasAccess, onNavigateToLogin)

    val pullToRefreshState = rememberPullToRefreshState()
    Scaffold(topBar = {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            },
            title = { Text(text = parentKey, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            actions = {
                if (hasAccess) {
                    IconButton(onClick = onLogout) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_logout_24),
                            contentDescription = stringResource(R.string.logout),
                        )
                    }
                }
            }
        )
    }) { pv ->

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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FilesList(files: LazyPagingItems<FileData>, onItemClicked: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        stickyHeader {
            FileItemHeader()
        }
        item {
            HorizontalDivider()
        }
        items(count = files.itemCount, key = files.itemKey { it.key }) { index ->
            val file = files[index]
            if (file != null) {
                FileItem(file = file) {
                    onItemClicked(file.key)
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun FileItemHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.name),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = stringResource(R.string.size),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun FileItem(file: FileData, onItemClicked: () -> Unit) {
    val context = LocalContext.current
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { if (file.type == FileType.FOLDER) onItemClicked() }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val icon = if (file.type == FileType.FOLDER) {
                painterResource(R.drawable.baseline_folder_24)
            } else {
                painterResource(R.drawable.baseline_file_24)
            }
            val contentDescription = if (file.type == FileType.FOLDER) {
                stringResource(R.string.folder)
            } else {
                stringResource(R.string.file)
            }
            Icon(
                painter = icon,
                contentDescription = contentDescription,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                modifier = Modifier.weight(1f),
                text = getFileDisplayName(file.key),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                modifier = Modifier.weight(.34f),
                text = Formatter.formatFileSize(context, file.size),
                textAlign = TextAlign.End
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            if (file.lastModified != null) {
                Text(
                    modifier = Modifier.weight(1f), text = stringResource(R.string.last_modified)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = DateUtils.getRelativeDateTimeString(
                        context,
                        file.lastModified!!.toEpochMilli(),
                        DateUtils.DAY_IN_MILLIS,
                        DateUtils.WEEK_IN_MILLIS,
                        0
                    ).toString()
                )
            }
        }
    }
}

@Composable
private fun ErrorView(hasError: Boolean, onRetryClick: () -> Unit) {
    if (hasError) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.errorContainer)
                .padding(start = 16.dp), verticalAlignment = Alignment.CenterVertically
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
    modifier: Modifier, isSyncing: Boolean, refreshState: PullToRefreshState, onRefresh: () -> Unit
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
    Log.d("TEST", "hasAccess: $hasAccess")
    if (!hasAccess) {
        LaunchedEffect(Unit) {
            onNavigateToLogin()
        }
    }
}

private fun getFileDisplayName(key: String?): String {
    if (key.isNullOrEmpty()) return ""
    // Folders ends in slash, so it needs to be excluded.
    val lastIndex = key.lastIndexOf("/", startIndex = key.lastIndex - 1)
    return if (lastIndex == -1) {
        key
    } else {
        key.substring(lastIndex + 1)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Preview_S3ExplorerScreen() {
    val files = listOf(parentFile, file, file2)
    val pagingFiles = MutableStateFlow(PagingData.from(files)).collectAsLazyPagingItems()

    S3ReaderTheme {
        S3ExplorerScreen(parentKey = "Folder",
            files = pagingFiles,
            hasAccess = true,
            isSyncing = false,
            hasError = false,
            onNavigateToLogin = {},
            onNavigateToFolder = {},
            onNavigateBack = {},
            onSync = {},
            onLogout = {}
        )
    }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//private fun Preview_FilesList() {
//    val items = listOf(parentFile, file, file2)
//    FilesListSimplified(count = 3, key = { items[it].key }, getItem = { items[it] }) {}
//}

// These variable should be else where, putting them here for convenience
private val parentFile = FileData(
    key = "parent_key", parent = null, size = 12345, lastModified = null, type = FileType.FOLDER
)

private val file = FileData(
    key = "key",
    parent = null,
    size = 728L,
    lastModified = Instant.parse("2024-01-01T00:00:00Z"),
    type = FileType.FILE
)
private val file2 = FileData(
    key = "key2",
    parent = null,
    size = 10240000L,
    lastModified = Instant.parse("2024-01-01T00:00:00Z"),
    type = FileType.FILE
)
