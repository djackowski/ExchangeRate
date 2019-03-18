package com.jackowski.exchangerate.models

import java.util.*

class Rates(
    val success: Boolean?, val timestamp: Number?,
    val historical: Boolean?, val base: String?,
    val date: String?, val rates: TreeMap<String, Double>?
)