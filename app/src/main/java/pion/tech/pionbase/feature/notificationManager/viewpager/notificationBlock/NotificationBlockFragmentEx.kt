package pion.tech.pionbase.feature.notificationManager.viewpager.notificationBlock

import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import pion.tech.pionbase.util.hideKeyboard

fun NotificationBlockFragment.initView() {
    adapter.setListener(this)
    binding.rvMain.adapter = adapter

    viewModel.getAppsWithNotification()
}

fun NotificationBlockFragment.searchEvent() {
    binding.edtSearch.doOnTextChanged { text, _, _, _ ->
        viewModel.updateSearchQuery(text.toString())
    }
    binding.edtSearch.setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
            binding.edtSearch.hideKeyboard()
            binding.edtSearch.clearFocus()
            true
        } else {
            false
        }
    }
}
