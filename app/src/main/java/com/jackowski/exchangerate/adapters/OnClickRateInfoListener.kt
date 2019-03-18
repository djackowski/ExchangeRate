package com.jackowski.exchangerate.adapters

import com.jackowski.exchangerate.models.RateInfo

interface OnClickRateInfoListener {
    fun onRateClicked(rateInfo: RateInfo)
}