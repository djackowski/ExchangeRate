package com.jackowski.exchangerate.adapters

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.jackowski.exchangerate.R
import com.jackowski.exchangerate.models.RateInfo
import com.jackowski.exchangerate.models.Rates
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter
import kotlinx.android.synthetic.main.item_header.view.*
import kotlinx.android.synthetic.main.item_loading.view.*
import kotlinx.android.synthetic.main.single_rate_collection_item.view.*
import java.util.*
import kotlin.collections.ArrayList

const val VIEW_TYPE_RATES = 0
const val VIEW_TYPE_LOADING = 1

class AllRatesAdapter(
    private val context: Context,
    private val onLoadMoreListener: OnLoadMoreListener?,
    private val onClickRateInfoListener: OnClickRateInfoListener?,
    recyclerView: RecyclerView
) : RecyclerView.Adapter<ViewHolder>(), OnClickSingleRateListener, StickyRecyclerHeadersAdapter<AllRatesAdapter.HeaderViewHolder> {
    override fun getHeaderId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup?): HeaderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_header, parent, false)
        return HeaderViewHolder(view)
    }

    override fun onBindHeaderViewHolder(holder: HeaderViewHolder?, position: Int) {
        holder?.header?.text =  ratesList[position]?.date
    }

    private var ratesList: ArrayList<Rates?> = ArrayList()
    var isLoading = false
    private var viewPool = RecyclerView.RecycledViewPool()
    private var subRates: TreeMap<String, Double>? = TreeMap()
    private var currentRates: Rates? = null
    private var isLoadedMore = true
    var hasDataPrefetched = false

    init {
        val linearLayoutManagerNested = recyclerView.layoutManager as LinearLayoutManager
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                recyclerView.post { doOnListScrolled(linearLayoutManagerNested) }
            }
        })
    }

    fun setRatesList(ratesList: ArrayList<Rates?>) {
        this.ratesList = ArrayList(ratesList)
    }

    private fun doOnListScrolled(linearLayoutManagerNested: LinearLayoutManager) {
        val itemCount = linearLayoutManagerNested.itemCount
        val lastVisibleItemPosition = linearLayoutManagerNested.findLastVisibleItemPosition()
        if (hasDataPrefetched && !isLoading && lastVisibleItemPosition == (itemCount - 1)) {
            isLoading = true
            onLoadMoreListener?.onLoadMore()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_RATES -> {
                val view = LayoutInflater.from(context).inflate(R.layout.single_rate_collection_item, parent, false)
                return RatesViewHolder(view)
            }
            else -> {
                val view: View = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false)
                LoadingViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, p1: Int) {
        if (viewHolder is RatesViewHolder) {
            doOnRatesViewHolder(viewHolder)
        } else if (viewHolder is LoadingViewHolder) {
            viewHolder.progressBar.isIndeterminate = true
        }
    }

    private fun doOnRatesViewHolder(viewHolder: RatesViewHolder) {
        val position = viewHolder.adapterPosition
        currentRates = ratesList[position]

        subRates = currentRates?.rates

        val gridLayoutManager = GridLayoutManager(context, 2,  GridLayoutManager.VERTICAL, false)
        viewHolder.rates.layoutManager = gridLayoutManager
        viewHolder.rates.apply {
            layoutManager = gridLayoutManager
            adapter = SingleRateListAdapter(context, this@AllRatesAdapter, currentRates)
            rates.setHasFixedSize(true)
            rates.isNestedScrollingEnabled = false
            rates.setRecycledViewPool(viewPool)
            setRecycledViewPool(viewPool)
        }
        viewHolder.rates.adapter = SingleRateListAdapter(context, this, currentRates)
    }

    override fun getItemCount(): Int {
        return ratesList.size
    }

    override fun getItemViewType(position: Int): Int {
        val get = ratesList[position]
        return if (get == null) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_RATES
        }
    }

    fun updateList(response: Rates) {
        val rates = ArrayList<Rates?>()
        rates.add(response)
        ratesList.addAll(rates)
        isLoading = false
        isLoadedMore = true
        notifyDataSetChanged()
    }

    fun onMoreDataFetched(response: Rates) {
        ratesList.remove(null)
        updateList(response)
        isLoading = false
    }

    fun onDataPrefetched(response: Rates) {
        val rates = java.util.ArrayList<Rates>()
        rates.add(response)
        ratesList.addAll(rates)
        hasDataPrefetched = true
        notifyDataSetChanged()
    }

    override fun onClickItem(currencyRateItem: RateInfo) {
        onClickRateInfoListener?.onRateClicked(currencyRateItem)
    }

    fun onLoadingMoreData() {
        ratesList.add(null)
        notifyItemInserted(ratesList.size - 1)
    }

    fun onDataRefreshed(response: Rates?) {
        if(ratesList.first()?.date == response?.date) {
            ratesList[0] = response
        } else {
            ratesList.add(0, response)
        }
    }

    inner class RatesViewHolder(view: View) : ViewHolder(view) {
        var rates: RecyclerView = view.rates
    }

    inner class LoadingViewHolder(view: View) : ViewHolder(view) {
        val progressBar: ProgressBar = view.load_more_progress_bar
    }

    inner class HeaderViewHolder(view: View) : ViewHolder(view) {
        val header: TextView = view.item_header
    }

}