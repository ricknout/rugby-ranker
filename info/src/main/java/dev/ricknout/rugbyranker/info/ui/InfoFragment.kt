package dev.ricknout.rugbyranker.info.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applySystemWindowInsetsToPadding
import dev.ricknout.rugbyranker.core.ui.openDrawer
import dev.ricknout.rugbyranker.core.util.CustomTabUtils
import dev.ricknout.rugbyranker.info.R
import dev.ricknout.rugbyranker.info.databinding.FragmentInfoBinding
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

    private var _binding: FragmentInfoBinding? = null
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
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        setupViewModel()
        setupNavigation()
        setupButtons()
        setupEdgeToEdge()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupViewModel() {
        infoViewModel.version.observe(
            viewLifecycleOwner,
            { version ->
                binding.label.text = if (version != null) getString(R.string.version, version) else null
            }
        )
    }

    private fun setupNavigation() {
        binding.navigation.setOnClickListener { openDrawer() }
    }

    private fun setupButtons() {
        binding.howAreRankingsCalculated.setOnClickListener {
            lifecycleScope.launch {
                val theme = themeViewModel.theme.first()
                withContext(Dispatchers.Main) {
                    CustomTabUtils.launchCustomTab(
                        requireContext(),
                        RANKINGS_EXPLANATION_URL,
                        theme.getCustomTabsIntentColorScheme()
                    )
                }
            }
        }
        binding.shareThisApp.setOnClickListener {
            val appName = getString(R.string.app_name)
            val intent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_SUBJECT, requireContext().getString(R.string.share_subject, appName))
                putExtra(Intent.EXTRA_TEXT, requireContext().getString(R.string.share_text, appName, PLAY_STORE_URL))
                type = "text/plain"
            }
            startActivity(Intent.createChooser(intent, requireContext().getString(R.string.share_title, appName)))
        }
        binding.viewSourceCode.setOnClickListener {
            lifecycleScope.launch {
                val theme = themeViewModel.theme.first()
                withContext(Dispatchers.Main) {
                    CustomTabUtils.launchCustomTab(
                        requireContext(),
                        GITHUB_URL,
                        theme.getCustomTabsIntentColorScheme()
                    )
                }
            }
        }
        binding.openSourceLicenses.setOnClickListener {
            val intent = Intent(requireContext(), OssLicensesMenuActivity::class.java)
            startActivity(intent)
        }
        binding.chooseTheme.setOnClickListener {
            lifecycleScope.launch {
                val theme = themeViewModel.theme.first()
                withContext(Dispatchers.Main) {
                    findNavController().navigate(InfoFragmentDirections.infoToTheme(theme))
                }
            }
        }
    }

    private fun setupEdgeToEdge() {
        binding.appBarLayout.applySystemWindowInsetsToPadding(left = true, top = true, right = true)
        binding.nestedScrollView.applySystemWindowInsetsToPadding(left = true, right = true, bottom = true)
    }

    companion object {
        private const val RANKINGS_EXPLANATION_URL = "https://www.world.rugby/rankings/explanation"
        private const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.ricknout.rugbyranker"
        private const val GITHUB_URL = "https://github.com/ricknout/rugby-ranker"
    }
}
