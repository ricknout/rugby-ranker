package dev.ricknout.rugbyranker.prediction.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent.ACTION_CANCEL
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.ListPopupWindow
import androidx.appcompat.widget.TooltipCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.core.ui.MaterialListPopupWindow
import dev.ricknout.rugbyranker.core.util.FlagUtils
import dev.ricknout.rugbyranker.prediction.R
import dev.ricknout.rugbyranker.prediction.databinding.BottomSheetDialogFragmentPredictionBinding
import dev.ricknout.rugbyranker.prediction.model.Prediction
import dev.ricknout.rugbyranker.prediction.model.Team
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PredictionBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private val args: PredictionBottomSheetDialogFragmentArgs by navArgs()

    private val sport: Sport by lazy { args.sport }
    private val prediction: Prediction? by lazy { args.prediction }
    private val edit: Boolean by lazy { args.edit }

    private val predictionViewModel: PredictionViewModel by lazy {
        when (sport) {
            Sport.MENS -> activityViewModels<MensPredictionViewModel>().value
            Sport.WOMENS -> activityViewModels<WomensPredictionViewModel>().value
        }
    }

    private var incrementHomeScoreJob: Job? = null
    private var decrementHomeScoreJob: Job? = null
    private var incrementAwayScoreJob: Job? = null
    private var decrementAwayScoreJob: Job? = null

    private var confirmed = false

    private var _binding: BottomSheetDialogFragmentPredictionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetDialogFragmentPredictionBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressWarnings("VisibleForTests", "RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupTitle()
        setupDismiss()
        setupScoreButtons()
        setupCheckBoxes()
        setupConfirm()
        setupEdgeToEdge()
        if (savedInstanceState == null && prediction != null) predictionViewModel.setInput(prediction!!)
        (requireDialog() as BottomSheetDialog).apply {
            behavior.disableShapeAnimations()
            dismissWithAnimation = true
            behavior.addBottomSheetCallback(
                object : BottomSheetBehavior.BottomSheetCallback() {

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    }

                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            if (confirmed) predictionViewModel.submitPrediction(prediction, edit)
                            predictionViewModel.resetInput()
                        }
                    }
                },
            )
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupViewModel() {
        predictionViewModel.teams.observe(
            viewLifecycleOwner,
            { teams ->
                setupTeams(teams)
            },
        )
        predictionViewModel.inputValid.observe(
            viewLifecycleOwner,
            { inputValid ->
                binding.confirm.isEnabled = inputValid
            },
        )
        predictionViewModel.homeScore.observe(
            viewLifecycleOwner,
            { score ->
                binding.homeScore.text = score.toString()
            },
        )
        predictionViewModel.awayScore.observe(
            viewLifecycleOwner,
            { score ->
                binding.awayScore.text = score.toString()
            },
        )
        predictionViewModel.homeTeam.observe(
            viewLifecycleOwner,
            { team ->
                if (team != null) {
                    binding.homeFlag.text = FlagUtils.getFlagEmojiForTeamAbbreviation(team.abbreviation)
                    binding.homeName.text = team.name
                    binding.homeName.setTextColor(
                        MaterialColors.getColor(requireView(), R.attr.colorOnSurface),
                    )
                } else {
                    binding.homeFlag.text = FlagUtils.getFlagEmojiForTeamAbbreviation(null)
                    binding.homeName.setText(R.string.home_team)
                    binding.homeName.setTextColor(
                        AppCompatResources.getColorStateList(requireView().context, R.color.material_on_surface_emphasis_medium),
                    )
                }
            },
        )
        predictionViewModel.awayTeam.observe(
            viewLifecycleOwner,
            { team ->
                if (team != null) {
                    binding.awayFlag.text = FlagUtils.getFlagEmojiForTeamAbbreviation(team.abbreviation)
                    binding.awayName.text = team.name
                    binding.awayName.setTextColor(
                        MaterialColors.getColor(requireView(), R.attr.colorOnSurface),
                    )
                } else {
                    binding.awayFlag.text = FlagUtils.getFlagEmojiForTeamAbbreviation(null)
                    binding.awayName.setText(R.string.away_team)
                    binding.awayName.setTextColor(
                        AppCompatResources.getColorStateList(requireView().context, R.color.material_on_surface_emphasis_medium),
                    )
                }
            },
        )
        predictionViewModel.rugbyWorldCup.observe(
            viewLifecycleOwner,
            { rugbyWorldCup ->
                binding.rwc.isChecked = rugbyWorldCup
            },
        )
        predictionViewModel.noHomeAdvantage.observe(
            viewLifecycleOwner,
            { noHomeAdvantage ->
                binding.nha.isChecked = noHomeAdvantage
            },
        )
    }

    private fun setupTitle() {
        binding.title.text = if (edit) getString(R.string.edit_prediction) else getString(R.string.add_prediction)
    }

    private fun setupDismiss() {
        binding.dismiss.setOnClickListener { dismiss() }
        TooltipCompat.setTooltipText(binding.dismiss, getString(R.string.dismiss))
    }

    private fun setupTeams(teams: List<Team>) {
        val homePopupMenu = createPopupMenu(binding.homeTeam, teams) { team ->
            predictionViewModel.setInput(homeTeam = team)
        }
        binding.homeTeam.setOnClickListener {
            homePopupMenu.show()
            homePopupMenu.listView?.isFastScrollEnabled = true
        }
        val awayPopupMenu = createPopupMenu(binding.awayTeam, teams) { team ->
            predictionViewModel.setInput(awayTeam = team)
        }
        binding.awayTeam.setOnClickListener {
            awayPopupMenu.show()
            awayPopupMenu.listView?.isFastScrollEnabled = true
        }
    }

    @SuppressWarnings("ClickableViewAccessibility")
    private fun setupScoreButtons() {
        binding.incrementHomeScore.apply {
            setOnClickListener { predictionViewModel.incrementHomeScore() }
            setOnLongClickListener {
                incrementHomeScoreRepeated()
                true
            }
            setOnTouchListener { _, event ->
                if (event.action == ACTION_UP || event.action == ACTION_CANCEL) {
                    incrementHomeScoreJob?.cancel()
                    incrementHomeScoreJob = null
                }
                false
            }
        }
        binding.decrementHomeScore.apply {
            setOnClickListener { predictionViewModel.decrementHomeScore() }
            setOnLongClickListener {
                decrementHomeScoreRepeated()
                true
            }
            setOnTouchListener { _, event ->
                if (event.action == ACTION_UP || event.action == ACTION_CANCEL) {
                    decrementHomeScoreJob?.cancel()
                    decrementHomeScoreJob = null
                }
                false
            }
        }
        binding.incrementAwayScore.apply {
            setOnClickListener { predictionViewModel.incrementAwayScore() }
            setOnLongClickListener {
                incrementAwayScoreRepeated()
                true
            }
            setOnTouchListener { _, event ->
                if (event.action == ACTION_UP || event.action == ACTION_CANCEL) {
                    incrementAwayScoreJob?.cancel()
                    incrementAwayScoreJob = null
                }
                false
            }
        }
        binding.decrementAwayScore.apply {
            setOnClickListener { predictionViewModel.decrementAwayScore() }
            setOnLongClickListener {
                decrementAwayScoreRepeated()
                true
            }
            setOnTouchListener { _, event ->
                if (event.action == ACTION_UP || event.action == ACTION_CANCEL) {
                    decrementAwayScoreJob?.cancel()
                    decrementAwayScoreJob = null
                }
                false
            }
        }
    }

    private fun setupCheckBoxes() {
        binding.rwc.setOnCheckedChangeListener { _, isChecked ->
            predictionViewModel.setInput(rugbyWorldCup = isChecked)
        }
        binding.nha.setOnCheckedChangeListener { _, isChecked ->
            predictionViewModel.setInput(noHomeAdvantage = isChecked)
        }
    }

    private fun setupConfirm() {
        binding.confirm.setOnClickListener {
            confirmed = true
            dismiss()
        }
        TooltipCompat.setTooltipText(binding.confirm, if (edit) getString(R.string.edit) else getString(R.string.add))
    }

    private fun setupEdgeToEdge() {
        val container = requireDialog().findViewById<View>(R.id.container)
        container.applyInsetter {
            type(statusBars = true) {
                padding()
            }
        }
    }

    private fun createPopupMenu(
        anchorView: View,
        teams: List<Team>,
        onClick: (team: Team) -> Unit,
    ): ListPopupWindow = MaterialListPopupWindow(requireContext()).apply {
        val adapter = object : ArrayAdapter<Team>(
            requireContext(),
            R.layout.list_item_team,
            R.id.name,
            teams,
        ) {

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val team = getItem(position)!!
                view.findViewById<TextView>(R.id.flag).text = FlagUtils.getFlagEmojiForTeamAbbreviation(team.abbreviation)
                view.findViewById<TextView>(R.id.name).text = team.name
                return view
            }
        }
        setAdapter(adapter)
        this.anchorView = anchorView
        setOnItemClickListener { _, _, position, _ ->
            val team = adapter.getItem(position)!!
            onClick(team)
            dismiss()
        }
    }

    private fun incrementHomeScoreRepeated() {
        incrementHomeScoreJob = lifecycleScope.launch {
            predictionViewModel.incrementHomeScore()
            delay(DELAY_TIME_MILLIS)
            incrementHomeScoreRepeated()
        }
    }

    private fun decrementHomeScoreRepeated() {
        decrementHomeScoreJob = lifecycleScope.launch {
            predictionViewModel.decrementHomeScore()
            delay(DELAY_TIME_MILLIS)
            decrementHomeScoreRepeated()
        }
    }

    private fun incrementAwayScoreRepeated() {
        incrementAwayScoreJob = lifecycleScope.launch {
            predictionViewModel.incrementAwayScore()
            delay(DELAY_TIME_MILLIS)
            incrementAwayScoreRepeated()
        }
    }

    private fun decrementAwayScoreRepeated() {
        decrementAwayScoreJob = lifecycleScope.launch {
            predictionViewModel.decrementAwayScore()
            delay(DELAY_TIME_MILLIS)
            decrementAwayScoreRepeated()
        }
    }

    companion object {
        private const val DELAY_TIME_MILLIS = 50L
    }
}
