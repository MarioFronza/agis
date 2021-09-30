package com.mariofronza.agis.models

data class SemesterResult(
    val finalGrade: Double,
    val courseLoad: Int,
    val absencesCount: Int,
    val attendancePercentage: Double,
    val result: String,
    val subjectName: String,
    val groupName: String
)