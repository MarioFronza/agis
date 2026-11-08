package com.mariofronza.agis.ui.semesterresults

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mariofronza.agis.R
import com.mariofronza.agis.databinding.SubjectInfoItemBinding
import com.mariofronza.agis.models.SemesterResult

class SemesterResultsAdapter(
    private val semesterResults: List<SemesterResult>
) : RecyclerView.Adapter<SemesterResultsAdapter.SemesterResultsViewHolder>() {

    class SemesterResultsViewHolder(val binding: SubjectInfoItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SemesterResultsViewHolder(
        SubjectInfoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: SemesterResultsViewHolder, position: Int) {
        val semesterResult = semesterResults[position]
        val context = holder.binding.root.context
        val statusColor = ColorStateList.valueOf(
            ContextCompat.getColor(context, STATUS_COLORS[semesterResult.result] ?: R.color.white)
        )

        with(holder.binding) {
            cvSubjectItem.setBackgroundResource(
                if (position % 2 == 0) R.color.adapter_blue_light else R.color.adapter_blue_dark
            )
            tvNameSubjectItem.text = semesterResult.subjectName
            tvFinalGradeSubjectItem.text = semesterResult.finalGrade.toString()
            tvFinalGradeSubjectItem.setTextColor(statusColor)
            cpStatusSubjectInfo.text = semesterResult.result
            cpStatusSubjectInfo.chipBackgroundColor = statusColor
        }
    }

    override fun getItemCount() = semesterResults.size

    companion object {
        private val STATUS_COLORS = mapOf(
            "Aprovado" to R.color.approved_status,
            "Cursando" to R.color.attending_status,
            "Reprovado Nota" to R.color.reproved_color,
        )
    }
}
