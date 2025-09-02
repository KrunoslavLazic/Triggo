package com.klazic.triggo.ui.screens.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.klazic.triggo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    name: String,
    avatarRes: Int,
    onSettingsClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_trigo),
                    contentDescription = null,
                )

                Column {
                    Text(
                        stringResource(R.string.hello_name, name),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        stringResource(R.string.ready_for_trigonometry),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Image(
                    painterResource(avatarRes),
                    contentDescription = stringResource(R.string.profile)
                )
            }

        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            scrolledContainerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    )
}
