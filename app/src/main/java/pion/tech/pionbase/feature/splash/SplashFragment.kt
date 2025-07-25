package pion.tech.pionbase.feature.splash

import android.animation.ValueAnimator
import android.view.View
import com.piontech.core.base.BaseFragment
import com.piontech.core.utils.collectFlowOnView
import dagger.hilt.android.AndroidEntryPoint
import pion.datlt.libads.AdsController
import pion.datlt.libads.utils.loadAndShowConsentFormIfRequire
import pion.datlt.libads.utils.requestConsentInfoUpdate
import pion.tech.pionbase.app.CommonViewModel
import pion.tech.pionbase.app.MainActivity
import pion.tech.pionbase.databinding.FragmentSplashBinding
import pion.tech.pionbase.util.Constant
import pion.tech.pionbase.util.onSuccess

@AndroidEntryPoint
class SplashFragment :
    BaseFragment<FragmentSplashBinding, SplashViewModel, CommonViewModel>(
        FragmentSplashBinding::inflate,
        SplashViewModel::class.java,
        CommonViewModel::class.java,
    ) {
    var progressAnimator: ValueAnimator? = null

    override fun init(view: View) {
        onBackEvent()
        startAnimation()
    }

    override fun subscribeObserver(view: View) {
        commonViewModel.cachedRemoteConfig.collectFlowOnView(viewLifecycleOwner) { result ->
            result.onSuccess { remoteConfigData ->
                if (remoteConfigData != null) {
                    Constant.isRemoteConfigSuccess = remoteConfigData.isRealData
                    AdsController.setConfigAds(remoteConfigData.firebaseRemoteConfig.getString("config_show_ads"))
                    AdsController.getInstance().setListAdsData(
                        listJsonData =
                            arrayListOf(
                                remoteConfigData.firebaseRemoteConfig.getString("admob_id"),
                            ),
                    )
                    (activity as? MainActivity)?.initAppResumeAds()
                    AdsController.getInstance().requestConsentInfoUpdate(
                        onFailed = { error ->
                            goToLanguageScreen()
                        },
                        onSuccess = { isRequire, isConsentAvailable ->
                            if (isRequire) {
                                AdsController
                                    .getInstance()
                                    .loadAndShowConsentFormIfRequire(
                                        onConsentError = { errorConsent ->
                                            goToLanguageScreen()
                                        },
                                        onConsentDone = {
                                            goToLanguageScreen()
                                        },
                                    )
                            } else {
                                goToLanguageScreen()
                            }
                        },
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        releaseAnimation()
    }
}
