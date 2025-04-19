package pion.tech.pionbase.core.presentation.navigator

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import pion.tech.pionbase.R

object NavigationRoute {
    val HOME_TO_SETTING = R.id.action_homeFragment_to_settingFragment
    val LANGUAGE_TO_ONBOARD = R.id.action_languageFragment_to_onboardFragment
    val ONBOARD_TO_HOME = R.id.action_onboardFragment_to_homeFragment
    val SETTING_TO_LANGUAGE = R.id.action_settingFragment_to_languageFragment
    val SPLASH_TO_LANGUAGE = R.id.action_splashFragment_to_languageFragment
}

interface Navigator {
    /**
     * Navigate to a specific route with optional arguments
     */
    fun navigateTo(actionId: Int, bundle: Bundle? = null)

    /**
     * Navigate to a specific route with animation
     */
    fun navigateTo(actionId: Int, bundle: Bundle? = null, enterAnim: Int, exitAnim: Int)

    /**
     * Navigate to a specific route and clear back stack
     */
    fun navigateTo(actionId: Int, bundle: Bundle? = null, clearBackStack: Boolean = false)

    /**
     * Navigate back to previous screen
     */
    fun navigateUp()

    /**
     * Add listener for navigation events
     */
    fun addOnDestinationChangedListener(listener: (NavController, NavDestination?, Bundle?) -> Unit)

    /**
     * Check if current screen was navigated from specific destination
     */
    fun isCameFrom(destinationId: Int): Boolean

    /**
     * Pop back stack to specific destination
     */
    fun popBackStack(destinationId: Int, inclusive: Boolean): Boolean
}
