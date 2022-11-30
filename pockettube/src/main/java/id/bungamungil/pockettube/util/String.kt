package id.bungamungil.pockettube.util

import java.text.SimpleDateFormat
import java.util.*

fun String.convertDateFormat(prevFormat: String, nextFormat: String): String? {
    val prevDateFormatter = SimpleDateFormat(prevFormat, Locale.getDefault())
    val nextDateFormatter = SimpleDateFormat(nextFormat, Locale.getDefault())
    val parsedDate = prevDateFormatter.parse(this) ?: return null
    return nextDateFormatter.format(parsedDate)
}