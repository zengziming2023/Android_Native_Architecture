package com.hele.base.extensions

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide

fun ImageView.loadImage(url: String, context: Context, @DrawableRes placeholder: Int = -1) {
    Glide.with(context).load(url).let {
        if (placeholder != -1) {
            it.placeholder(placeholder)
        } else {
            it
        }
    }.into(this)
}

fun ImageView.loadResource(
    @DrawableRes res: Int, context: Context, @DrawableRes placeholder: Int = -1
) {
    Glide.with(context).load(res).let {
        if (placeholder != -1) {
            it.placeholder(placeholder)
        } else {
            it
        }
    }.into(this)
}