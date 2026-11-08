package com.mariofronza.agis.models

data class Period(
    val year: String,
    val term: Int,
) {
    fun label() = "$year/$term"
}
