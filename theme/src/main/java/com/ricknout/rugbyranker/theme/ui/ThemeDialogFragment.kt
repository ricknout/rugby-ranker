package com.ricknout.rugbyranker.theme.ui

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ricknout.rugbyranker.theme.R
import dagger.android.support.DaggerDialogFragment
import javax.inject.Inject

class ThemeDialogFragment : DaggerDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: ThemeViewModel by viewModels({ requireActivity() }, { viewModelFactory })

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val themes = viewModel.getThemes()
        val themeTitles = themes.map { theme -> getString(theme.titleResId) }.toTypedArray()
        val theme = viewModel.getTheme()
        val checkedItem = themes.indexOf(theme)
        return MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.title_choose_theme)
                .setSingleChoiceItems(themeTitles, checkedItem) { _, which ->
                    val whichTheme = themes[which]
                    viewModel.setTheme(whichTheme)
                    dismiss()
                }
                .create()
    }
}
