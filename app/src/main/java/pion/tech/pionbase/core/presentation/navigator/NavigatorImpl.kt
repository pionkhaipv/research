package pion.tech.pionbase.core.presentation.navigator

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import pion.tech.pionbase.R

class NavigatorImpl(
    private val navController: NavController,
    private val lifecycle: Lifecycle,
    private val currentDestinationId: Int
) : Navigator {

    private var navObserver: LifecycleEventObserver? = null

    private fun isAtCurrentDestination(): Boolean {
        return navController.currentDestination?.id == currentDestinationId
    }

    private fun safeNav(actionId: Int, bundle: Bundle? = null) {
        if (!isAtCurrentDestination()) return
        runCatching {
            navObserver = object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_RESUME) {
                        lifecycle.removeObserver(this)
                        runCatching {
                            if (navController.currentDestination?.id == currentDestinationId) {
                                navController.navigate(actionId, bundle)
                            }
                        }
                    }
                }
            }
            lifecycle.addObserver(navObserver!!)

            navController.addOnDestinationChangedListener(object :
                NavController.OnDestinationChangedListener {
                override fun onDestinationChanged(
                    controller: NavController,
                    destination: NavDestination,
                    arguments: Bundle?
                ) {
                    if (destination.id != currentDestinationId) {
                        navController.removeOnDestinationChangedListener(this)
                        lifecycle.removeObserver(navObserver as LifecycleEventObserver)
                    }
                }
            })

            if (navController.currentDestination?.id == currentDestinationId) {
                navController.navigate(actionId, bundle)
            }
        }
    }

    override fun openHomeToSetting(bundle: Bundle?) {
        safeNav(R.id.action_homeFragment_to_settingFragment, bundle)
    }

    override fun openLanguageToOnboard(bundle: Bundle?) {
        safeNav(R.id.action_languageFragment_to_onboardFragment, bundle)
    }

    override fun openOnboardToHome(bundle: Bundle?) {
        safeNav(R.id.action_onboardFragment_to_homeFragment, bundle)
    }

    override fun openSettingToLanguage(bundle: Bundle?) {
        safeNav(R.id.action_settingFragment_to_languageFragment, bundle)
    }

    override fun openSplashToLanguage(bundle: Bundle?) {
        safeNav(R.id.action_splashFragment_to_languageFragment, bundle)
    }

    override fun navigateUp() {
        navController.navigateUp()
    }

    override fun addOnDestinationChangedListener(listener: (NavController, NavDestination?, Bundle?) -> Unit) {
        navController.addOnDestinationChangedListener(listener)
    }

    override fun isCameFrom(destinationId: Int): Boolean {
        return navController.previousBackStackEntry?.destination?.id == destinationId
    }

    override fun popBackStack(
        destinationId: Int,
        inclusive: Boolean
    ): Boolean {
        return navController.popBackStack(destinationId, inclusive)
    }
}
