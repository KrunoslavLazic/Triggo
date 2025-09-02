package com.klazic.triggo.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.klazic.triggo.R
import com.klazic.triggo.data.prefs.Lang
import com.klazic.triggo.data.prefs.UserPrefs
import com.klazic.triggo.di.LocalDailyStreak
import com.klazic.triggo.di.LocalProgressStore
import com.klazic.triggo.ui.ThemeOption
import com.klazic.triggo.ui.common.AvatarAssets
import kotlinx.coroutines.launch

@Composable
fun SettingsRoute(
) {
    val context = LocalContext.current
    val vm: SettingsViewModel = viewModel(
        factory = SettingsViewModel.SettingsViewModelFactory(UserPrefs(context))
    )

    val state by vm.state.collectAsStateWithLifecycle()
    val isSaving by vm.saving.collectAsState()

    val progressStore = LocalProgressStore.current
    val streakStore = LocalDailyStreak.current

    val prefs = remember { UserPrefs(context) }
    val language by prefs.language.collectAsStateWithLifecycle("system")


    val scope = rememberCoroutineScope()
    var showResetDialog by rememberSaveable { mutableStateOf(false) }
    var resetting by remember { mutableStateOf(false) }

    SettingsScreen(
        state = state.copy(isSavingName = isSaving),
        language = language,
        onThemeSelected = vm::selectTheme,
        onDynamicToggle = vm::setDynamicColors,
        onLanguageSelected = { value -> scope.launch { prefs.setLanguage(value) } },
        onNameChange = vm::onNameChange,
        onSaveName = vm::saveName,
        onAvatarSelected = vm::setAvatar,
        onResetProgress = { showResetDialog = true }
    )
    if (showResetDialog) {
        ResetProgressDialog(
            onDismiss = { if (!resetting) showResetDialog = false },
            onConfirm = {
                resetting = true
                scope.launch {
                    progressStore.resetAll()
                    streakStore.reset()
                    resetting = false
                    showResetDialog = false
                    Toast.makeText(context,
                        R.string.reset_toast, Toast.LENGTH_SHORT)
                        .show()
                }
            },
            confirming = resetting
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    state: SettingsUiState,
    language: String,
    onThemeSelected: (String) -> Unit,
    onDynamicToggle: (Boolean) -> Unit,
    onLanguageSelected: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onSaveName: () -> Unit,
    onAvatarSelected: (Int) -> Unit,
    onResetProgress: (() -> Unit)? = null,
) {
    val cs = MaterialTheme.colorScheme
    val bg = Brush.verticalGradient(listOf(cs.secondaryContainer, cs.background))

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = cs.secondaryContainer,
                    titleContentColor = cs.onSecondaryContainer
                )
            )
        },
        containerColor = cs.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                SectionCard(
                    title = stringResource(R.string.appearance),
                    subtitle = stringResource(R.string.theme_and_color)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            ThemeOption.SYSTEM,
                            ThemeOption.LIGHT,
                            ThemeOption.DARK
                        ).forEach { t ->
                            FilterChip(
                                selected = state.theme == t,
                                onClick = { onThemeSelected(t) },
                                label = { Text(t.replaceFirstChar { it.uppercase() }) }
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.dynamic), modifier = Modifier.weight(1f))
                        Switch(checked = state.dynamicColors, onCheckedChange = onDynamicToggle)
                    }
                }
            }
            item {
                SectionCard(title = stringResource(R.string.language)) {

                    val options = listOf(
                        Lang.SYSTEM to R.string.language_system,
                        Lang.EN to R.string.language_english,
                        Lang.HR to R.string.language_croatian
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        options.forEach { (value, labelRes) ->
                            FilterChip(
                                selected = language == value,
                                onClick = { onLanguageSelected(value) },
                                label = { Text(stringResource(labelRes)) }
                            )
                        }


                    }
                }
            }

            item {
                SectionCard(
                    title = stringResource(R.string.profile),
                    subtitle = stringResource(R.string.display_name_and_avatar)
                ) {
                    OutlinedTextField(
                        value = state.nameInput,
                        onValueChange = onNameChange,
                        label = { Text(stringResource(R.string.name)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onSaveName, enabled = !state.isSavingName) {
                        Text(
                            if (state.isSavingName) stringResource(R.string.saving) else stringResource(
                                R.string.save_name
                            )
                        )
                    }
                    Spacer(Modifier.height(12.dp))

                    FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AvatarAssets.avatars.forEachIndexed { idx, avatar ->
                            ElevatedAssistChip(
                                leadingIcon = {
                                    Icon(
                                        painterResource(avatar.resId),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                label = { Text(stringResource(avatar.labelRes)) },
                                onClick = { onAvatarSelected(idx) }
                            )
                        }
                    }
                }
            }


            item {
                SectionCard(title = stringResource(R.string.content_data)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.reset_progress))
                        FilledTonalButton(
                            onClick = { onResetProgress?.invoke() },
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = cs.errorContainer)
                        ) { Text(stringResource(R.string.reset), color = cs.onErrorContainer) }
                    }
                }
            }
            item {
                SectionCard(title = stringResource(R.string.about)) {
                    val version = "1.0.0"
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.app_version)) },
                        supportingContent = { Text(version) }
                    )
                }
            }        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        color = cs.surface,
        border = DividerDefaults.color.copy(alpha = 0.4f).let { BorderStroke(1.dp, it) }
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = cs.onSurface)
            if (subtitle != null) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = cs.onSurfaceVariant
                )
            }
            content()
        }
    }
}

@Composable
private fun ResetProgressDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirming: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.reset_progress,"?")) },
        text = { Text(stringResource(R.string.reset_message)) },
        dismissButton = {
            TextButton(
                enabled = !confirming,
                onClick = onDismiss
            ) { Text(stringResource(R.string.cancel)) }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !confirming,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text(if (confirming) stringResource(R.string.resetting) else stringResource(R.string.reset))
            }
        }
    )
}
