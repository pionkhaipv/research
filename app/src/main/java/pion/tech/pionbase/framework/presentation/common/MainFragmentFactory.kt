package pion.tech.pionbase.framework.presentation.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.bumptech.glide.RequestManager
import pion.tech.pionbase.framework.presentation.splash.SplashFragment
import pion.tech.pionbase.util.PrefUtil
import javax.inject.Inject

class MainFragmentFactory
@Inject
constructor(
    private val prefUtil: PrefUtil,
    private val glide: RequestManager
) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {

        return when (className) {
            SplashFragment::class.java.name -> {
                SplashFragment(glide).apply {
                    arguments = Bundle().apply {
                        putString("key", "check new")
                    }
                }
            }

            else -> super.instantiate(classLoader, className)
        }

    }
}