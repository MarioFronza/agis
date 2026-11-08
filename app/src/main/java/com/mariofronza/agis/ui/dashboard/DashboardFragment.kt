package com.mariofronza.agis.ui.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import com.mariofronza.agis.R
import com.mariofronza.agis.api.ApiService
import com.mariofronza.agis.data.DashboardRepository
import com.mariofronza.agis.data.RetrieveRepository
import com.mariofronza.agis.databinding.FragmentDashboardBinding
import com.mariofronza.agis.models.DashboardInfoResponse
import com.mariofronza.agis.models.Period
import com.mariofronza.agis.models.SemesterResult
import com.mariofronza.agis.ui.auth.SignInActivity
import com.mariofronza.agis.ui.retrieve.RetrieveViewModel
import com.mariofronza.agis.ui.semesterresults.SemesterResultsAdapter
import com.mariofronza.agis.utils.SessionManager
import com.onesignal.OneSignal

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private val dashboardViewModel: DashboardViewModel by viewModels {
        viewModelFactory { initializer { DashboardViewModel(DashboardRepository(ApiService())) } }
    }
    private val retrieveViewModel: RetrieveViewModel by viewModels {
        viewModelFactory { initializer { RetrieveViewModel(RetrieveRepository(ApiService())) } }
    }

    private val semesterResults = mutableListOf<SemesterResult>()
    private val adapter = SemesterResultsAdapter(semesterResults)
    private var currentPeriod: Period? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)
        sessionManager = SessionManager(requireContext())

        binding.rvSemesterResults.adapter = adapter
        binding.rvSemesterResults.layoutManager = LinearLayoutManager(requireContext())

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

        binding.powerSpinner.setOnSpinnerItemSelectedListener<String> { _, _, _, label ->
            val (year, term) = label.split("/")
            Period(year, term.toInt()).also {
                currentPeriod = it
                loadDashboard(it)
            }
        }

        binding.btnSyncDashboard.setOnClickListener { currentPeriod?.let(::startRetrieve) }

        binding.btnSignOut.setOnClickListener {
            signOut()
        }

        if (dashboardViewModel.dashboardInfoResult.value == null) loadDashboardInfo()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("SetTextI18n")
    private fun setDashboardInfo(info: DashboardInfoResponse) {
        binding.tvUsernameDashboard.text = "Olá, ${info.username}"
        binding.powerSpinner.setItems(info.periods.map(Period::label))
        // Selecting an item fires the spinner listener, which loads the dashboard.
        if (info.periods.isNotEmpty()) binding.powerSpinner.selectItemByIndex(0)
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
        binding.btnSyncDashboard.isEnabled = !syncing
        binding.powerSpinner.isEnabled = !syncing
    }

    private fun showLoading() {
        binding.btnSyncDashboard.isEnabled = false
        binding.pbListLoading.visibility = VISIBLE
        binding.rvSemesterResults.visibility = GONE
    }

    private fun showList() {
        binding.btnSyncDashboard.isEnabled = true
        binding.pbListLoading.visibility = GONE
        binding.rvSemesterResults.visibility = VISIBLE
    }

    private fun signOut() {
        OneSignal.removeExternalUserId()
        sessionManager.clear()
        startActivity(Intent(requireContext(), SignInActivity::class.java))
        requireActivity().finish()
    }

    private fun toast(message: String?) = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}
