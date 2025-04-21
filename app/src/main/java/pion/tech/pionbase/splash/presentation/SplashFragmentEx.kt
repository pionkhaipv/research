package pion.tech.pionbase.splash.presentation

import pion.tech.pionbase.core.presentation.navigator.NavigationRoute

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
    navigator.navigateTo(NavigationRoute.SPLASH_TO_LANGUAGE)
}