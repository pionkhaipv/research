package pion.tech.pionbase.framework.presentation.splash

import androidx.activity.addCallback
import pion.tech.pionbase.R

fun SplashFragment.backEvent() {
    activity?.onBackPressedDispatcher?.addCallback(this, true) {
        onBackPressed()
    }
//    binding.btnBack.setPreventDoubleClickScaleView {
//        onBackPressed()
//    }
}

fun SplashFragment.onBackPressed() {
//    findNavController().popBackStack()
}

fun SplashFragment.startAnimation(){
    binding.loadingView.startAnim(2000L) {

    }
}