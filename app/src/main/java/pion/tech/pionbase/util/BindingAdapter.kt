package pion.tech.pionbase.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("imageResource", requireAll = true)
fun ImageView.setImage(imageResource: Int?) {
    imageResource ?: return
    setImageResource(imageResource)
}


//demo
//@BindingAdapter(value = ["viewMode", "currentMode"], requireAll = true)
//fun View.setVisibleInMode(
//    viewMode: ModeEditResult?,
//    currentMode: ModeEditResult?
//) {
//    viewMode ?: return
//    currentMode ?: return
//    visibility = if (viewMode == currentMode) {
//        View.VISIBLE
//    } else {
//        View.GONE
//    }
//}