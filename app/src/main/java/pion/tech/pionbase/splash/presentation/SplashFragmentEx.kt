package pion.tech.pionbase.splash.presentation

import androidx.activity.addCallback
import pion.tech.pionbase.R

fun SplashFragment.backEvent() {
    onSystemBack {
        onBackPressed()
    }
//    binding.btnBack.setPreventDoubleClickScaleView {
//        onBackPressed()
//    }
}

fun SplashFragment.onBackPressed() {
//    findNavController().popBackStack()
}

fun SplashFragment.startAnimation() {
    binding.loadingView.startAnim(2000L)
}

fun SplashFragment.goToLanguageScreen() {
    safeNav(R.id.splashFragment, R.id.action_splashFragment_to_languageFragment)
}