package dev.ricknout.rugbyranker.news.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import dev.ricknout.rugbyranker.core.ui.component.RugbyRankerCard
import dev.ricknout.rugbyranker.core.ui.preview.UIModePreviews
import dev.ricknout.rugbyranker.core.ui.theme.RugbyRankerTheme
import dev.ricknout.rugbyranker.core.util.DateUtils
import dev.ricknout.rugbyranker.news.R
import dev.ricknout.rugbyranker.news.model.News
import dev.ricknout.rugbyranker.news.model.Type
import dev.ricknout.rugbyranker.core.ui.component.RugbyRankerButton
import dev.ricknout.rugbyranker.core.ui.component.RugbyRankerCircularProgressIndicator
import dev.ricknout.rugbyranker.core.ui.component.RugbyRankerSnackbar
import dev.ricknout.rugbyranker.core.ui.component.RugbyRankerTopAppBar
import kotlin.math.floor

@Composable
fun News(
    news: LazyPagingItems<News>,
    onItemClick: (news: News) -> Unit,
    onNavigationClick: () -> Unit,
) {
    // TODO: Add pull to refresh after issues are fixed:
    // https://issuetracker.google.com/issues/317177684
    // https://issuetracker.google.com/issues/317177683
    val lazyGridState = rememberLazyGridState()
    val isAtTop by remember {
        derivedStateOf {
            lazyGridState.firstVisibleItemIndex == 0 && lazyGridState.firstVisibleItemScrollOffset == 0
        }
    }
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        topBar = {
            RugbyRankerTopAppBar(
                elevated = !isAtTop,
                onNavigationClick = onNavigationClick,
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData ->  
                    RugbyRankerSnackbar(snackbarData = snackbarData)
                }
            )
        },
    ) { contentPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            NewsContent(
                news = news,
                lazyGridState = lazyGridState,
                contentPadding = contentPadding,
                onItemClick = onItemClick,
            )
            when (news.loadState.refresh) {
                is LoadState.Loading -> {
                    if (news.itemCount == 0) {
                        NewsProgress(contentPadding = contentPadding)
                    }
                }
                is LoadState.NotLoading -> {
                    // Nothing to do here, yet
                }
                is LoadState.Error -> {
                    if (news.itemCount == 0) {
                        NewsError(
                            contentPadding = contentPadding,
                            onRetryClick = { news.retry() },
                        )
                    } else {
                        val errorMessage = stringResource(id = R.string.failed_to_refresh_news)
                        LaunchedEffect(key1 = news.loadState.refresh) {
                            snackbarHostState.showSnackbar(message = errorMessage)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NewsContent(
    news: LazyPagingItems<News>,
    lazyGridState: LazyGridState,
    contentPadding: PaddingValues,
    onItemClick: (news: News) -> Unit,
) {
    val spanCount = integerResource(id = R.integer.span_count_grid)
    LazyVerticalGrid(
        columns = GridCells.Fixed(count = spanCount),
        modifier = Modifier.fillMaxSize(),
        state = lazyGridState,
        contentPadding = contentPadding,
    ) {
        items(
            count = news.itemCount,
            key = news.itemKey { it.id },
            contentType = news.itemContentType { "News" },
        ) { index ->
            val row = floor(index / spanCount.toFloat()).toInt()
            val evenSpanCount = spanCount % 2 == 0
            val evenRow = row % 2 == 0
            val evenIndex = index % 2 == 0
            val alternate =
                ((!evenSpanCount || evenRow) && evenIndex) || ((evenSpanCount && !evenRow) && !evenIndex)
            val containerColor = if (alternate) {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
            } else {
                MaterialTheme.colorScheme.surface
            }
            val item = news[index]
            if (item != null) {
                NewsItem(
                    news = item,
                    onClick = { onItemClick(item) },
                    containerColor = containerColor,
                )
            }
        }
    }
}

@Composable
private fun NewsProgress(contentPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        RugbyRankerCircularProgressIndicator()
    }
}

@Composable
private fun NewsError(
    contentPadding: PaddingValues,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = contentPadding)
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_error),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        )
        Spacer(modifier = Modifier.height(height = 16.dp))
        RugbyRankerButton(onClick = onRetryClick) {
            Text(text = stringResource(id = R.string.retry))
        }
    }
}

@Composable
private fun NewsItem(
    news: News,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
) {
    RugbyRankerCard(
        onClick = onClick,
        modifier = modifier,
        containerColor = containerColor
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(id = R.drawable.ic_image_24dp),
                contentDescription = null,
                tint = LocalContentColor.current.copy(alpha = 0.6f),
            )
            AsyncImage(
                model = news.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = 224.dp),
                contentScale = ContentScale.Crop,
            )
        }
        Column(
            modifier = Modifier.padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(space = 16.dp),
        ) {
            if (news.subtitle != null) {
                Text(
                    text = news.subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                text = news.title,
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = news.summary,
                style = MaterialTheme.typography.bodyLarge,
            )
            val isCurrentDay = DateUtils.isDayCurrentDay(news.timeMillis)
            val label = if (isCurrentDay) {
                stringResource(id = R.string.today)
            } else {
                DateUtils.getDate(DateUtils.DATE_FORMAT_D_MMM_YYYY, news.timeMillis)
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = LocalContentColor.current.copy(alpha = 0.6f),
            )
        }
    }
}

@Composable
@UIModePreviews
private fun NewsItemPreview() {
    val news = News(
        id = "123",
        type = Type.TEXT,
        title = "News title",
        subtitle = "News subtitle",
        summary = "News summary",
        imageUrl = "https://resources.world.rugby/photo-resources/2020/05/13/89d97b1c-cc16-4b90-9814-1138a3921fce/2020-WR_mark_generic.jpg?width=1000",
        articleUrl = "https://world.rugby/news",
        timeMillis = 1699635600000,
        language = "en",
    )
    RugbyRankerTheme {
        NewsItem(
            news = news,
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
