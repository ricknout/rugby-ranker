package com.ricknout.rugbyranker.prediction.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ricknout.rugbyranker.core.ui.NoFilterArrayAdapter
import com.ricknout.rugbyranker.core.ui.dagger.DaggerBottomSheetDialogFragment
import com.ricknout.rugbyranker.core.vo.Sport
import com.ricknout.rugbyranker.prediction.R
import com.ricknout.rugbyranker.prediction.vo.Prediction
import com.ricknout.rugbyranker.prediction.vo.Team
import com.ricknout.rugbyranker.teams.ui.MensTeamsViewModel
import com.ricknout.rugbyranker.teams.ui.TeamsViewModel
import com.ricknout.rugbyranker.teams.ui.WomensTeamsViewModel
import com.ricknout.rugbyranker.teams.vo.WorldRugbyTeam
import javax.inject.Inject
import kotlinx.android.synthetic.main.bottom_sheet_dialog_fragment_prediction.*

class PredictionBottomSheetDialogFragment : DaggerBottomSheetDialogFragment() {

    private val args: PredictionBottomSheetDialogFragmentArgs by navArgs()

    private val sport: Sport by lazy { args.sport }

    private val isEditing: Boolean by lazy { args.isEditing }

    private val prediction: Prediction? by lazy { args.prediction }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val predictionViewModel: PredictionViewModel by lazy {
        when (sport) {
            Sport.MENS -> activityViewModels<MensPredictionViewModel> { viewModelFactory }.value
            Sport.WOMENS -> activityViewModels<WomensPredictionViewModel> { viewModelFactory }.value
        }
    }
    private val teamsViewModel: TeamsViewModel by lazy {
        when (sport) {
            Sport.MENS -> activityViewModels<MensTeamsViewModel> { viewModelFactory }.value
            Sport.WOMENS -> activityViewModels<WomensTeamsViewModel> { viewModelFactory }.value
        }
    }

