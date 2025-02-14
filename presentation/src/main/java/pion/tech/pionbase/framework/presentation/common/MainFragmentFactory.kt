package pion.tech.pionbase.framework.presentation.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import pion.tech.pionbase.framework.presentation.splash.SplashFragment

class MainFragmentFactory : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {

        return when (className) {
            SplashFragment::class.java.name -> {
                SplashFragment().apply {
                    arguments = Bundle().apply {
                        putString("key", "value")
                    }
                }
            }

            else -> super.instantiate(classLoader, className)
        }

    }
}