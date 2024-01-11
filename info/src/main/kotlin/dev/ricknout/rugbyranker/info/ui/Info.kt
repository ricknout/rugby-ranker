package dev.ricknout.rugbyranker.info.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.ricknout.rugbyranker.core.ui.component.RugbyRankerButton
import dev.ricknout.rugbyranker.core.ui.component.RugbyRankerTopAppBar
import dev.ricknout.rugbyranker.core.ui.preview.UIModePreviews
import dev.ricknout.rugbyranker.core.ui.theme.RugbyRankerTheme
import dev.ricknout.rugbyranker.info.R

@Composable
fun Info(
    version: String,
    onHowAreRankingsCalculatedClick: () -> Unit,
    onShareThisAppClick: () -> Unit,
    onViewOnGooglePlayClick: () -> Unit,
    onViewSourceCodeClick: () -> Unit,
    onOpenSourceLicensesClick: () -> Unit,
    onChooseThemeClick: () -> Unit,
    onNavigationClick: () -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val isAtTop by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0
        }
    }
    Scaffold(
        topBar = {
            RugbyRankerTopAppBar(
                elevated = !isAtTop,
                onNavigationClick = onNavigationClick,
            )
        },
    ) { contentPadding ->
        InfoContent(
            lazyListState = lazyListState,
            contentPadding = contentPadding,
            version = version,
            onHowAreRankingsCalculatedClick = onHowAreRankingsCalculatedClick,
            onShareThisAppClick = onShareThisAppClick,
            onViewOnGooglePlayClick = onViewOnGooglePlayClick,
            onViewSourceCodeClick = onViewSourceCodeClick,
            onOpenSourceLicensesClick = onOpenSourceLicensesClick,
            onChooseThemeClick = onChooseThemeClick,
        )
    }
}

@Composable
private fun InfoContent(
    lazyListState: LazyListState,
    contentPadding: PaddingValues,
    version: String,
    onHowAreRankingsCalculatedClick: () -> Unit,
    onShareThisAppClick: () -> Unit,
    onViewOnGooglePlayClick: () -> Unit,
    onViewSourceCodeClick: () -> Unit,
    onOpenSourceLicensesClick: () -> Unit,
    onChooseThemeClick: () -> Unit,
) {
    LazyColumn(
        state = lazyListState,
        contentPadding = contentPadding,
    ) {
        item {
            InfoItem(
                title = stringResource(id = R.string.how_are_rankings_calculated),
                onClick = onHowAreRankingsCalculatedClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            InfoItem(
                title = stringResource(id = R.string.share_this_app),
                onClick = onShareThisAppClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            InfoItem(
                title = stringResource(id = R.string.view_on_google_play),
                onClick = onViewOnGooglePlayClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            InfoItem(
                title = stringResource(id = R.string.view_source_code),
                onClick = onViewSourceCodeClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            InfoItem(
                title = stringResource(id = R.string.open_source_licenses),
                onClick = onOpenSourceLicensesClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            InfoItem(
                title = stringResource(id = R.string.choose_theme),
                onClick = onChooseThemeClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            InfoVersion(
                version = version,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun InfoItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RugbyRankerButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Text(
            text = title,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun InfoVersion(
    version: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .sizeIn(minWidth = 56.dp, minHeight = 56.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = version,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
    }
}

@Composable
@UIModePreviews
private fun InfoItemPreview() {
    val title = "Info title"
    RugbyRankerTheme {
        InfoItem(
            title = title,
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
