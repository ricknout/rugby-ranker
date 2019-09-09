package com.ricknout.rugbyranker.news.ui

import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkInfo
import com.google.android.material.snackbar.Snackbar
import com.ricknout.rugbyranker.core.livedata.EventObserver
import com.ricknout.rugbyranker.core.ui.SpaceItemDecoration
import com.ricknout.rugbyranker.core.ui.dagger.DaggerAndroidXFragment
import com.ricknout.rugbyranker.core.util.CustomTabsUtils
import com.ricknout.rugbyranker.core.util.doIfResumed
import com.ricknout.rugbyranker.news.ArticlesNavGraphDirections
import com.ricknout.rugbyranker.news.R
import com.ricknout.rugbyranker.news.vo.ArticleType
import com.ricknout.rugbyranker.theme.ui.ThemeViewModel
import com.ricknout.rugbyranker.theme.util.getCustomTabsIntentColorScheme
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_articles.*

class ArticlesFragment : DaggerAndroidXFragment(R.layout.fragment_articles) {

    private val args: ArticlesFragmentArgs by navArgs()

    private val articleType: ArticleType by lazy { args.articleType }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val articlesViewModel: ArticlesViewModel by lazy {
        when (articleType) {
            ArticleType.TEXT -> activityViewModels<TextArticlesViewModel> { viewModelFactory }.value
            ArticleType.VIDEO -> activityViewModels<VideoArticlesViewModel> { viewModelFactory }.value
        }
    }

    private val themeViewModel: ThemeViewModel by activityViewModels { viewModelFactory }

    private val coordinatorLayout: CoordinatorLayout
        get() = ActivityCompat.requireViewById(requireActivity(), R.id.coordinatorLayout)

    private var workerSnackBar: Snackbar? = null

    private lateinit var worldRugbyArticlePagedListAdapter: WorldRugbyArticlePagedListAdapter
    private lateinit var spaceItemDecoration: SpaceItemDecoration

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupViewModel()
        setupSwipeRefreshLayout()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dismissWorkerSnackbar()
    }

    private fun setupRecyclerView() {
        spaceItemDecoration = SpaceItemDecoration(requireContext())
        articlesRecyclerView.addItemDecoration(spaceItemDecoration, 0)
        worldRugbyArticlePagedListAdapter = WorldRugbyArticlePagedListAdapter { worldRugbyArticle ->
            CustomTabsUtils.launchCustomTab(requireContext(), worldRugbyArticle.articleUrl,
                    themeViewModel.getTheme().getCustomTabsIntentColorScheme())
        }
        articlesRecyclerView.adapter = worldRugbyArticlePagedListAdapter
        articlesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                articlesViewModel.onScroll(delta = dy)
            }
        })
    }

    private fun setupViewModel() {
        articlesViewModel.latestWorldRugbyArticles.observe(viewLifecycleOwner, Observer { latestWorldRugbyArticles ->
            worldRugbyArticlePagedListAdapter.submitList(latestWorldRugbyArticles)
            val isEmpty = latestWorldRugbyArticles?.isEmpty() ?: true
            progressBar.isVisible = isEmpty
        })
        articlesViewModel.latestWorldRugbyArticlesWorkInfos.observe(viewLifecycleOwner, Observer { workInfos ->
            val workInfo = workInfos?.firstOrNull()
            when (workInfo?.state) {
                WorkInfo.State.RUNNING -> {
                    swipeRefreshLayout.isEnabled = false
                    doIfResumed {
                        workerSnackBar = Snackbar.make(
                                coordinatorLayout,
                                when (articleType) {
                                    ArticleType.TEXT -> R.string.snackbar_fetching_world_rugby_news
                                    ArticleType.VIDEO -> R.string.snackbar_fetching_world_rugby_videos
                                },
                                Snackbar.LENGTH_INDEFINITE
                        ).apply { show() }
                    }
                }
                else -> {
                    swipeRefreshLayout.isEnabled = true
                    dismissWorkerSnackbar()
                }
            }
        })
        articlesViewModel.refreshingLatestWorldRugbyArticles.observe(viewLifecycleOwner, Observer { refreshingLatestWorldRugbyArticles ->
            swipeRefreshLayout.isRefreshing = refreshingLatestWorldRugbyArticles
        })
        articlesViewModel.scrollToTop.observe(viewLifecycleOwner, EventObserver {
            doIfResumed { articlesRecyclerView.smoothScrollToPosition(0) }
        })
    }

    private fun setupSwipeRefreshLayout() {
        val swipeRefreshColors = resources.getIntArray(R.array.colors_swipe_refresh)
        swipeRefreshLayout.setColorSchemeColors(*swipeRefreshColors)
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.color_surface)
        swipeRefreshLayout.setProgressViewOffset(true,
                swipeRefreshLayout.progressViewStartOffset + resources.getDimensionPixelSize(R.dimen.spacing_double),
                swipeRefreshLayout.progressViewEndOffset)
        swipeRefreshLayout.setOnRefreshListener {
            articlesViewModel.refreshLatestWorldRugbyArticles { success ->
                if (!success) {
                    doIfResumed {
                        Snackbar.make(
                                coordinatorLayout,
                                when (articleType) {
                                    ArticleType.TEXT -> R.string.snackbar_failed_to_refresh_world_rugby_news
                                    ArticleType.VIDEO -> R.string.snackbar_failed_to_refresh_world_rugby_videos
                                },
                                Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun dismissWorkerSnackbar() {
        workerSnackBar?.dismiss()
        workerSnackBar = null
    }

    companion object {
        const val TAG = "ArticlesFragment"
        fun newInstance(articleType: ArticleType): ArticlesFragment {
            val articlesFragment = ArticlesFragment()
            articlesFragment.arguments = ArticlesNavGraphDirections.articlesFragmentAction(articleType).arguments
            return articlesFragment
        }
    }
}
