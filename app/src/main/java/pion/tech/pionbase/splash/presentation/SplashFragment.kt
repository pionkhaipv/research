package pion.tech.pionbase.splash.presentation

import android.util.Log
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import pion.datlt.libads.AdsController
import pion.datlt.libads.utils.loadAndShowConsentFormIfRequire
import pion.datlt.libads.utils.requestConsentInfoUpdate
import pion.tech.pionbase.databinding.FragmentSplashBinding
import pion.tech.pionbase.app.presentation.MainActivity
import pion.tech.pionbase.core.presentation.common.base.BaseFragment
import pion.tech.pionbase.core.presentation.util.Constant
import pion.tech.pionbase.core.presentation.util.collectFlowOnView


@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding, SplashViewModel>(
    FragmentSplashBinding::inflate,
    SplashViewModel::class.java
) {

    override fun init(view: View) {
        backEvent()
        startAnimation()
    }

    override fun subscribeObserver(view: View) {
        commonViewModel.remoteConfigDataStateFlow.collectFlowOnView(viewLifecycleOwner) {
            Log.d("asgawgawgwgawga", "subscribeObserver: ${it?.isRealData}")
            if (it != null) {
                Constant.isRemoteConfigSuccess = it.isRealData
                AdsController.setConfigAds(it.configShowAds)
                AdsController.getInstance().setListAdsData(listJsonData = arrayListOf(it.admobId))
                (activity as? MainActivity)?.initAppResumeAds()
                AdsController.getInstance().requestConsentInfoUpdate(
                    onFailed = { error ->
                        goToLanguageScreen()
                    },
                    onSuccess = { isRequire, isConsentAvailable ->
                        if (isRequire) {
                            AdsController.getInstance()
                                .loadAndShowConsentFormIfRequire(
                                    onConsentError = { errorConsent ->
                                        goToLanguageScreen()
                                    },
                                    onConsentDone = {
                                        goToLanguageScreen()
                                    }
                                )
                        } else {
                            goToLanguageScreen()
                        }
                    }
                )
            }

        }
    }

}
