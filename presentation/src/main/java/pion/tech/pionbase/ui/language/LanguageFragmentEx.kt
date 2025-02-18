package pion.tech.pionbase.ui.language

import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import pion.tech.pionbase.R
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.setPreventDoubleClick
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun LanguageFragment.initView() {
    adapter.setListener(this)
    binding.rvMain.adapter = adapter
    binding.ivBack.isVisible = isCameFromSetting()
}

fun LanguageFragment.applyEvent() {
    binding.ivDone.setPreventDoubleClick {
        val language = viewModel.getSelectedLanguage()
        if (language != null) {
            setLocale(language.localeCode)
            if (isCameFromSetting()) {
                findNavController().popBackStack(R.id.settingFragment, false)
            } else {
                safeNav(R.id.languageFragment, R.id.action_languageFragment_to_onboardFragment)
            }
        } else {
            displayToast(getString(R.string.something_error))
        }
    }
}

fun LanguageFragment.onBackEvent() {
    activity?.onBackPressedDispatcher?.addCallback(this, true) {
        backEvent()
    }
    binding.ivBack.setPreventDoubleClickScaleView {
        backEvent()
    }
}

fun LanguageFragment.backEvent() {
    if (isCameFromSetting()) {
        findNavController().navigateUp()
    }
}

fun setLocale(languageCode: String?) {
    val locales = LocaleListCompat.forLanguageTags(languageCode)
    AppCompatDelegate.setApplicationLocales(locales)
}

fun LanguageFragment.isCameFromSetting(): Boolean {
    return navController.previousBackStackEntry?.destination?.id == R.id.settingFragment

}