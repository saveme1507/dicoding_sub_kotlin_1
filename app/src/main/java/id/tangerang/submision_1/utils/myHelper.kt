package id.tangerang.submision_1.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun myNumberFormat(text: String): String {
    val localeID = Locale("in", "ID")
    return NumberFormat.getNumberInstance(localeID).format(text.toDouble())
}

fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val date = Date()
    return dateFormat.format(date)
}

