package com.anhhoang.aws.feature.usersettings.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.regions.Regions
import com.anhhoang.aws.feature.s3reader.work.SyncService
import com.anhhoang.aws.feature.usersettings.api.data.UserSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class UserSettingsViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository,
    private val syncService: SyncService,
) : ViewModel() {
    val hasAccess = userSettingsRepository.hasAccessFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = false,
    )

    val regions = Regions.entries.map { it.getName() }

    fun updateUserSettings(
        accessId: String,
        secretKey: String,
        bucketName: String,
        region: String,
    ) {
        if (accessId.isEmpty() || accessId.isBlank() ||
            secretKey.isEmpty() || secretKey.isBlank() ||
            bucketName.isEmpty() || bucketName.isBlank()
        ) return

        // Just assumption that user has input correctly everything.
        viewModelScope.launch(NonCancellable) {
            userSettingsRepository.saveUserSettings(accessId, secretKey, bucketName, region)
            syncService.startOneTimeWork()
        }
    }

    fun isValid(input: String): Boolean = input.isNotEmpty() && input.isNotBlank()
}