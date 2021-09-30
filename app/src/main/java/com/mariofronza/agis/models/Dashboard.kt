package com.mariofronza.agis.models

data class Dashboard(
    val period: Period,
    val results: List<SemesterResult>
)