package pion.tech.pionbase.framework.presentation.splash

import android.util.Log
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import pion.datlt.libads.AdsController
import pion.datlt.libads.utils.loadAndShowConsentFormIfRequire
import pion.datlt.libads.utils.requestConsentInfoUpdate
import pion.tech.pionbase.databinding.FragmentSplashBinding
import pion.tech.pionbase.framework.presentation.common.BaseFragment
import pion.tech.pionbase.framework.presentation.common.RemoteConfigDataStatus
import pion.tech.pionbase.util.collectFlowOnView


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
            when (it) {
                RemoteConfigDataStatus.Error -> {

                }

                RemoteConfigDataStatus.None -> {

                }

                RemoteConfigDataStatus.Standby -> {
                }

                is RemoteConfigDataStatus.Success -> {
                    Log.d("asgawgawgwgawga", "subscribeObserver: ${it.remoteConfigDataModel.isRealData}")
                    AdsController.getInstance().requestConsentInfoUpdate(
                        onFailed = { error ->
                            //vao nhu luong binh thuong
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

}
