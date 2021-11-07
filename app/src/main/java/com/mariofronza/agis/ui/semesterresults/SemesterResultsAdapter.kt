package com.mariofronza.agis.ui.semesterresults

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mariofronza.agis.R
import com.mariofronza.agis.models.SemesterResult
import kotlinx.android.synthetic.main.subject_info_item.view.*

class SemesterResultsAdapter(
    private val semesterResults: List<SemesterResult>
) : RecyclerView.Adapter<SemesterResultsAdapter.SemesterResultsViewHolder>() {

    inner class SemesterResultsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SemesterResultsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.subject_info_item,
            parent,
            false
        )
        return SemesterResultsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SemesterResultsViewHolder, position: Int) {
        val semesterResult = semesterResults[position]

        holder.itemView.apply {
            if (position % 2 == 0) {
                cvSubjectItem.setBackgroundResource(R.color.adapter_blue_light)
            } else {
                cvSubjectItem.setBackgroundResource(R.color.adapter_blue_dark)
            }

            val result = semesterResult.result

            tvNameSubjectItem.text = semesterResult.subjectName

            tvFinalGradeSubjectItem.text = semesterResult.finalGrade.toString()
            tvFinalGradeSubjectItem.setTextColor(getStatusColorByResult(result, context))

            cpStatusSubjectInfo.text = result
            cpStatusSubjectInfo.chipBackgroundColor = getStatusColorByResult(result, context)
        }
    }

    override fun getItemCount(): Int {
        return semesterResults.size
    }

    private fun getStatusColorByResult(result: String, context: Context): ColorStateList {
        return when (result) {
            "Aprovado" -> ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    R.color.approved_status
                )
            )
            "Cursando" -> ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    R.color.attending_status
                )
            )
            "Reprovado Nota" -> ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    R.color.reproved_color
                )
            )
            else -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
        }
    }
}