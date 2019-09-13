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
import com.ricknout.rugbyranker.core.util.FlagUtils
import com.ricknout.rugbyranker.core.vo.Sport
import com.ricknout.rugbyranker.prediction.R
import com.ricknout.rugbyranker.prediction.vo.Prediction
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

    private var homeTeamId: Long? = null
    private var homeTeamName: String? = null
    private var homeTeamAbbreviation: String? = null
    private var awayTeamId: Long? = null
    private var awayTeamName: String? = null
    private var awayTeamAbbreviation: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.bottom_sheet_dialog_fragment_prediction, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModels()
        setupEditTexts()
        if (savedInstanceState == null) {
            if (prediction != null) applyPredictionToInput(prediction!!)
        } else {
            homeTeamId = savedInstanceState.getLong(KEY_HOME_TEAM_ID)
            homeTeamName = savedInstanceState.getString(KEY_HOME_TEAM_NAME)
            homeTeamAbbreviation = savedInstanceState.getString(KEY_HOME_TEAM_ABBREVIATION)
            awayTeamId = savedInstanceState.getLong(KEY_AWAY_TEAM_ID)
            awayTeamName = savedInstanceState.getString(KEY_AWAY_TEAM_NAME)
            awayTeamAbbreviation = savedInstanceState.getString(KEY_AWAY_TEAM_ABBREVIATION)
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
        homeTeamId?.let { homeTeamId ->
            outState.putLong(KEY_HOME_TEAM_ID, homeTeamId)
        }
        outState.putString(KEY_HOME_TEAM_NAME, homeTeamName)
        outState.putString(KEY_HOME_TEAM_ABBREVIATION, homeTeamAbbreviation)
        awayTeamId?.let { awayTeamId ->
            outState.putLong(KEY_AWAY_TEAM_ID, awayTeamId)
        }
        outState.putString(KEY_AWAY_TEAM_NAME, awayTeamName)
        outState.putString(KEY_AWAY_TEAM_ABBREVIATION, awayTeamAbbreviation)
    }

    private fun setupCloseButton() {
        closeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun setupEditTexts() {
        homeTeamEditText.apply {
            onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                val worldRugbyTeam = teamsViewModel.getLatestWorldRugbyTeam(position) ?: return@OnItemClickListener
                val teamId = worldRugbyTeam.id
                val teamName = worldRugbyTeam.name
                val teamAbbreviation = worldRugbyTeam.abbreviation
                if (teamId == awayTeamId) {
                    val teamText = if (homeTeamAbbreviation == null || homeTeamName == null) {
                        null
                    } else {
                        getTeamText(homeTeamAbbreviation!!, homeTeamName!!)
                    }
                    setText(teamText, false)
                    return@OnItemClickListener
                }
                homeTeamId = teamId
                homeTeamName = teamName
                homeTeamAbbreviation = teamAbbreviation
                val teamText = getTeamText(teamAbbreviation, teamName)
                setText(teamText, false)
            }
            doOnTextChanged { text, _, _, _ ->
                val valid = !text.isNullOrEmpty()
                predictionViewModel.homeTeamInputValid.value = valid
            }
        }
        awayTeamEditText.apply {
            onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                val worldRugbyTeam = teamsViewModel.getLatestWorldRugbyTeam(position) ?: return@OnItemClickListener
                val teamId = worldRugbyTeam.id
                val teamName = worldRugbyTeam.name
                val teamAbbreviation = worldRugbyTeam.abbreviation
                if (teamId == homeTeamId) {
                    val teamText = if (awayTeamAbbreviation == null || awayTeamName == null) {
                        null
                    } else {
                        getTeamText(awayTeamAbbreviation!!, awayTeamName!!)
                    }
                    setText(teamText, false)
                    return@OnItemClickListener
                }
                awayTeamId = teamId
                awayTeamName = teamName
                awayTeamAbbreviation = teamAbbreviation
                val teamText = getTeamText(teamAbbreviation, teamName)
                setText(teamText, false)
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
        homeTeamId = prediction.homeTeamId
        homeTeamName = prediction.homeTeamName
        homeTeamAbbreviation = prediction.homeTeamAbbreviation
        val homeTeamText = getTeamText(prediction.homeTeamAbbreviation, homeTeamName!!)
        homeTeamEditText.setText(homeTeamText, false)
        if (prediction.homeTeamScore != Prediction.NO_SCORE) {
            homePointsEditText.setText(prediction.homeTeamScore.toString())
        }
        awayTeamId = prediction.awayTeamId
        awayTeamName = prediction.awayTeamName
        awayTeamAbbreviation = prediction.awayTeamAbbreviation
        val awayTeamText = getTeamText(prediction.awayTeamAbbreviation, awayTeamName!!)
        awayTeamEditText.setText(awayTeamText, false)
        if (prediction.awayTeamScore != Prediction.NO_SCORE) {
            awayPointsEditText.setText(prediction.awayTeamScore.toString())
        }
        nhaCheckBox.isChecked = prediction.noHomeAdvantage
        rwcCheckBox.isChecked = prediction.rugbyWorldCup
    }

    private fun addOrEditPredictionFromInput(): Boolean {
        val homeTeamId = homeTeamId ?: return false
        val homeTeamName = homeTeamName ?: return false
        val homeTeamAbbreviation = homeTeamAbbreviation ?: return false
        if (homePointsEditText.text.isNullOrEmpty()) {
            return false
        }
        val homeTeamScore = homePointsEditText.text.toString().toInt()
        val awayTeamId = awayTeamId ?: return false
        val awayTeamName = awayTeamName ?: return false
        val awayTeamAbbreviation = awayTeamAbbreviation ?: return false
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
                homeTeamId = homeTeamId,
                homeTeamName = homeTeamName,
                homeTeamAbbreviation = homeTeamAbbreviation,
                homeTeamScore = homeTeamScore,
                awayTeamId = awayTeamId,
                awayTeamName = awayTeamName,
                awayTeamAbbreviation = awayTeamAbbreviation,
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
        homeTeamEditText.text?.clear()
        homePointsEditText.text?.clear()
        awayTeamEditText.text?.clear()
        awayPointsEditText.text?.clear()
        nhaCheckBox.isChecked = false
        rwcCheckBox.isChecked = false
    }

    private fun applyWorldRugbyTeamsToInput(worldRugbyTeams: List<WorldRugbyTeam>) {
        val teams = worldRugbyTeams.map { worldRugbyTeam ->
            getTeamText(worldRugbyTeam.abbreviation, worldRugbyTeam.name)
        }
        val homeTeamAdapter = NoFilterArrayAdapter(requireContext(), R.layout.list_item_team, teams)
        homeTeamEditText.setAdapter(homeTeamAdapter)
        val awayTeamAdapter = NoFilterArrayAdapter(requireContext(), R.layout.list_item_team, teams)
        awayTeamEditText.setAdapter(awayTeamAdapter)
    }

    private fun getTeamText(teamAbbreviation: String, teamName: String) =
            getString(R.string.menu_item_team, FlagUtils.getFlagEmojiForTeamAbbreviation(teamAbbreviation), teamName)

    override fun dismiss() {
        predictionViewModel.resetPredictionInputValid()
        super.dismiss()
    }

    companion object {
        const val TAG = "PredictionBottomSheetDF"
        private const val KEY_HOME_TEAM_ID = "home_team_id"
        private const val KEY_HOME_TEAM_NAME = "home_team_name"
        private const val KEY_HOME_TEAM_ABBREVIATION = "home_team_abbreviation"
        private const val KEY_AWAY_TEAM_ID = "away_team_id"
        private const val KEY_AWAY_TEAM_NAME = "away_team_name"
        private const val KEY_AWAY_TEAM_ABBREVIATION = "away_team_abbreviation"
    }
}
