package com.mariofronza.agis.ui.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import br.com.simplepass.loadingbutton.customViews.CircularProgressImageButton
import com.mariofronza.agis.R
import com.mariofronza.agis.api.ApiService
import com.mariofronza.agis.data.DashboardRepository
import com.mariofronza.agis.data.RetrieveRepository
import com.mariofronza.agis.models.DashboardInfoResponse
import com.mariofronza.agis.models.Period
import com.mariofronza.agis.models.SemesterResult
import com.mariofronza.agis.ui.auth.SignInActivity
import com.mariofronza.agis.ui.retrieve.RetrieveViewModel
import com.mariofronza.agis.ui.retrieve.RetrieveViewModelFactory
import com.mariofronza.agis.ui.semesterresults.SemesterResultsAdapter
import com.mariofronza.agis.utils.SessionManager
import com.onesignal.OneSignal
import com.skydoves.powerspinner.PowerSpinnerView
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.json.JSONObject

class DashboardFragment : Fragment() {

    private lateinit var sessionManager: SessionManager

    private lateinit var rvSemesterResults: RecyclerView
    private lateinit var btnSignOut: CircularProgressImageButton
    private lateinit var powerSpinner: PowerSpinnerView
    private lateinit var btnSyncDashboard: CircularProgressButton
    private lateinit var pbListLoading: ProgressBar

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var retrieveViewModel: RetrieveViewModel
    private lateinit var dashboardViewModelFactory: DashboardViewModelFactory
    private lateinit var retrieveViewModelFactory: RetrieveViewModelFactory

    private val semesterResults = mutableListOf<SemesterResult>()
    private lateinit var currentPeriod: Period

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        sessionManager = SessionManager(requireActivity())
        val api = ApiService()

        initUiComponents(view)
        initDashboardViewModel(api)
        initRetrieveViewModel(api)

        val adapter = SemesterResultsAdapter(semesterResults)
        rvSemesterResults.adapter = adapter
        rvSemesterResults.layoutManager = LinearLayoutManager(requireActivity())

        dashboardViewModel.dashboardInfoResult.observe(requireActivity(), Observer {
            setDashboardInfo(it)
        })

        dashboardViewModel.dashboardResult.observe(requireActivity(), Observer {
            semesterResults.clear()
            semesterResults.addAll(it.results)
            goneLoadingComponents()
            adapter.notifyDataSetChanged()
        })

        retrieveViewModel.retrieveResult.observe(requireActivity(), Observer {
            pauseRetrieveLoadingComponents()
            getDashboardInfo()
        })

        retrieveViewModel.error.observe(requireActivity(), Observer {
            pauseRetrieveLoadingComponents()
            toastMessage("Erro ao extrair informações do período ${getCurrentPeriodString()}")
        })

        getDashboardInfo()

        powerSpinner.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newText ->
            newText.split("/").let {
                val year = it.first()
                val term = it.last()
                currentPeriod = Period(year, term.toInt())
                getDashboard(currentPeriod)
            }
        }


        btnSyncDashboard.setOnClickListener {
            btnSyncDashboard.startAnimation()
            startRetrieve(currentPeriod)
        }

        btnSignOut.setOnClickListener {
            btnSignOut.startAnimation()
            removeOneSignalExternalId()
        }

        return view
    }

    private fun startRetrieve(currentPeriod: Period) {
        initRetrieveLoadingComponents()
        toastMessage("Extraindo informações do período: ${getCurrentPeriodString()}")
        sessionManager.fetchAccessToken()?.let {
            retrieveViewModel.retrieve(it, currentPeriod.year, currentPeriod.term.toString())
        }
    }

    private fun getCurrentPeriodString() = "${currentPeriod.year}/${currentPeriod.term}"

    private fun initRetrieveLoadingComponents() {
        btnSyncDashboard.startAnimation()
        powerSpinner.isEnabled = false
    }

    private fun pauseRetrieveLoadingComponents() {
        powerSpinner.isEnabled = true
        btnSyncDashboard.revertAnimation()
    }

    private fun initUiComponents(view: View) {
        rvSemesterResults = view.findViewById(R.id.rvSemesterResults) as RecyclerView
        btnSignOut = view.findViewById(R.id.btnSignOut) as CircularProgressImageButton
        powerSpinner = view.findViewById(R.id.powerSpinner) as PowerSpinnerView
        pbListLoading = view.findViewById(R.id.pbListLoading) as ProgressBar
        btnSyncDashboard = view.findViewById(R.id.btnSyncDashboard) as CircularProgressButton
        btnSyncDashboard.setBackgroundResource(R.drawable.background_button)
        btnSyncDashboard.spinningBarColor = Color.WHITE
    }


    private fun initDashboardViewModel(api: ApiService) {
        val dashboardRepository = DashboardRepository(api)
        dashboardViewModelFactory = DashboardViewModelFactory(dashboardRepository)
        dashboardViewModel =
            ViewModelProvider(this, dashboardViewModelFactory).get(DashboardViewModel::class.java)
    }


    private fun initRetrieveViewModel(api: ApiService) {
        val retrieveRepository = RetrieveRepository(api)
        retrieveViewModelFactory = RetrieveViewModelFactory(retrieveRepository)
        retrieveViewModel =
            ViewModelProvider(this, retrieveViewModelFactory).get(RetrieveViewModel::class.java)

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
        initLoadingComponents()
        sessionManager.fetchAccessToken()?.let {
            dashboardViewModel.dashboard(it, firstPeriod.year, firstPeriod.term.toString())
        }
    }

    private fun removeOneSignalExternalId() {
        Toast.makeText(activity, "Aguarde...", Toast.LENGTH_SHORT).show()
        OneSignal.removeExternalUserId(object : OneSignal.OSExternalUserIdUpdateCompletionHandler {
            override fun onSuccess(results: JSONObject?) {
                signOut()
            }

            override fun onFailure(error: OneSignal.ExternalIdError?) {
                signOut()
            }
        })
    }

    private fun initLoadingComponents() {
        btnSyncDashboard.isEnabled = false
        pbListLoading.visibility = VISIBLE
        rvSemesterResults.visibility = GONE
    }

    private fun goneLoadingComponents() {
        btnSyncDashboard.isEnabled = true
        pbListLoading.visibility = GONE
        rvSemesterResults.visibility = VISIBLE
    }


    private fun signOut() {
        btnSignOut.revertAnimation()
        sessionManager.removeTokens()
        Intent(activity, SignInActivity::class.java).also {
            startActivity(it)
        }
    }

    private fun toastMessage(message: String?) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }


}