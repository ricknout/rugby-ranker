package dev.ricknout.rugbyranker.info.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.compose.content
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import dev.ricknout.rugbyranker.core.ui.openDrawer
import dev.ricknout.rugbyranker.core.ui.theme.RugbyRankerTheme
import dev.ricknout.rugbyranker.core.util.CustomTabUtils
import dev.ricknout.rugbyranker.info.R
import dev.ricknout.rugbyranker.theme.ui.ThemeViewModel
import dev.ricknout.rugbyranker.theme.util.getCustomTabsIntentColorScheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class InfoFragment : Fragment() {
    private val infoViewModel: InfoViewModel by activityViewModels()

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
    ) = content {
        RugbyRankerTheme {
            val version by infoViewModel.version.collectAsState()
            Info(
                version = version?.let { stringResource(id = R.string.version, it) } ?: "",
                onHowAreRankingsCalculatedClick = {
                    lifecycleScope.launch {
                        val theme = themeViewModel.theme.first()
                        withContext(Dispatchers.Main) {
                            CustomTabUtils.launchCustomTab(
                                requireContext(),
                                RANKINGS_EXPLANATION_URL,
                                theme.getCustomTabsIntentColorScheme(),
                            )
                        }
                    }
                },
                onShareThisAppClick = {
                    val appName = getString(R.string.app_name)
                    val intent =
                        Intent(Intent.ACTION_SEND).apply {
                            putExtra(Intent.EXTRA_SUBJECT, requireContext().getString(R.string.share_subject, appName))
                            putExtra(Intent.EXTRA_TEXT, requireContext().getString(R.string.share_text, appName, GOOGLE_PLAY_URL))
                            type = "text/plain"
                        }
                    startActivity(Intent.createChooser(intent, requireContext().getString(R.string.share_title, appName)))
                },
                onViewOnGooglePlayClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_URL))
                    startActivity(intent)
                },
                onViewSourceCodeClick = {
                    lifecycleScope.launch {
                        val theme = themeViewModel.theme.first()
                        withContext(Dispatchers.Main) {
                            CustomTabUtils.launchCustomTab(
                                requireContext(),
                                GITHUB_URL,
                                theme.getCustomTabsIntentColorScheme(),
                            )
                        }
                    }
                },
                onOpenSourceLicensesClick = {
                    val intent = Intent(requireContext(), OssLicensesMenuActivity::class.java)
                    startActivity(intent)
                },
                onChooseThemeClick = {
                    lifecycleScope.launch {
                        val theme = themeViewModel.theme.first()
                        withContext(Dispatchers.Main) {
                            findNavController().navigate(InfoFragmentDirections.infoToTheme(theme))
                        }
                    }
                },
                onNavigationClick = {
                    openDrawer()
                },
            )
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

    companion object {
        private const val RANKINGS_EXPLANATION_URL = "https://www.world.rugby/tournaments/rankings/explanation"
        private const val GOOGLE_PLAY_URL = "https://play.google.com/store/apps/details?id=com.ricknout.rugbyranker"
        private const val GITHUB_URL = "https://github.com/ricknout/rugby-ranker"
    }
}
