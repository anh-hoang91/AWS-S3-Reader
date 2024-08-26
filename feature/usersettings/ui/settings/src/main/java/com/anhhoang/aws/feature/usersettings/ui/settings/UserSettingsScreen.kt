package com.anhhoang.aws.feature.usersettings.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun UserSettingsScreen(
    onNavigateToFileExplorer: () -> Unit,
) {
    val viewModel = hiltViewModel<UserSettingsViewModel>()
    val hasAccess by viewModel.hasAccess.collectAsState()

    UserSettingsScreen(
        hasAccess = hasAccess,
        regions = viewModel.regions,
        onUpdateSettings = viewModel::updateUserSettings,
        isInputValid = viewModel::isValid,
        onNavigateToFileExplorer = onNavigateToFileExplorer
    )
}

@Composable
private fun UserSettingsScreen(
    hasAccess: Boolean,
    regions: List<String>,
    onUpdateSettings: (accessId: String, secretKey: String, bucketName: String, region: String) -> Unit,
    isInputValid: (String) -> Boolean,
    onNavigateToFileExplorer: () -> Unit,
) {
    if (hasAccess) {
        LaunchedEffect(Unit) {
            onNavigateToFileExplorer()
        }
    }

    var accessId by remember { mutableStateOf("") }
    var secretKey by remember { mutableStateOf("") }
    var bucketName by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }

    Scaffold { pv ->
        Column(
            modifier = Modifier
                .padding(pv)
                .padding(16.dp)
                .verticalScroll(state = rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InputField(
                label = stringResource(R.string.access_id),
                placeholder = stringResource(R.string.enter_access_id),
                value = accessId,
                onValueChange = { accessId = it },
                isInputValid = isInputValid
            )
            InputField(
                label = stringResource(R.string.secret_key),
                placeholder = stringResource(R.string.enter_secret_key),
                value = secretKey,
                onValueChange = { secretKey = it },
                isInputValid = isInputValid
            )
            InputField(
                label = stringResource(R.string.bucket_name),
                placeholder = stringResource(R.string.enter_bucket_name),
                value = bucketName,
                onValueChange = { bucketName = it },
                isInputValid = isInputValid
            )

            var expanded by remember { mutableStateOf(false) }
            InputField(
                modifier = Modifier.clickable { expanded = !expanded },
                label = stringResource(R.string.region),
                placeholder = stringResource(R.string.select_region),
                value = region,
                readOnly = true,
                onValueChange = { },
                isInputValid = { true },
                trailingIcon = {
                    val icon =
                        if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp
                    val contentDescription = if (expanded) {
                        stringResource(R.string.collapse_regions)
                    } else {
                        stringResource(R.string.expand_regions)
                    }
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = icon,
                            contentDescription = contentDescription,
                        )
                    }
                }
            )
            DropdownMenu(
                modifier = Modifier.defaultMinSize(minWidth = 68.dp),
                expanded = expanded, onDismissRequest = { expanded = false }) {
                regions.forEach {
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),
                        text = {
                            Text(text = it)
                        }, onClick = {
                            region = it
                            expanded = false
                        })
                }
            }

            val isValid by remember {
                derivedStateOf {
                    isInputValid(accessId) &&
                            isInputValid(secretKey) &&
                            isInputValid(bucketName) &&
                            isInputValid(region)
                }
            }
            Button(
                onClick = { onUpdateSettings(accessId, secretKey, bucketName, region) },
                enabled = isValid
            ) {
                Text(text = stringResource(R.string.save))
            }
        }
    }
}

@Composable
private fun InputField(
    modifier: Modifier = Modifier,
    label: String,
    placeholder: String,
    value: String,
    readOnly: Boolean = false,
    onValueChange: (String) -> Unit,
    isInputValid: (String) -> Boolean,
    trailingIcon: @Composable () -> Unit = {
        if (value.isNotEmpty()) {
            IconButton(onClick = {
                onValueChange("")
            }) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = stringResource(R.string.clear_input)
                )
            }
        }
    },
) {
    var isValid by remember { mutableStateOf(true) }
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = {
            onValueChange(it)
            isValid = isInputValid(it)
        },
        readOnly = readOnly,
        isError = !isValid,
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        supportingText = {
            if (!isValid) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.invalid_input),
                )
            }
        },
        trailingIcon = trailingIcon
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Preview_UserSettingsScreen() {
    UserSettingsScreen(
        hasAccess = true,
        regions = listOf("us-east-1", "us-west-2"),
        onUpdateSettings = { _, _, _, _ -> },
        isInputValid = { true },
        onNavigateToFileExplorer = {}
    )
}