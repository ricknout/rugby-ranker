package com.ricknout.rugbyranker.news.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.ricknout.rugbyranker.core.livedata.EventObserver
import com.ricknout.rugbyranker.core.ui.dagger.DaggerAndroidXFragment
import com.ricknout.rugbyranker.news.R
import com.ricknout.rugbyranker.news.vo.ArticleType
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_news.*

class NewsFragment : DaggerAndroidXFragment(R.layout.fragment_news) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: NewsViewModel by activityViewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabsAndViewPager()
        setupViewModel()
    }

    private fun setupTabsAndViewPager() {
        viewPager.adapter = NewsFragmentStateAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                POSITION_TEXT -> getString(R.string.title_news)
                POSITION_VIDEO -> getString(R.string.title_videos)
                else -> null
            }
        }.attach()
    }

    private fun setupViewModel() {
        viewModel.scrollToTop.observe(viewLifecycleOwner, EventObserver {
            appBarLayout.setExpanded(true)
        })
    }

    inner class NewsFragmentStateAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun createFragment(position: Int) = when (position) {
            POSITION_TEXT -> ArticlesFragment.newInstance(ArticleType.TEXT)
            POSITION_VIDEO -> ArticlesFragment.newInstance(ArticleType.VIDEO)
            else -> throw IllegalArgumentException("Position $position exceeds NewsFragmentStateAdapter count")
        }

        override fun getItemCount() = 2
    }

    companion object {
        const val TAG = "NewsFragment"
        private const val POSITION_TEXT = 0
        private const val POSITION_VIDEO = 1
    }
}
