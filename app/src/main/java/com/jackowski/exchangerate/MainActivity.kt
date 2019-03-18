package com.jackowski.exchangerate

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import com.jackowski.exchangerate.adapters.AllRatesAdapter
import com.jackowski.exchangerate.adapters.OnClickRateInfoListener
import com.jackowski.exchangerate.adapters.OnLoadMoreListener
import com.jackowski.exchangerate.api.Service
import com.jackowski.exchangerate.models.RateInfo
import com.jackowski.exchangerate.models.Rates
import com.jackowski.exchangerate.mvp.presenters.MainActivityPresenter
import com.jackowski.exchangerate.mvp.views.MainActivityView
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


//Tests to presenters
//comments
class MainActivity : BaseInternetConnectionActivity(), OnLoadMoreListener, OnClickRateInfoListener, MainActivityView {

    private val service: Service = Service.create(this)

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AllRatesAdapter
    private lateinit var presenter: MainActivityPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.setDisplayShowTitleEnabled(false)

        presenter = ViewModelProviders.of(this).get(MainActivityPresenter::class.java)
        presenter.setService(service)
        presenter.setOnRestoreChanges(savedInstanceState != null)

        initializeData()
    }

    override fun onResume() {
        super.onResume()
        presenter.onAttach(this)
    }

    override fun onPause() {
        presenter.onDetach()
        super.onPause()
    }

    private fun initializeData() {
        recyclerView = findViewById(R.id.all_rates)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AllRatesAdapter(this, this, this, recyclerView)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.addItemDecoration(StickyRecyclerHeadersDecoration(adapter))


        go_up_button.setOnClickListener {
            (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(0, 0)
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 50)
                    go_up_button.hide()
                else if (dy < 0)
                    go_up_button.show()
            }
        })

        pull_to_load_view.setOnRefreshListener {
            presenter.onRefreshData()
        }
    }

    override fun onDataPrefetched(response: Rates) {
        adapter.onDataPrefetched(response)
    }

    override fun onSetBaseExchange(base: String?) {
        val completeText = getString(R.string.current_base, base)
        exchange_base_toolbar.text = completeText
    }

    override fun onSetLastDataUpdateDate(date: String) {
        val completeText = getString(R.string.last_data_update, date)
        last_update_toolbar.text = completeText
    }

    override fun onDataIsUpToDate() {
        pull_to_load_view.isRefreshing = false
        Snackbar.make(
            findViewById(getSnackbarLayoutMovingUpId()),
            getString(R.string.rates_up_to_date),
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onDataRefreshed(response: Rates?) {
        adapter.onDataRefreshed(response)
        pull_to_load_view.isRefreshing = false
    }

    override fun onRestoreChanges(hasDataFetched: Boolean, ratesList: ArrayList<Rates?>) {
        adapter.hasDataPrefetched = hasDataFetched
        adapter.setRatesList(ratesList)
    }


    override fun onRateClicked(rateInfo: RateInfo) {
        val intent = Intent(this, RateDetailsActivity::class.java)
        val bundle = Bundle()
        bundle.putString(RateDetailsActivity.DATE_KEY, rateInfo.date)
        bundle.putString(RateDetailsActivity.BASE_KEY, rateInfo.base)
        bundle.putString(RateDetailsActivity.CURRENCY_KEY, rateInfo.ratesMap!!.key)
        bundle.putDouble(RateDetailsActivity.RATE_KEY, rateInfo.ratesMap.value)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun onLoadMore() {
        presenter.onLoadMore()
    }

    override fun onMoreDataFetched(response: Rates) {
        adapter.onMoreDataFetched(response)
//        adapter.notifyDataSetChanged()
    }

    override fun onLoadingMoreData() {
        adapter.onLoadingMoreData()
    }

    override fun onFetchingDataError() {
        Toast.makeText(this, getString(R.string.no_internet_connection_message), Toast.LENGTH_LONG).show()
    }

    override fun getSnackbarLayoutMovingUpId(): Int {
        return R.id.all_rates
    }

    override fun showOnLoadingScreen() {
        fetching_data_progress_bar.visibility = VISIBLE
    }

    override fun hideOnLoadingScreen() {
        fetching_data_progress_bar.visibility = INVISIBLE
    }

    override fun onInternetConnectionAvailable() {
        presenter.onInternetConnectionAvailable()
    }

    override fun onInternetConnectionLost() {
        //irrelevant
    }
}
