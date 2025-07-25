package pion.tech.pionbase.feature.notificationManager

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import pion.tech.pionbase.feature.notificationManager.notificationPermissions.NotificationPermissionsFragment
import pion.tech.pionbase.feature.notificationManager.recentNotifications.RecentNotificationsFragment
import pion.tech.pionbase.util.NotificationPermissionManager
import pion.tech.pionbase.util.setPreventDoubleClick

fun NotificationManagerFragment.initView() {
    // Initialize any basic view setup here
}

fun NotificationManagerFragment.setupViewPager() {
    val adapter = NotificationPagerAdapter(this)
    binding.viewPager.adapter = adapter

    TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
        tab.text =
            when (position) {
                0 -> "Recent Notifications"
                1 -> "App Permissions"
                else -> ""
            }
    }.attach()
}

fun NotificationManagerFragment.setupSwitchListener() {
    binding.switchNotificationListener.setPreventDoubleClick {
        val isChecked = binding.switchNotificationListener.isChecked
        if (isChecked) {
            if (NotificationPermissionManager.areAllNotificationPermissionsGranted(requireContext())) {
                binding.switchNotificationListener.isChecked = true
                viewModel.toggleNotificationListener(true)
            } else {
                binding.switchNotificationListener.isChecked = false
            }
        } else {
            viewModel.toggleNotificationListener(false)
        }
    }
}

fun NotificationManagerFragment.onBackEvent() {
    onSystemBack {
        backEvent()
    }
}

fun NotificationManagerFragment.backEvent() {
    navigator.navigateUp()
}

private class NotificationPagerAdapter(
    fragment: Fragment,
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> RecentNotificationsFragment()
            1 -> NotificationPermissionsFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
}
