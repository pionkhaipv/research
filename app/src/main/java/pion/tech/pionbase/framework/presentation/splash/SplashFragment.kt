package pion.tech.pionbase.framework.presentation.splash

import android.view.View
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.databinding.FragmentSplashBinding
import pion.tech.pionbase.framework.presentation.common.BaseFragment
import pion.tech.pionbase.util.PrefUtil


@AndroidEntryPoint
class SplashFragment(
    private val glide: RequestManager,
    val prefUtil: PrefUtil
) : BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::inflate) {

    override fun init(view: View) {

        binding.loadingView.startAnim(4000L) { }

    }

    override fun subscribeObserver(view: View) {

    }

}
