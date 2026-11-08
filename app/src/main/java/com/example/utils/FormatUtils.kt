package com.example.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatUtils {

    private val banglaDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

    fun toBanglaNumber(number: Any?): String {
        if (number == null) return "০"
        val str = when (number) {
            is Double -> {
                if (number % 1.0 == 0.0) number.toLong().toString()
                else String.format(Locale.US, "%.2f", number)
            }
            is Float -> {
                if (number % 1.0f == 0.0f) number.toLong().toString()
                else String.format(Locale.US, "%.2f", number)
            }
            else -> number.toString()
        }

        val sb = java.lang.StringBuilder()
        for (ch in str) {
            if (ch in '0'..'9') {
                sb.append(banglaDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun formatCurrency(amount: Double): String {
        return "৳ ${toBanglaNumber(amount)}"
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH)
        val formatted = sdf.format(Date(timestamp))
        // Convert digits to bangla
        val sb = java.lang.StringBuilder()
        for (ch in formatted) {
            if (ch in '0'..'9') {
                sb.append(banglaDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun formatDateNumeric(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        return sdf.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM, yyyy hh:mm a", Locale.ENGLISH)
        val formatted = sdf.format(Date(timestamp))
        val sb = java.lang.StringBuilder()
        for (ch in formatted) {
            if (ch in '0'..'9') {
                sb.append(banglaDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }
}
