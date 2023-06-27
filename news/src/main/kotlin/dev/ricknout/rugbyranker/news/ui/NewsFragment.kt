package dev.ricknout.rugbyranker.news.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import com.google.android.material.color.MaterialColors
import com.google.android.material.elevation.ElevationOverlayProvider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import dev.ricknout.rugbyranker.core.ui.openDrawer
import dev.ricknout.rugbyranker.core.util.CustomTabUtils
import dev.ricknout.rugbyranker.news.R
import dev.ricknout.rugbyranker.news.databinding.FragmentNewsBinding
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

    private val adapter = NewsAdapter { news ->
        lifecycleScope.launch {
            val theme = themeViewModel.theme.first()
            withContext(Dispatchers.Main) {
                CustomTabUtils.launchCustomTab(
                    requireContext(),
                    news.articleUrl,
                    theme.getCustomTabsIntentColorScheme(),
                )
            }
        }
    }

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

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
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        setupViewModel()
        setupNavigation()
        setupSwipeRefresh()
        setupRecyclerView()
        setupEdgeToEdge()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupViewModel() {
        newsViewModel.news.observe(
            viewLifecycleOwner,
        ) { pagingData ->
            adapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
        }
    }

    private fun setupNavigation() {
        binding.navigation.setOnClickListener { openDrawer() }
    }

    private fun setupSwipeRefresh() {
        val primaryColor = MaterialColors.getColor(binding.swipeRefreshLayout, R.attr.colorPrimary)
        val elevationOverlayProvider = ElevationOverlayProvider(requireContext())
        val surfaceColor = elevationOverlayProvider.compositeOverlayWithThemeSurfaceColorIfNeeded(
            resources.getDimension(R.dimen.elevation_swipe_refresh_layout),
        )
        binding.swipeRefreshLayout.setProgressBackgroundColorSchemeColor(surfaceColor)
        binding.swipeRefreshLayout.setColorSchemeColors(primaryColor)
        binding.swipeRefreshLayout.setProgressViewOffset(
            true,
            binding.swipeRefreshLayout.progressViewStartOffset,
            binding.swipeRefreshLayout.progressViewEndOffset,
        )
        binding.swipeRefreshLayout.setOnRefreshListener { adapter.refresh() }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = adapter
        adapter.addLoadStateListener { combinedLoadStates ->
            when (combinedLoadStates.refresh) {
                is LoadState.Loading -> {
                    binding.retry.isVisible = false
                    if (adapter.itemCount == 0) binding.progressIndicator.show() else binding.progressIndicator.hide()
                }
                is LoadState.NotLoading -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.retry.isVisible = false
                    binding.progressIndicator.hide()
                }
                is LoadState.Error -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.retry.isVisible = adapter.itemCount == 0
                    binding.progressIndicator.hide()
                    if (adapter.itemCount > 0) {
                        Snackbar.make(
                            binding.root,
                            R.string.failed_to_refresh_news,
                            Snackbar.LENGTH_SHORT,
                        ).show()
                    }
                }
            }
        }
        binding.retryButton.setOnClickListener { adapter.retry() }
    }

    private fun setupEdgeToEdge() {
        binding.appBarLayout.applyInsetter {
            type(statusBars = true, navigationBars = true) {
                padding(horizontal = true, top = true)
            }
        }
        binding.recyclerView.applyInsetter {
            type(navigationBars = true) {
                padding()
            }
        }
    }
}
