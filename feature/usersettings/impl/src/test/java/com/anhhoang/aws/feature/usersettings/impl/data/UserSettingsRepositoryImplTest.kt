package com.anhhoang.aws.feature.usersettings.impl.data

import app.cash.turbine.test
import com.anhhoang.aws.feature.usersettings.api.data.UserSettings
import com.anhhoang.aws.feature.usersettings.impl.local.UserSettingsLocalDataSource
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

/** Tests for [UserSettingsRepositoryImpl]. */
class UserSettingsRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private val localDataSource = mockk<UserSettingsLocalDataSource>(relaxed = true)

    private val sut = UserSettingsRepositoryImpl(
        coroutineContext = testDispatcher, localDataSource = localDataSource
    )

    @Test
    fun `saveUserSettings(), verify user settings are saved`() = runTest(testDispatcher) {
        sut.saveUserSettings("test", "test", "test", "test")

        coVerify(exactly = 1) {
            localDataSource.saveUserSettings(
                UserSettings(
                    "test", "test", "test", "test"
                )
            )
        }
    }

    @Test
    fun `clearUserSettings(), verify clear user settings invoked`() = runTest(testDispatcher) {
        sut.clearUserSettings()

        coVerify(exactly = 1) { localDataSource.clearUserSettings() }
    }

    @Test
    fun `getUserSettings(), verify get user settings invoked`() = runTest(testDispatcher) {
        coEvery { localDataSource.getUserSettings() } returns UserSettings(
            "test", "test", "test", "test"
        )

        val result = sut.getUserSettings()

        assertThat(result).isEqualTo(
            UserSettings(
                "test", "test", "test", "test"
            )
        )
    }

    @Test
    fun `getUserSettingsFlow(), verify get user settings flow returned`() =
        runTest(testDispatcher) {
            val userSettings = UserSettings("test", "test", "test", "test")
            coEvery { localDataSource.getUserSettingsFlow() } returns flowOf(userSettings)

            sut.getUserSettingsFlow().test {
                assertThat(awaitItem()).isEqualTo(userSettings)
                awaitComplete()
            }
        }


    @Test
    fun `hasAccess(), returns true if user settings are available`() = runTest(testDispatcher) {
        coEvery { localDataSource.getUserSettings() } returns UserSettings(
            "test", "test", "test", "test"
        )

        val result = sut.hasAccess()

        assertThat(result).isTrue()
    }

    @Test
    fun `hasAccess(), returns false if user settings are not available`() =
        runTest(testDispatcher) {
            coEvery { localDataSource.getUserSettings() } returns UserSettings()

            val result = sut.hasAccess()

            assertThat(result).isFalse()
        }
}