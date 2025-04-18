package pion.tech.pionbase.core.presentation.navigator

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavDestination

interface Navigator {
    fun openHomeToSetting(bundle: Bundle? = null)
    fun openLanguageToOnboard(bundle: Bundle? = null)
    fun openOnboardToHome(bundle: Bundle? = null)
    fun openSettingToLanguage(bundle: Bundle? = null)
    fun openSplashToLanguage(bundle: Bundle? = null)

    fun navigateUp()
    fun addOnDestinationChangedListener(listener: (NavController, NavDestination?, Bundle?) -> Unit)
    fun isCameFrom(destinationId: Int): Boolean
    fun popBackStack(destinationId: Int, inclusive: Boolean): Boolean

}
