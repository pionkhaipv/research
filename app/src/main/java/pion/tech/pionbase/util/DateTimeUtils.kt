package pion.tech.pionbase.util

import android.content.Context
import android.content.res.Resources
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import pion.tech.pionbase.R

/**
 * Utility class for date and time formatting
 */
object DateTimeUtils {
    /**
     * Format timestamp according to the following rules:
     * - < 1 minute: Just now
     * - 1 - 59 minutes: [number] min ago
     * - Today (after 1 hour): h:mm AM/PM (Ex: 2:30 PM)
     * - Yesterday: Yesterday, h:mm AM/PM (Ex: Yesterday, 4:45 PM)
     * - 2-6 days ago: [Day of week], h:mm AM/PM (Ex: Tuesday, 9:00 AM)
     * - > 1 week (this year): MMM d, h:mm AM/PM (Ex: Jul 25, 11:20 AM)
     * - Previous years: MM/dd/yyyy
     *
     * @param context Context for accessing string resources
     * @param timestamp Unix timestamp in milliseconds
     * @return Formatted timestamp string
     */
    fun formatTimestamp(context: Context, timestamp: Long): String {
        val now = Calendar.getInstance()
        val date = Calendar.getInstance()
        date.timeInMillis = timestamp

        // Calculate time difference in minutes
        val diffInMillis = now.timeInMillis - timestamp
        val diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)

        // Less than 1 minute
        if (diffInMinutes < 1) {
            return context.getString(R.string.time_just_now)
        }

        // 1-59 minutes
        if (diffInMinutes < 60) {
            return diffInMinutes.toString() + " " + context.getString(R.string.time_minutes_ago)
        }

        // Check if same day (today)
        val sameDay =
            now.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)

        // Check if yesterday
        val yesterday =
            now.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) - date.get(Calendar.DAY_OF_YEAR) == 1

        // Time format for hours and minutes
        val timeFormat = SimpleDateFormat("h:mm a", Locale.US)
        val timeString = timeFormat.format(Date(timestamp))

        // Today (after 1 hour)
        if (sameDay) {
            return timeString
        }

        // Yesterday
        if (yesterday) {
            return context.getString(R.string.time_yesterday_format) + ", " + timeString
        }

        // Calculate days difference
        val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)

        // 2-6 days ago
        if (diffInDays < 7) {
            val dayFormat = SimpleDateFormat("EEEE", Locale.US)
            val dayOfWeek = dayFormat.format(Date(timestamp))
            return dayOfWeek + ", " + timeString
        }

        // Check if same year
        val sameYear = now.get(Calendar.YEAR) == date.get(Calendar.YEAR)

        // > 1 week (this year)
        if (sameYear) {
            val monthDayFormat = SimpleDateFormat("MMM d", Locale.US)
            val monthDay = monthDayFormat.format(Date(timestamp))
            return monthDay + ", " + timeString
        }

        // Previous years
        val yearFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        return yearFormat.format(Date(timestamp))
    }
    
    /**
     * Overloaded method for backward compatibility
     * Uses hardcoded strings for testing purposes
     * 
     * @param timestamp Unix timestamp in milliseconds
     * @return Formatted timestamp string
     */
    @JvmStatic
    fun formatTimestamp(timestamp: Long): String {
        val now = Calendar.getInstance()
        val date = Calendar.getInstance()
        date.timeInMillis = timestamp

        // Calculate time difference in minutes
        val diffInMillis = now.timeInMillis - timestamp
        val diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)

        // Less than 1 minute
        if (diffInMinutes < 1) {
            return "Just now"
        }

        // 1-59 minutes
        if (diffInMinutes < 60) {
            return "$diffInMinutes min ago"
        }

        // Check if same day (today)
        val sameDay = now.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
        
        // Check if yesterday
        val yesterday = now.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) - date.get(Calendar.DAY_OF_YEAR) == 1

        // Time format for hours and minutes
        val timeFormat = SimpleDateFormat("h:mm a", Locale.US)
        val timeString = timeFormat.format(Date(timestamp))

        // Today (after 1 hour)
        if (sameDay) {
            return timeString
        }

        // Yesterday
        if (yesterday) {
            return "Yesterday, $timeString"
        }

        // Calculate days difference
        val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)

        // 2-6 days ago
        if (diffInDays < 7) {
            val dayFormat = SimpleDateFormat("EEEE", Locale.US)
            val dayOfWeek = dayFormat.format(Date(timestamp))
            return "$dayOfWeek, $timeString"
        }

        // Check if same year
        val sameYear = now.get(Calendar.YEAR) == date.get(Calendar.YEAR)

        // > 1 week (this year)
        if (sameYear) {
            val monthDayFormat = SimpleDateFormat("MMM d", Locale.US)
            val monthDay = monthDayFormat.format(Date(timestamp))
            return "$monthDay, $timeString"
        }

        // Previous years
        val yearFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        return yearFormat.format(Date(timestamp))
    }
}
