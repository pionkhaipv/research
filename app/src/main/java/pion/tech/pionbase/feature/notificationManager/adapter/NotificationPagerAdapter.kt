package pion.tech.pionbase.feature.notificationManager.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import pion.tech.pionbase.feature.notificationManager.notificationBlock.NotificationBlockFragment
import pion.tech.pionbase.feature.notificationManager.recentNotifications.RecentNotificationsFragment

class NotificationPagerAdapter(
    fragment: Fragment,
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> RecentNotificationsFragment()
            1 -> NotificationBlockFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
}
