package pion.tech.pionbase.util

import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.io.File

fun ImageView.loadImage(resDrawable: Int) {
    Glide.with(this)
        .load(resDrawable)
        .into(this)
}

fun ImageView.loadImage(file: File) {
    Glide.with(this)
        .load(file)
        .into(this)
}

fun ImageView.loadImage(bitmap: Bitmap) {
    Glide.with(this)
        .load(bitmap)
        .into(this)
}

fun ImageView.loadImage(urlImage: String) {
    Glide.with(this)
        .load(urlImage)
        .into(this)
}

fun ImageView.loadImage(uri: Uri) {
    Glide.with(this)
        .load(uri)
        .into(this)
}

fun ImageView.loadImage(resDrawable: Int, placeHolder: Int) {
    Glide.with(this)
        .load(resDrawable)
        .placeholder(placeHolder)
        .into(this)
}

fun ImageView.loadImage(file: File, placeHolder: Int) {
    Glide.with(this)
        .load(file)
        .placeholder(placeHolder)
        .into(this)
}


fun ImageView.loadImage(bitmap: Bitmap, placeHolder: Int) {
    Glide.with(this)
        .load(bitmap)
        .placeholder(placeHolder)
        .into(this)
}

fun ImageView.loadImage(urlImage: String, placeHolder: Int) {
    Glide.with(this)
        .load(urlImage)
        .placeholder(placeHolder)
        .into(this)
}


fun ImageView.loadImage(uri: Uri, placeHolder: Int) {
    Glide.with(this)
        .load(uri)
        .placeholder(placeHolder)
        .into(this)
}


