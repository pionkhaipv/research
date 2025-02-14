package pion.datlt.libads.model

import android.view.View
import android.view.ViewGroup

data class ConfigNative(
    val ratio : String,
    val adChoice : Int,
    val viewAds : View? = null
)