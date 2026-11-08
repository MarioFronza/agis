package com.mariofronza.agis.ui.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mariofronza.agis.R
import com.mariofronza.agis.api.ApiService
import com.mariofronza.agis.data.DashboardRepository
import com.mariofronza.agis.data.RetrieveRepository
import com.mariofronza.agis.models.DashboardInfoResponse
import com.mariofronza.agis.models.Period
import com.mariofronza.agis.models.SemesterResult
import com.mariofronza.agis.ui.ViewModelFactory
import com.mariofronza.agis.ui.auth.SignInActivity
import com.mariofronza.agis.ui.retrieve.RetrieveViewModel
import com.mariofronza.agis.ui.semesterresults.SemesterResultsAdapter
import com.mariofronza.agis.utils.SessionManager
import com.onesignal.OneSignal
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.json.JSONObject

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private lateinit var sessionManager: SessionManager
    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var retrieveViewModel: RetrieveViewModel

    private val semesterResults = mutableListOf<SemesterResult>()
    private val adapter = SemesterResultsAdapter(semesterResults)
    private var currentPeriod: Period? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        val api = ApiService()
        dashboardViewModel = ViewModelProvider(this, ViewModelFactory { DashboardViewModel(DashboardRepository(api)) })
            .get(DashboardViewModel::class.java)
        retrieveViewModel = ViewModelProvider(this, ViewModelFactory { RetrieveViewModel(RetrieveRepository(api)) })
            .get(RetrieveViewModel::class.java)

        rvSemesterResults.adapter = adapter
        rvSemesterResults.layoutManager = LinearLayoutManager(requireContext())

        dashboardViewModel.dashboardInfoResult.observe(viewLifecycleOwner, ::setDashboardInfo)

        dashboardViewModel.dashboardResult.observe(viewLifecycleOwner) {
            semesterResults.clear()
            semesterResults.addAll(it.results)
            adapter.notifyDataSetChanged()
            showList()
        }

        dashboardViewModel.error.observe(viewLifecycleOwner) { message ->
            showList()
            toast(message)
        }

        retrieveViewModel.retrieveResult.observe(viewLifecycleOwner) {
            setSyncing(false)
            currentPeriod?.let(::loadDashboard)
        }

        retrieveViewModel.error.observe(viewLifecycleOwner) {
            setSyncing(false)
            toast("Erro ao extrair informações do período ${currentPeriod?.label()}")
        }

        powerSpinner.setOnSpinnerItemSelectedListener<String> { _, _, _, label ->
            val (year, term) = label.split("/")
            Period(year, term.toInt()).also {
                currentPeriod = it
                loadDashboard(it)
            }
        }

        btnSyncDashboard.setOnClickListener { currentPeriod?.let(::startRetrieve) }

        btnSignOut.setOnClickListener {
            btnSignOut.isEnabled = false
            toast("Aguarde...")
            signOut()
        }

        if (dashboardViewModel.dashboardInfoResult.value == null) loadDashboardInfo()
    }

    @SuppressLint("SetTextI18n")
    private fun setDashboardInfo(info: DashboardInfoResponse) {
        tvUsernameDashboard.text = "Olá, ${info.username}"
        powerSpinner.setItems(info.periods.map(Period::label))
        // Selecting an item fires the spinner listener, which loads the dashboard.
        if (info.periods.isNotEmpty()) powerSpinner.selectItemByIndex(0)
    }

    private fun loadDashboardInfo() {
        sessionManager.fetchAccessToken()?.let(dashboardViewModel::dashboardInfo)
    }

    private fun loadDashboard(period: Period) {
        showLoading()
        sessionManager.fetchAccessToken()?.let {
            dashboardViewModel.dashboard(it, period.year, period.term.toString())
        }
    }

    private fun startRetrieve(period: Period) {
        setSyncing(true)
        toast("Extraindo informações do período: ${period.label()}")
        sessionManager.fetchAccessToken()?.let {
            retrieveViewModel.retrieve(it, period.year, period.term.toString())
        }
    }

    private fun setSyncing(syncing: Boolean) {
        btnSyncDashboard.isEnabled = !syncing
        powerSpinner.isEnabled = !syncing
    }

    private fun showLoading() {
        btnSyncDashboard.isEnabled = false
        pbListLoading.visibility = VISIBLE
        rvSemesterResults.visibility = GONE
    }

    private fun showList() {
        btnSyncDashboard.isEnabled = true
        pbListLoading.visibility = GONE
        rvSemesterResults.visibility = VISIBLE
    }

    // OneSignal callbacks run off the main thread.
    private fun signOut() {
        OneSignal.removeExternalUserId(object : OneSignal.OSExternalUserIdUpdateCompletionHandler {
            override fun onSuccess(results: JSONObject?) = requireActivity().runOnUiThread(::clearSession)
            override fun onFailure(error: OneSignal.ExternalIdError?) = requireActivity().runOnUiThread(::clearSession)
        })
    }

    private fun clearSession() {
        sessionManager.clear()
        startActivity(Intent(requireContext(), SignInActivity::class.java))
        requireActivity().finish()
    }

    private fun toast(message: String?) = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}
