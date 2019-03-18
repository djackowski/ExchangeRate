package com.jackowski.exchangerate.mvp.presenters

import android.util.Log
import com.jackowski.exchangerate.api.Service
import com.jackowski.exchangerate.models.Rates
import com.jackowski.exchangerate.mvp.views.MainActivityView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*


const val DATE_FORMAT = "yyyy-MM-dd"
const val DETAILS_DATE_FORMAT = "yyyy-MM-dd HH:mm"
const val DAYS_OFFSET = -1

class MainActivityPresenter : Presenter<MainActivityView>() {

    private lateinit var service: Service
    private lateinit var dateToFetch: String
    private var fetchDisposable: Disposable? = null
    private var prefetchDataFailed: Boolean = false
    private var loadMoreDataFailed: Boolean = false
    private var hasPrefetchedData: Boolean = false
    private var ratesList = ArrayList<Rates?>()
    private var onRestoreChanges: Boolean = false
    private var lastFetched: String? = null
    private var base: String? = null

    fun setService(service: Service) {
        this.service = service
    }

    override fun onAttach(view: MainActivityView) {
        super.onAttach(view)
        if (onRestoreChanges) {
            view.onRestoreChanges(true, ratesList)
            view.onSetLastDataUpdateDate(lastFetched!!)
            view.onSetBaseExchange(base)
        }

        prefetchData()
    }

    override fun onDetach() {
        fetchDisposable?.dispose()
        super.onDetach()
    }

    private fun prefetchData() {
        if (hasPrefetchedData) return

        view?.showOnLoadingScreen()

        fetchLatestData()
    }

    private fun fetchLatestData() {
        fetchDisposable = service.getLatestRates()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                ratesList.add(response)
                view?.hideOnLoadingScreen()
                dateToFetch = getNextDateToFetch(response.date.toString())
                view?.onDataPrefetched(response)
                lastFetched = getCurrentDate()
                view?.onSetLastDataUpdateDate(lastFetched!!)
                base = response.base
                view?.onSetBaseExchange(base)
                prefetchDataFailed = false
                hasPrefetchedData = true
            }, { error -> onPrefetchDataError(error) })
    }

    private fun onPrefetchDataError(error: Throwable) {
        showError(error)
        prefetchDataFailed = true
    }

    fun onLoadMore() {
        if (!hasPrefetchedData) return

        if (!loadMoreDataFailed) {
            view?.onLoadingMoreData()
        }

        view?.showOnLoadingScreen()
        fetchDisposable = service.getRatesFromDate(dateToFetch)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                ratesList.add(response)
                view?.onMoreDataFetched(response)
                dateToFetch = getNextDateToFetch(response.date.toString())
                loadMoreDataFailed = false
                view?.hideOnLoadingScreen()
            }, { error -> onLoadMoreDataError(error) })
    }

    private fun onLoadMoreDataError(error: Throwable) {
        showError(error)
        loadMoreDataFailed = true
    }

    private fun showError(error: Throwable) {
        view?.onFetchingDataError()
        error.printStackTrace()
    }

    private fun getNextDateToFetch(responseDate: String): String {
        val simpleDateFormat = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)
        val date = simpleDateFormat.parse(responseDate)
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_MONTH, DAYS_OFFSET)
        return simpleDateFormat.format(calendar.time).toString()
    }

    private fun getCurrentDate(): String {
        val simpleDataFormat = SimpleDateFormat(DETAILS_DATE_FORMAT, Locale.ENGLISH)
        val date = Date()
        return simpleDataFormat.format(date)
    }

    fun onInternetConnectionAvailable() {
        if (prefetchDataFailed) {
            prefetchData()
        }

        if (loadMoreDataFailed) {
            onLoadMore()
        }
    }

    fun setOnRestoreChanges(onRestoreChanges: Boolean) {
        this.onRestoreChanges = onRestoreChanges
    }

    fun onRefreshData() {
        fetchDisposable = service.getLatestRates()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                if (ratesList[0]?.timestamp != response.timestamp) {
                    if(ratesList.first()?.date == response.date) {
                        ratesList[0] = response
                    } else {
                        ratesList.add(0, response)
                    }
                    view?.onDataRefreshed(response)
                } else {
                    view?.onDataIsUpToDate()
                }
                lastFetched = getCurrentDate()
                view?.onSetLastDataUpdateDate(lastFetched!!)
            }, { error -> onPrefetchDataError(error) })

    }

}