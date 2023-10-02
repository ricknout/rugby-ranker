package dev.ricknout.rugbyranker.live.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.color.MaterialColors
import com.google.android.material.elevation.ElevationOverlayProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.core.util.NotificationUtils
import dev.ricknout.rugbyranker.live.R
import dev.ricknout.rugbyranker.live.databinding.FragmentLiveMatchBinding
import java.lang.RuntimeException
import javax.inject.Inject

@AndroidEntryPoint
class LiveMatchFragment : Fragment() {
    private val args: LiveMatchFragmentArgs by navArgs()

    private val sport: Sport by lazy { args.sport }

    private val liveMatchViewModel: LiveMatchViewModel by lazy {
        when (sport) {
            Sport.MENS -> activityViewModels<MensLiveMatchViewModel>().value
            Sport.WOMENS -> activityViewModels<WomensLiveMatchViewModel>().value
        }
    }

    private val adapter =
        LiveMatchAdapter(
            { match ->
                val prediction = match.toPrediction()
                liveMatchViewModel.predict(prediction)
            },
            { match ->
                pin(match.id)
            },
        )

    private var pinMatchId: String? = null

    @Inject
    lateinit var notificationManager: NotificationManagerCompat

    private val notificationsRequestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { granted: Boolean ->
            if (granted) {
                if (pinMatchId == null) throw RuntimeException("Pin match ID is null")
                liveMatchViewModel.pin(matchId = pinMatchId!!)
            } else {
                showNotificationSettingsSnackbar()
            }
        }

    private val coordinatorLayout: CoordinatorLayout
        get() = ActivityCompat.requireViewById(requireActivity(), R.id.coordinatorLayout)

    private val fab: FloatingActionButton
        get() = ActivityCompat.requireViewById(requireActivity(), R.id.fab)

    private var _binding: FragmentLiveMatchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        if (savedInstanceState != null) pinMatchId = savedInstanceState.getString(KEY_PIN_MATCH_ID)
        _binding = FragmentLiveMatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupSwipeRefresh()
        setupRecyclerView()
        setupEdgeToEdge()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupViewModel() {
        liveMatchViewModel.liveMatches.observe(
            viewLifecycleOwner,
        ) { liveMatches ->
            adapter.submitList(liveMatches)
            if (liveMatches == null) binding.progressIndicator.show() else binding.progressIndicator.hide()
            binding.noLiveMatches.isVisible = liveMatches?.isEmpty() ?: false
        }
        liveMatchViewModel.refreshingLiveMatches.observe(
            viewLifecycleOwner,
        ) { refreshingLiveMatches ->
            binding.swipeRefreshLayout.isRefreshing = refreshingLiveMatches
        }
        liveMatchViewModel.scrollToTop.observe(
            viewLifecycleOwner,
        ) { scrollToTop ->
            if (scrollToTop) {
                binding.recyclerView.smoothScrollToPosition(0)
                liveMatchViewModel.resetScrollToTop()
            }
        }
    }

    private fun setupSwipeRefresh() {
        // Prevent AppBarLayout#liftOnScroll flickering in parent SportFragment
        binding.swipeRefreshLayout.isNestedScrollingEnabled = false
        val primaryColor = MaterialColors.getColor(binding.swipeRefreshLayout, dev.ricknout.rugbyranker.match.R.attr.colorPrimary)
        val elevationOverlayProvider = ElevationOverlayProvider(requireContext())
        val surfaceColor =
            elevationOverlayProvider.compositeOverlayWithThemeSurfaceColorIfNeeded(
                resources.getDimension(dev.ricknout.rugbyranker.match.R.dimen.elevation_swipe_refresh_layout),
            )
        binding.swipeRefreshLayout.setProgressBackgroundColorSchemeColor(surfaceColor)
        binding.swipeRefreshLayout.setColorSchemeColors(primaryColor)
        binding.swipeRefreshLayout.setProgressViewOffset(
            true,
            binding.swipeRefreshLayout.progressViewStartOffset,
            binding.swipeRefreshLayout.progressViewEndOffset,
        )
        binding.swipeRefreshLayout.setOnRefreshListener {
            liveMatchViewModel.refreshLiveMatches { success ->
                if (!success) {
                    Snackbar.make(
                        coordinatorLayout,
                        R.string.failed_to_refresh_live_matches,
                        Snackbar.LENGTH_SHORT,
                    ).apply {
                        anchorView = fab
                        show()
                    }
                }
            }
        }
    }

    private fun setupEdgeToEdge() {
        binding.recyclerView.applyInsetter {
            type(navigationBars = true) {
                padding()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = adapter
    }

    private fun pin(matchId: String) {
        val notificationsEnabled = NotificationUtils.areNotificationsEnabled(requireContext())
        val liveNotificationChannelEnabled =
            NotificationUtils.isNotificationChannelEnabled(
                notificationManager = notificationManager,
                channelId = NotificationUtils.NOTIFICATION_CHANNEL_ID_LIVE,
            )
        val resultNotificationChannelEnabled =
            NotificationUtils.isNotificationChannelEnabled(
                notificationManager = notificationManager,
                channelId = NotificationUtils.NOTIFICATION_CHANNEL_ID_RESULT,
            )
        when {
            !notificationsEnabled -> {
                if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    showNotificationPermissionRationaleSnackbar(matchId)
                } else {
                    requestNotificationsPermission(matchId)
                }
            }
            !liveNotificationChannelEnabled || !resultNotificationChannelEnabled -> {
                showNotificationSettingsSnackbar()
            }
            else -> liveMatchViewModel.pin(matchId)
        }
    }

    private fun requestNotificationsPermission(matchId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pinMatchId = matchId
            notificationsRequestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            showNotificationSettingsSnackbar()
        }
    }

    private fun showNotificationPermissionRationaleSnackbar(matchId: String) {
        Snackbar.make(
            coordinatorLayout,
            R.string.notifications_permission_text,
            Snackbar.LENGTH_LONG,
        ).apply {
            anchorView = fab
            setAction(R.string.proceed) {
                requestNotificationsPermission(matchId)
            }
            show()
        }
    }

    private fun showNotificationSettingsSnackbar() {
        Snackbar.make(
            coordinatorLayout,
            R.string.notifications_permission_text,
            Snackbar.LENGTH_LONG,
        ).apply {
            anchorView = fab
            setAction(R.string.settings) {
                NotificationUtils.showNotificationSettings(requireContext())
            }
            show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_PIN_MATCH_ID, pinMatchId)
    }

    companion object {
        private const val KEY_PIN_MATCH_ID = "pin_match_id"

        fun newInstance(sport: Sport): LiveMatchFragment {
            val liveMatchFragment = LiveMatchFragment()
            liveMatchFragment.arguments = LiveMatchFragmentDirections.liveMatchAction(sport).arguments
            return liveMatchFragment
        }
    }
}
