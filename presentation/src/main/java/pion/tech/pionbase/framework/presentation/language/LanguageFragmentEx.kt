package pion.tech.pionbase.framework.presentation.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import pion.tech.pionbase.R
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.setPreventDoubleClick

fun LanguageFragment.initView() {
    adapter.setListener(this)
    binding.rvMain.adapter = adapter
}

fun LanguageFragment.applyEvent() {
    binding.ivDone.setPreventDoubleClick {
        val language = adapter.getCurrentLanguageSelected()
        if (language != null) {
            setLocale(language.localeCode)
        } else {
            displayToast(getString(R.string.something_error))
        }
    }
}

fun setLocale(languageCode: String?) {
    val locales = LocaleListCompat.forLanguageTags(languageCode)
    AppCompatDelegate.setApplicationLocales(locales)
}