package dev.ricknout.rugbyranker.core.ui.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RugbyRankerTopAppBar(
    elevated: Boolean = false,
    onNavigationClick: () -> Unit,
) {
    val elevation by animateDpAsState(
        targetValue = if (elevated) 4.dp else 0.dp,
        label = "Top app bar elevation",
    )
    Surface(
        shadowElevation = elevation,
        tonalElevation = elevation,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(insets = TopAppBarDefaults.windowInsets),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(size = 56.dp)
                        .clickable(onClick = onNavigationClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                )
            }
        }
    }
}
