package com.mariofronza.agis.ui.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mariofronza.agis.R
import com.mariofronza.agis.api.ApiService
import com.mariofronza.agis.data.DashboardRepository
import com.mariofronza.agis.models.DashboardInfoResponse
import com.mariofronza.agis.models.Period
import com.mariofronza.agis.models.SemesterResult
import com.mariofronza.agis.ui.semesterresults.SemesterResultsAdapter
import com.mariofronza.agis.utils.SessionManager
import com.skydoves.powerspinner.PowerSpinnerView
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var rvSemesterResults: RecyclerView
    private lateinit var powerSpinner: PowerSpinnerView
    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var dashboardViewModelFactory: DashboardViewModelFactory

    private val semesterResults = mutableListOf<SemesterResult>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val api = ApiService()
        val dashboardRepository = DashboardRepository(api)

        rvSemesterResults = view.findViewById(R.id.rvSemesterResults) as RecyclerView
        powerSpinner = view.findViewById(R.id.powerSpinner) as PowerSpinnerView

        sessionManager = SessionManager(requireActivity())
        dashboardViewModelFactory = DashboardViewModelFactory(dashboardRepository)
        dashboardViewModel =
            ViewModelProvider(this, dashboardViewModelFactory).get(DashboardViewModel::class.java)

        val adapter = SemesterResultsAdapter(semesterResults)
        rvSemesterResults.adapter = adapter
        rvSemesterResults.layoutManager = LinearLayoutManager(requireActivity())

        dashboardViewModel.dashboardInfoResult.observe(requireActivity(), Observer {
            setDashboardInfo(it)
        })

        dashboardViewModel.dashboardResult.observe(requireActivity(), Observer {
            semesterResults.clear()
            semesterResults.addAll(it.results)
            adapter.notifyDataSetChanged()
        })

        getDashboardInfo()

        powerSpinner.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newText ->
            newText.split("/").let {
                val year = it.first()
                val term = it.last()
                getDashboard(Period(year, term.toInt()))
            }
        }

        return view
    }

    @SuppressLint("SetTextI18n")
    private fun setDashboardInfo(info: DashboardInfoResponse) {
        tvUsernameDashboard.text = "Olá, ${info.username}"
        powerSpinner.setItems(info.periods.map { "${it.year}/${it.term}" })
        powerSpinner.selectItemByIndex(0)
        val firstPeriod = info.periods.first()
        getDashboard(firstPeriod)
    }

    private fun getDashboardInfo() {
        sessionManager.fetchAccessToken()?.let {
            dashboardViewModel.dashboardInfo(it)
        }
    }

    private fun getDashboard(firstPeriod: Period) {
        sessionManager.fetchAccessToken()?.let {
            dashboardViewModel.dashboard(it, firstPeriod.year, firstPeriod.term.toString())
        }
    }

}