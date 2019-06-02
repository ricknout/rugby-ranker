package com.ricknout.rugbyranker.info.ui

import android.os.Bundle
import android.view.View
import com.ricknout.rugbyranker.info.R
import kotlinx.android.synthetic.main.fragment_info.*
import android.content.Intent
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.ricknout.rugbyranker.core.livedata.EventObserver
import com.ricknout.rugbyranker.core.ui.dagger.DaggerAndroidXFragment
import com.ricknout.rugbyranker.info.BuildConfig
import com.ricknout.rugbyranker.info.util.CustomTabsUtils
import com.ricknout.rugbyranker.theme.ui.showThemeChooser
import javax.inject.Inject

class InfoFragment : DaggerAndroidXFragment(R.layout.fragment_info) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: InfoViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory)
                .get(InfoViewModel::class.java)
        setupViewModel()
        setupButtons()
        setupNestedScrollView()
    }

    private fun setupViewModel() {
        viewModel.scrollToTop.observe(viewLifecycleOwner, EventObserver {
            infoNestedScrollView.smoothScrollTo(0, 0)
            appBarLayout.setExpanded(true)
        })
    }

    private fun setupButtons() {
        howAreWorldRugbyRankingsCalculatedButton.setOnClickListener {
            CustomTabsUtils.launchCustomTab(requireContext(), RANKINGS_EXPLANATION_URL)
        }
        shareThisAppButton.setOnClickListener {
            val appName = getString(R.string.app_name)
            val intent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_SUBJECT, requireContext().getString(R.string.subject_share, appName))
                putExtra(Intent.EXTRA_TEXT, requireContext().getString(R.string.text_share, appName, PLAY_STORE_URL))
                type = "text/plain"
            }
            startActivity(Intent.createChooser(intent, requireContext().getString(R.string.title_share, appName)))
        }
        viewSourceCodeButton.setOnClickListener {
            CustomTabsUtils.launchCustomTab(requireContext(), GITHUB_URL)
        }
        openSourceLicensesButton.setOnClickListener {
            val intent = Intent(requireContext(), OssLicensesMenuActivity::class.java)
            startActivity(intent)
        }
        chooseThemeButton.setOnClickListener {
            showThemeChooser(requireContext())
        }
    }

    private fun setupNestedScrollView() {
        infoNestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            val delta = scrollY - oldScrollY
            viewModel.onScroll(delta)
        })
    }

    companion object {
        const val TAG = "InfoFragment"
        private const val RANKINGS_EXPLANATION_URL = "https://www.world.rugby/rankings/explanation"
        private const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
        private const val GITHUB_URL = "https://github.com/nicholasrout/rugby-ranker"
    }
}
