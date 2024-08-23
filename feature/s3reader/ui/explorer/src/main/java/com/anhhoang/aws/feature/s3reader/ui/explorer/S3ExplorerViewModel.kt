package com.anhhoang.aws.feature.s3reader.ui.explorer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.work.WorkInfo
import com.anhhoang.aws.feature.s3reader.api.data.FileRepository
import com.anhhoang.aws.feature.s3reader.work.SyncService
import com.anhhoang.aws.feature.usersettings.api.data.UserSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
internal class S3ExplorerViewModel @Inject constructor(
    private val syncService: SyncService,
    private val fileRepository: FileRepository,
    private val userSettingsRepository: UserSettingsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val parentKey: String? = savedStateHandle["parentKey"]

    private val syncInfo = merge(
        syncService.getExistingOneTimeWorkResourceState(),
        syncService.getExistingPeriodicWorkResourceState(),
    )

    val hasAccess = userSettingsRepository.hasAccessFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = true,
        )

    val isSyncing = syncInfo.map {
        it?.state == WorkInfo.State.RUNNING
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = true,
    )

    val hasError = syncInfo.map {
        it?.state == WorkInfo.State.FAILED || it?.state == WorkInfo.State.CANCELLED || it?.state == WorkInfo.State.BLOCKED
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = false,
    )

    val files = fileRepository.getFiles(parentKey).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = PagingData.empty()
    )

    fun syncFiles() {
        syncService.startOneTimeWork()
    }

    fun logout() {
        viewModelScope.launch {
            fileRepository.deleteAllFiles()
            userSettingsRepository.clearUserSettings()
        }
    }
}