package pion.tech.pionbase.framework.presentation.splash

import android.view.View
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.R
import pion.tech.pionbase.databinding.FragmentSplashBinding
import pion.tech.pionbase.framework.presentation.common.BaseFragment
import pion.tech.pionbase.util.PrefUtil


@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::inflate) {

    override fun init(view: View) {

        binding.loadingView.startAnim(2000L) {
            safeNav(R.id.splashFragment , SplashFragmentDirections.actionSplashFragmentToHomeFragment())

        }

    }

    override fun subscribeObserver(view: View) {

    }

}
