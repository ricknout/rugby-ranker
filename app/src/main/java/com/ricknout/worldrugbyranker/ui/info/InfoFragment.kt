package com.ricknout.worldrugbyranker.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ricknout.worldrugbyranker.R
import kotlinx.android.synthetic.main.fragment_info.*
import android.content.Intent
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.ricknout.worldrugbyranker.BuildConfig
import com.ricknout.worldrugbyranker.util.IntentUtils

class InfoFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_info, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        howAreWorldRugbyRankingsCalculatedButton.setOnClickListener {
            IntentUtils.launchCustomTab(requireContext(), RANKINGS_EXPLANATION_URL)
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
            IntentUtils.launchCustomTab(requireContext(), GITHUB_URL)
        }
        openSourceLicensesButton.setOnClickListener {
            val intent = Intent(requireContext(), OssLicensesMenuActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        const val TAG = "InfoFragment"
        private const val RANKINGS_EXPLANATION_URL = "https://www.world.rugby/rankings/explanation"
        private const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
        private const val GITHUB_URL = "https://github.com/nicholasrout/world-rugby-ranker"
    }
}
