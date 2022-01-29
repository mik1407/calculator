package net.pubnative.hybidstandalonedemo.helpers

import android.content.Context

interface Calculator {
    fun showNewResult(value: String, context: Context)

    fun showNewFormula(value: String, context: Context)
}
