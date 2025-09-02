package com.klazic.triggo.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.klazic.triggo.R
import com.klazic.triggo.data.prefs.UserPrefs
import com.klazic.triggo.data.progress.Progress
import com.klazic.triggo.data.quiz.Difficulty
import com.klazic.triggo.di.LocalProgressStore
import com.klazic.triggo.di.LocalQuizRepository
import com.klazic.triggo.ui.common.AvatarAssets
import com.klazic.triggo.ui.common.GlobalProgressHeader
import com.klazic.triggo.ui.screens.main.components.CategoryGrid
import com.klazic.triggo.ui.screens.main.components.MainTopBar


@Composable
fun MainRoute(
    onSettingsClick: () -> Unit = {},
    onCategoryClick: (String, Difficulty) -> Unit = { _, _ -> }
) {

    val context = LocalContext.current
    val repo = LocalQuizRepository.current
    val store = LocalProgressStore.current

    val vm: MainViewModel = viewModel(
        factory = MainViewModelFactory(
            prefs = UserPrefs(context),
            repo = repo,
            store = store
        )
    )

    val header by vm.header.collectAsStateWithLifecycle()
    val ui by vm.ui.collectAsStateWithLifecycle()
    val progress by vm.globalProgress.collectAsStateWithLifecycle()

    val avatarRes = AvatarAssets.at(header.avatarIndex)


    MainScreen(
        name = header.name,
        avatarRes = avatarRes,
        ui = ui,
        progress = progress,
        onSettingsClick = onSettingsClick,
        onCategoryClick = onCategoryClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    name: String,
    avatarRes: Int,
    ui: MainContract.UiState,
    progress: Progress,
    onSettingsClick: () -> Unit,
    onCategoryClick: (String, Difficulty) -> Unit
) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MainTopBar(
                name = name,
                avatarRes = avatarRes,
                onSettingsClick = onSettingsClick,
                scrollBehavior = scrollBehavior
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.secondaryContainer,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GlobalProgressHeader(progress)

            Text(stringResource(R.string.lessons), style = MaterialTheme.typography.titleMedium)

            Box(modifier = Modifier.weight(1f)) {
                CategoryGrid(
                    items = ui.categories,
                    onClick = onCategoryClick,
                    continueTarget = ui.continueTarget
                )

            }
        }
    }
}







