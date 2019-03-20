package com.jackowski.exchangerate.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jackowski.exchangerate.R
import com.jackowski.exchangerate.adapters.SingleRateListAdapter.SingleRateListViewHolder
import com.jackowski.exchangerate.models.RateInfo
import com.jackowski.exchangerate.models.Rates
import kotlinx.android.synthetic.main.single_rate_item.view.*
import java.text.DecimalFormat


class SingleRateListAdapter(
    private val context: Context,
    private val onClickSingleRateListener: OnClickSingleRateListener?,
    private var ratesMap: Rates?
) :
    RecyclerView.Adapter<SingleRateListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): SingleRateListViewHolder {
        return SingleRateListViewHolder(LayoutInflater.from(context).inflate(R.layout.single_rate_item, parent, false))
    }

    override fun getItemCount(): Int {
        return ratesMap?.rates?.size ?: 0
    }

    override fun onBindViewHolder(viewHolder: SingleRateListViewHolder, position: Int) {
        if (ratesMap == null) return

        val adapterPosition = viewHolder.adapterPosition

        val entries = ratesMap!!.rates?.entries?.toTypedArray()
        val currentRate = entries!![adapterPosition]
        val df = DecimalFormat("#.####")
        viewHolder.currency.text = (currentRate.key + ":")
        val rate = df.format(currentRate.value)
        viewHolder.rate.text = (rate)

        if (position % 4 == 1 || position % 4 == 2) {
            viewHolder.cardView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
        } else {
            viewHolder.cardView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        }
    }

    fun updateList(ratesMap: Rates?) {
        this.ratesMap = ratesMap
    }

    inner class SingleRateListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cardView: CardView = view.single_rat_item_card
        val rate: TextView = view.rate
        val currency: TextView = view.currency

        init {
            itemView.setOnClickListener {
                val entries = ratesMap!!.rates!!.entries.toTypedArray()
                val currentRate = entries[adapterPosition]
                onClickSingleRateListener?.onClickItem(
                    RateInfo(
                        ratesMap!!.timestamp, ratesMap!!.base, ratesMap!!.date, currentRate
                    )
                )
            }
        }
    }
}