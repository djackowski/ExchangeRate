package com.jackowski.exchangerate

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_rate_details.*


class RateDetailsActivity: AppCompatActivity() {
    companion object {
        const val DATE_KEY = "date"
        const val BASE_KEY = "base"
        const val CURRENCY_KEY = "currency"
        const val RATE_KEY = "rate"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rate_details)

        val date = intent.getStringExtra(DATE_KEY)
        val base = intent.getStringExtra(BASE_KEY)
        val currency = intent.getStringExtra(CURRENCY_KEY)
        val rate: Double = intent.getDoubleExtra(RATE_KEY, 0.0)

        date_details.text = date.toString()
        rate_base_details.text = "1 $base"
        currency_details.text = "$rate $currency"
    }
}