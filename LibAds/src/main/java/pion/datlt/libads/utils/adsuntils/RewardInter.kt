package pion.datlt.libads.utils.adsuntils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import pion.datlt.libads.AdsController
import pion.datlt.libads.callback.AdCallback
import pion.datlt.libads.utils.DialogLoadAdsUtils

fun Fragment.loadAndShowRewardInter(
    spaceNameConfig: String,
    spaceName: String,
    timeOut: Long = 7000L,
    destinationToShowAds: Int? = null,
    isScreenType: Boolean = true,
    onRewardDone: ((Boolean) -> Unit),
    onGetReward: (() -> Unit)? = null
) {
    if (checkConditionShowAds(context, spaceNameConfig)) {
        AdsController.isInterIsShowing = true
        var fragmentEvent = Lifecycle.Event.ON_ANY
        val callback = object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {
                fragmentEvent = Lifecycle.Event.ON_STOP
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {

            }
        }
        AdsController.getInstance().activity.application.registerActivityLifecycleCallbacks(callback)
        val lifecycleObserver = object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_STOP) {
                    fragmentEvent = event
                }
            }
        }
        DialogLoadAdsUtils.getInstance().showDialogLoadingAds(activity, isScreenType)

        var isRewardGot = false

        AdsController.getInstance().loadAndShow(
            spaceName = spaceName,
            destinationToShowAds = destinationToShowAds,
            lifecycle = lifecycle,
            timeout = timeOut,
            adCallback = object : AdCallback {
                override fun onAdShow() {
                    DialogLoadAdsUtils.getInstance().hideDialogLoadingAds()
                    AdsController.isInterIsShowing = true
                }

                override fun onAdClose() {
                    AdsController.isInterIsShowing = false
                    AdsController.isBlockOpenAds = fragmentEvent == Lifecycle.Event.ON_STOP
                    AdsController.getInstance().activity.application.unregisterActivityLifecycleCallbacks(
                        callback
                    )
                    lifecycle.removeObserver(lifecycleObserver)
                    onRewardDone.invoke(isRewardGot)
                }

                override fun onAdFailToLoad(messageError: String?) {
                    DialogLoadAdsUtils.getInstance().hideDialogLoadingAds(0)
                    AdsController.isInterIsShowing = false
                    AdsController.isBlockOpenAds = fragmentEvent == Lifecycle.Event.ON_STOP
                    AdsController.getInstance().activity.application.unregisterActivityLifecycleCallbacks(
                        callback
                    )
                    lifecycle.removeObserver(lifecycleObserver)
                    onRewardDone.invoke(false)
                }

                override fun onGotReward() {
                    super.onGotReward()
                    isRewardGot = true
                    onGetReward?.invoke()
                }

                override fun onAdClick() {
                    super.onAdClick()
                    AdsController.isBlockOpenAds = true
                }

            }
        )

    } else {
        onRewardDone.invoke(false)
    }
}

fun Fragment.showLoadedRewardInter(
    spaceNameConfig: String,
    spaceName: String,
    timeOut: Long = 7000L,
    isPreloadAfterShow: Boolean = false,
    destinationToShowAds: Int? = null,
    isShowLoadingView: Boolean = false,
    timeShowLoadingView: Long = 1000L,
    isScreenType: Boolean = true,
    onRewardDone: ((Boolean) -> Unit),
    onGetReward: (() -> Unit)? = null
) {
    if (checkConditionShowAds(context, spaceNameConfig)) {

        AdsController.isInterIsShowing = true
        var fragmentEvent = Lifecycle.Event.ON_ANY
        val callback = object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {
                fragmentEvent = Lifecycle.Event.ON_STOP
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {

            }
        }
        AdsController.getInstance().activity.application.registerActivityLifecycleCallbacks(callback)
        val lifecycleObserver = object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_STOP) {
                    fragmentEvent = event
                }
            }
        }

        val countDownTimer = object : CountDownTimer(timeShowLoadingView, timeShowLoadingView) {
            override fun onTick(p0: Long) {
                //do nothing
            }

            override fun onFinish() {
                var isRewardGot = false

                AdsController.getInstance().showLoadedAds(
                    spaceName = spaceName,
                    destinationToShowAds = destinationToShowAds,
                    lifecycle = lifecycle,
                    timeout = timeOut,
                    adCallback = object : AdCallback {


                        override fun onAdShow() {
                            DialogLoadAdsUtils.getInstance().hideDialogLoadingAds()
                            AdsController.isInterIsShowing = true
                        }

                        override fun onAdClose() {
                            AdsController.isInterIsShowing = false
                            AdsController.isBlockOpenAds = fragmentEvent == Lifecycle.Event.ON_STOP
                            AdsController.getInstance().activity.application.unregisterActivityLifecycleCallbacks(
                                callback
                            )
                            lifecycle.removeObserver(lifecycleObserver)
                            if (isPreloadAfterShow) {
                                safePreloadAds(
                                    spaceNameAds = spaceName,
                                    spaceNameConfig = spaceNameConfig
                                )
                            }
                            onRewardDone.invoke(isRewardGot)
                        }

                        override fun onAdFailToLoad(messageError: String?) {
                            DialogLoadAdsUtils.getInstance().hideDialogLoadingAds(0)
                            AdsController.isInterIsShowing = false
                            AdsController.isBlockOpenAds = fragmentEvent == Lifecycle.Event.ON_STOP
                            AdsController.getInstance().activity.application.unregisterActivityLifecycleCallbacks(
                                callback
                            )
                            lifecycle.removeObserver(lifecycleObserver)
                            if (isPreloadAfterShow) {
                                safePreloadAds(
                                    spaceNameAds = spaceName,
                                    spaceNameConfig = spaceNameConfig
                                )
                            }
                            onRewardDone.invoke(false)
                        }

                        override fun onGotReward() {
                            super.onGotReward()
                            isRewardGot = true
                            onGetReward?.invoke()
                        }

                        override fun onAdClick() {
                            super.onAdClick()
                            AdsController.isBlockOpenAds = true
                        }

                    }

                )
            }

        }

        if (isShowLoadingView) {
            DialogLoadAdsUtils.getInstance().showDialogLoadingAds(activity, isScreenType)
            countDownTimer.start()
        } else {
            countDownTimer.onFinish()
        }


    } else {
        onRewardDone.invoke(false)
    }
}