package id.bungamungil.pockettube.util

import android.view.View
import android.widget.TextView

fun TextView.onlyVisibleWhenHasText(string: String?) {
    if (string == null || string.isEmpty()) {
        visibility = View.GONE
        return
    }
    text = string
}