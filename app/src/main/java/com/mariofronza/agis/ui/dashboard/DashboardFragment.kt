package com.mariofronza.agis.ui.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.simplepass.loadingbutton.customViews.CircularProgressImageButton
import com.mariofronza.agis.R
import com.mariofronza.agis.api.ApiService
import com.mariofronza.agis.data.DashboardRepository
import com.mariofronza.agis.models.DashboardInfoResponse
import com.mariofronza.agis.models.Period
import com.mariofronza.agis.models.SemesterResult
import com.mariofronza.agis.ui.auth.SignInActivity
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
        btnSignOut = view.findViewById(R.id.btnSignOut) as CircularProgressImageButton
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

        btnSignOut.setOnClickListener {
            btnSignOut.startAnimation()
            removeOneSignalExternalId()
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


    private fun signOut() {
        btnSignOut.revertAnimation()
        sessionManager.removeTokens()
        Intent(activity, SignInActivity::class.java).also {
            startActivity(it)
        }
    }


}