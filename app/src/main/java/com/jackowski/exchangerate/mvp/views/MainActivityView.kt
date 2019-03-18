package com.jackowski.exchangerate.mvp.views

import com.jackowski.exchangerate.models.Rates
import java.util.*

interface MainActivityView : View {

    fun onDataPrefetched(response: Rates)

    fun onMoreDataFetched(response: Rates)

    fun onLoadingMoreData()

    fun onFetchingDataError()

    fun showOnLoadingScreen()

    fun hideOnLoadingScreen()

    fun onRestoreChanges(hasDataFetched: Boolean, ratesList: ArrayList<Rates?>)

    fun onDataRefreshed(response: Rates?)

    fun onDataIsUpToDate()

    fun onSetLastDataUpdateDate(date: String)

    fun onSetBaseExchange(base: String?)
}