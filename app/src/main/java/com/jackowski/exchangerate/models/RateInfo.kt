package com.jackowski.exchangerate.models

class RateInfo(
    val timestamp: Number?,
    val base: String?,
    val date: String?, val ratesMap: Map.Entry<String, Double>?
)
