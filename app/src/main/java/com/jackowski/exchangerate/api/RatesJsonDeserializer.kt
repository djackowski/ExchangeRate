package com.jackowski.exchangerate.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.jackowski.exchangerate.models.Rates
import java.lang.reflect.Type
import java.util.*

const val SUCCESS = "success"
const val TIMESTAMP = "timestamp"
const val HISTORICAL = "historical"
const val BASE = "base"
const val DATE = "date"
const val RATES = "rates"

class RatesJsonDeserializer: JsonDeserializer<Rates> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Rates? {
        val jsonObject = json?.asJsonObject

        val success = jsonObject?.get(SUCCESS)?.asBoolean
        val timestamp = jsonObject?.get(TIMESTAMP)?.asNumber
        val historical = jsonObject?.get(HISTORICAL)?.asBoolean
        val base = jsonObject?.get(BASE)?.asString
        val date = jsonObject?.get(DATE)?.asString

        val rates = readFromMap(jsonObject)


       return Rates(success, timestamp, historical, base, date , rates)
    }

    fun readFromMap(jsonObject: JsonObject?): TreeMap<String, Double>? {
        val rates = jsonObject?.get(RATES) ?: return null

        val ratesObject = rates.asJsonObject

        val keyValue = TreeMap<String, Double>()

        for(entry: Map.Entry<String, JsonElement> in ratesObject.entrySet()) {
            val key = entry.key
            val value = entry.value.asDouble
            keyValue[key] = value
        }
        return keyValue

    }

}