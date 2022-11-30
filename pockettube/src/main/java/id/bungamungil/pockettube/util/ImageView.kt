package id.bungamungil.pockettube.util

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadImageFromRemote(url: String?) {
    val mUrl = url ?: return
    Glide.with(this)
        .load(mUrl)
        .into(this);
}