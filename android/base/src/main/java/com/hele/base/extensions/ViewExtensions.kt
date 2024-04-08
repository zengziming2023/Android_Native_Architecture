package com.hele.base.extensions

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.load
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

fun ImageView.loadImageWithCoil(url: String, @DrawableRes placeholder: Int = -1) {
    this.load(url) {
        placeholder(placeholder)
    }

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

fun ImageView.loadResourceWithCoil(@DrawableRes res: Int, @DrawableRes placeholder: Int = -1) {
    this.load(res) {
        placeholder(placeholder)
    }
}