    private var homeTeam: Team? = null
    private var awayTeam: Team? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.bottom_sheet_dialog_fragment_prediction, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModels()
        setupEditTexts()
        if (savedInstanceState == null) {
            if (prediction != null) applyPredictionToInput(prediction!!)
        } else {
            homeTeam = savedInstanceState.getParcelable(KEY_HOME_TEAM)
            awayTeam = savedInstanceState.getParcelable(KEY_AWAY_TEAM)
        }
        setupCloseButton()
        setupClearOrCancelButton()
        setupAddOrEditButton()
        adjustForEditing(isEditing)
    }

    @SuppressWarnings("VisibleForTests", "RestrictedApi")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireDialog() as BottomSheetDialog).apply {
            behavior.disableShapeAnimations()
            dismissWithAnimation = true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_HOME_TEAM, homeTeam)
        outState.putParcelable(KEY_AWAY_TEAM, awayTeam)
    }

    private fun setupCloseButton() {
        closeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun setupEditTexts() {
        homeTeamEditText.apply {
            onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                val team = adapter.getItem(position) as Team
                if (team.id == awayTeam?.id) {
                    setText(homeTeam?.getEmojiProcessedTitle(), false)
                    return@OnItemClickListener
                }
                homeTeam = team
                setText(team.getEmojiProcessedTitle(), false)
            }
            doOnTextChanged { text, _, _, _ ->
                val valid = !text.isNullOrEmpty()
                predictionViewModel.homeTeamInputValid.value = valid
            }
        }
        awayTeamEditText.apply {
            onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                val team = adapter.getItem(position) as Team
                if (team.id == homeTeam?.id) {
                    setText(awayTeam?.getEmojiProcessedTitle(), false)
                    return@OnItemClickListener
                }
                awayTeam = team
                setText(team.getEmojiProcessedTitle(), false)
            }
            doOnTextChanged { text, _, _, _ ->
                val valid = !text.isNullOrEmpty()
                predictionViewModel.awayTeamInputValid.value = valid
            }
        }
        homePointsEditText.doOnTextChanged { text, _, _, _ ->
            val valid = !text.isNullOrEmpty()
            predictionViewModel.homePointsInputValid.value = valid
        }
        awayPointsEditText.apply {
            doOnTextChanged { text, _, _, _ ->
                val valid = !text.isNullOrEmpty()
                predictionViewModel.awayPointsInputValid.value = valid
            }
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    return@setOnEditorActionListener addOrEditPredictionFromInput()
                }
                false
            }
        }
    }

    private fun setupClearOrCancelButton() {
        clearOrCancelButton.setOnClickListener {
            clearPredictionInput()
        }
    }

    private fun setupAddOrEditButton() {
        addOrEditButton.setOnClickListener {
            addOrEditPredictionFromInput()
        }
    }

    private fun setupViewModels() {
        predictionViewModel.predictionInputValid.observe(viewLifecycleOwner, Observer { predictionInputValid ->
            addOrEditButton.isEnabled = predictionInputValid
        })
        teamsViewModel.latestWorldRugbyTeams.observe(viewLifecycleOwner, Observer { worldRugbyTeams ->
            applyWorldRugbyTeamsToInput(worldRugbyTeams)
        })
    }

    private fun adjustForEditing(isEditing: Boolean) {
        titleTextView.setText(if (isEditing) R.string.title_edit_match_prediction else R.string.title_add_match_prediction)
        addOrEditButton.setText(if (isEditing) R.string.button_edit else R.string.button_add)
    }

    private fun applyPredictionToInput(prediction: Prediction) {
        homeTeam = Team.from(requireContext(), prediction, isHomeTeam = true)
        homeTeamEditText.setText(homeTeam!!.getEmojiProcessedTitle(), false)
        if (prediction.homeTeamScore != Prediction.NO_SCORE) {
            homePointsEditText.setText(prediction.homeTeamScore.toString())
        }
        awayTeam = Team.from(requireContext(), prediction, isHomeTeam = false)
        awayTeamEditText.setText(awayTeam!!.getEmojiProcessedTitle(), false)
        if (prediction.awayTeamScore != Prediction.NO_SCORE) {
            awayPointsEditText.setText(prediction.awayTeamScore.toString())
        }
        nhaCheckBox.isChecked = prediction.noHomeAdvantage
        rwcCheckBox.isChecked = prediction.rugbyWorldCup
    }

    private fun addOrEditPredictionFromInput(): Boolean {
        val homeTeam = homeTeam ?: return false
        if (homePointsEditText.text.isNullOrEmpty()) {
            return false
        }
        val homeTeamScore = homePointsEditText.text.toString().toInt()
        val awayTeam = awayTeam ?: return false
        if (awayPointsEditText.text.isNullOrEmpty()) {
            return false
        }
        val awayTeamScore = awayPointsEditText.text.toString().toInt()
        val nha = nhaCheckBox.isChecked
        val rwc = rwcCheckBox.isChecked
        val id = when {
            isEditing -> prediction!!.id
            else -> Prediction.generateId()
        }
        val prediction = Prediction(
                id = id,
                homeTeamId = homeTeam.id,
                homeTeamName = homeTeam.name,
                homeTeamAbbreviation = homeTeam.abbreviation,
                homeTeamScore = homeTeamScore,
                awayTeamId = awayTeam.id,
                awayTeamName = awayTeam.name,
                awayTeamAbbreviation = awayTeam.abbreviation,
                awayTeamScore = awayTeamScore,
                noHomeAdvantage = nha,
                rugbyWorldCup = rwc
        )
        if (isEditing) {
            predictionViewModel.editPrediction(prediction)
        } else {
            predictionViewModel.addPrediction(prediction)
        }
        dismiss()
        return true
    }

    private fun clearPredictionInput() {
        homeTeam = null
        homeTeamEditText.text?.clear()
        awayTeam = null
        homePointsEditText.text?.clear()
        awayTeamEditText.text?.clear()
        awayPointsEditText.text?.clear()
        nhaCheckBox.isChecked = false
        rwcCheckBox.isChecked = false
    }

    private fun applyWorldRugbyTeamsToInput(worldRugbyTeams: List<WorldRugbyTeam>) {
        val teams = worldRugbyTeams.map { worldRugbyTeam -> Team.from(requireContext(), worldRugbyTeam) }
        val homeTeamAdapter = NoFilterArrayAdapter(requireContext(), R.layout.list_item_team, teams)
        homeTeamEditText.setAdapter(homeTeamAdapter)
        val awayTeamAdapter = NoFilterArrayAdapter(requireContext(), R.layout.list_item_team, teams)
        awayTeamEditText.setAdapter(awayTeamAdapter)
    }

    override fun dismiss() {
        predictionViewModel.resetPredictionInputValid()
        super.dismiss()
    }

    companion object {
        const val TAG = "PredictionBSDFragment"
        private const val KEY_HOME_TEAM = "home_team"
        private const val KEY_AWAY_TEAM = "away_team"
    }
}
