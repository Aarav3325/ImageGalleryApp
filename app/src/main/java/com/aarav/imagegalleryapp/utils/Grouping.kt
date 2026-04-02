package com.aarav.imagegalleryapp.utils

import android.text.format.DateUtils
import com.aarav.imagegalleryapp.data.model.ImageItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun formatDate(timestamp: Long): String {
    val imageDate = Calendar.getInstance().apply {
        timeInMillis = timestamp * 1000
    }

    val today = Calendar.getInstance()

    val yesterday = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -1)
    }

    return when {
        isSameDay(imageDate, today) -> "Today"
        isSameDay(imageDate, yesterday) -> "Yesterday"
        else -> {
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}

fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}