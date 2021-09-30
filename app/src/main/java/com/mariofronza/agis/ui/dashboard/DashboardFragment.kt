package com.mariofronza.agis.ui.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mariofronza.agis.R
import com.mariofronza.agis.models.SemesterResult
import com.mariofronza.agis.ui.semesterresults.SemesterResultsAdapter
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : Fragment() {

    private lateinit var rvSemesterResults: RecyclerView

    private val semesterResults = listOf(
        SemesterResult(
            finalGrade = 8.8,
            courseLoad = 36,
            absencesCount = 0,
            attendancePercentage = 100.0,
            result = "Aprovado",
            subjectName = "Arquitetura de Software",
            groupName = "ESO07 - 2020/1"
        ),
        SemesterResult(
            finalGrade = 10.0,
            courseLoad = 36,
            absencesCount = 0,
            attendancePercentage = 100.0,
            result = "Aprovado",
            subjectName = "Gerência de Configuração",
            groupName = "ESO07 - 2020/1"
        ),
        SemesterResult(
            finalGrade = 7.0,
            courseLoad = 36,
            absencesCount = 0,
            attendancePercentage = 100.0,
            result = "Cursando",
            subjectName = "Inteligência Computacional",
            groupName = "ESO07 - 2020/1"
        ),
        SemesterResult(
            finalGrade = 1.8,
            courseLoad = 72,
            absencesCount = 0,
            attendancePercentage = 100.0,
            result = "Reprovado",
            subjectName = "Gestão do Conhecimento",
            groupName = "ESO07 - 2020/1"
        ),
        SemesterResult(
            finalGrade = 8.8,
            courseLoad = 36,
            absencesCount = 0,
            attendancePercentage = 100.0,
            result = "Aprovado",
            subjectName = "Arquitetura de Software",
            groupName = "ESO07 - 2020/1"
        ),
        SemesterResult(
            finalGrade = 10.0,
            courseLoad = 36,
            absencesCount = 0,
            attendancePercentage = 100.0,
            result = "Aprovado",
            subjectName = "Gerência de Configuração",
            groupName = "ESO07 - 2020/1"
        ),
        SemesterResult(
            finalGrade = 7.0,
            courseLoad = 36,
            absencesCount = 0,
            attendancePercentage = 100.0,
            result = "Cursando",
            subjectName = "Inteligência Computacional",
            groupName = "ESO07 - 2020/1"
        ),
        SemesterResult(
            finalGrade = 1.8,
            courseLoad = 72,
            absencesCount = 0,
            attendancePercentage = 100.0,
            result = "Reprovado",
            subjectName = "Gestão do Conhecimento",
            groupName = "ESO07 - 2020/1"
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        rvSemesterResults = view.findViewById(R.id.rvSemesterResults) as RecyclerView

        val adapter = SemesterResultsAdapter(semesterResults)
        rvSemesterResults.adapter = adapter
        rvSemesterResults.layoutManager = LinearLayoutManager(requireActivity())

        return view
    }

}