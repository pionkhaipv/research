package pion.tech.pionbase.framework.presentation.splash

import android.view.View
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.databinding.FragmentSplashBinding
import pion.tech.pionbase.framework.presentation.common.BaseFragment


@AndroidEntryPoint
class SplashFragment(
    private val glide: RequestManager
) : BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::inflate) {

    override fun init(view: View) {

    }

    override fun subscribeObserver(view: View) {

    }

}
