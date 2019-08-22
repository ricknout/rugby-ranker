package com.ricknout.rugbyranker.info.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.ricknout.rugbyranker.core.livedata.EventObserver
import com.ricknout.rugbyranker.core.ui.dagger.DaggerAndroidXFragment
import com.ricknout.rugbyranker.core.util.CustomTabsUtils
import com.ricknout.rugbyranker.info.R
import com.ricknout.rugbyranker.theme.ui.ThemeViewModel
import com.ricknout.rugbyranker.theme.util.getCustomTabsIntentColorScheme
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_info.*

class InfoFragment : DaggerAndroidXFragment(R.layout.fragment_info) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val infoViewModel: InfoViewModel by activityViewModels { viewModelFactory }

    private val themeViewModel: ThemeViewModel by activityViewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViewModel()
        setupButtons()
        setupNestedScrollView()
    }

    private fun setupViewModel() {
        infoViewModel.scrollToTop.observe(viewLifecycleOwner, EventObserver {
            infoNestedScrollView.smoothScrollTo(0, 0)
            appBarLayout.setExpanded(true)
        })
    }

    private fun setupButtons() {
        howAreWorldRugbyRankingsCalculatedButton.setOnClickListener {
            CustomTabsUtils.launchCustomTab(requireContext(), RANKINGS_EXPLANATION_URL,
                    themeViewModel.getTheme().getCustomTabsIntentColorScheme())
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
            CustomTabsUtils.launchCustomTab(requireContext(), GITHUB_URL,
                    themeViewModel.getTheme().getCustomTabsIntentColorScheme())
        }
        openSourceLicensesButton.setOnClickListener {
            val intent = Intent(requireContext(), OssLicensesMenuActivity::class.java)
            startActivity(intent)
        }
        chooseThemeButton.setOnClickListener {
            findNavController().navigate(R.id.infoFragmentToThemeDialogFragmentAction)
        }
    }

    private fun setupNestedScrollView() {
        infoNestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            val delta = scrollY - oldScrollY
            infoViewModel.onScroll(delta)
        })
    }

    companion object {
        const val TAG = "InfoFragment"
        private const val RANKINGS_EXPLANATION_URL = "https://www.world.rugby/rankings/explanation"
        private const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.ricknout.rugbyranker"
        private const val GITHUB_URL = "https://github.com/nicholasrout/rugby-ranker"
    }
}
