package com.jackowski.exchangerate.adapters

import com.jackowski.exchangerate.models.RateInfo

interface OnClickSingleRateListener {
    fun onClickItem(currencyRateItem: RateInfo)
}