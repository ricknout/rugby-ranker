package dev.ricknout.rugbyranker.news.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import dev.ricknout.rugbyranker.core.ui.openDrawer
import dev.ricknout.rugbyranker.core.ui.theme.RugbyRankerTheme
import dev.ricknout.rugbyranker.core.util.CustomTabUtils
import dev.ricknout.rugbyranker.news.model.Type
import dev.ricknout.rugbyranker.theme.ui.ThemeViewModel
import dev.ricknout.rugbyranker.theme.util.getCustomTabsIntentColorScheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class NewsFragment : Fragment() {
    private val args: NewsFragmentArgs by navArgs()

    private val type: Type by lazy { args.type }

    private val newsViewModel: NewsViewModel by lazy {
        when (type) {
            Type.TEXT -> activityViewModels<TextNewsViewModel>().value
        }
    }

    private val themeViewModel: ThemeViewModel by activityViewModels()

    private val transitionDuration by lazy {
        resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply { duration = transitionDuration }
        exitTransition = MaterialFadeThrough().apply { duration = transitionDuration }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            RugbyRankerTheme {
                val news = newsViewModel.news.collectAsLazyPagingItems()
                News(
                    news = news,
                    onItemClick = { item ->
                        lifecycleScope.launch {
                            val theme = themeViewModel.theme.first()
                            withContext(Dispatchers.Main) {
                                CustomTabUtils.launchCustomTab(
                                    requireContext(),
                                    item.articleUrl,
                                    theme.getCustomTabsIntentColorScheme(),
                                )
                            }
                        }
                    },
                    onNavigationClick = {
                        openDrawer()
                    },
                )
            }
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }
}
