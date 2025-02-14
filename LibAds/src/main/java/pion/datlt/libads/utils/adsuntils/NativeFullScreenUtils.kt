package pion.datlt.libads.utils.adsuntils

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import pion.datlt.libads.AdsController
import pion.datlt.libads.R
import pion.datlt.libads.callback.AdCallback
import pion.datlt.libads.callback.PreloadCallback
import pion.datlt.libads.model.ConfigNative
import pion.datlt.libads.utils.AdsConstant
import pion.datlt.libads.utils.StateLoadAd

fun Fragment.showLoadedNativeFullScreen(
    spaceNameConfig: String,
    spaceName: String,
    includeHasBeenOpened: Boolean = false,
    adChoice: Int? = AdsConstant.TOP_LEFT,
    isResetConfig : Boolean = true,
    layoutToAttachAds: ViewGroup,
    layoutContainAds: ViewGroup? = null,
    viewAdsInflateFromXml: View? = LayoutInflater.from(context).inflate(R.layout.layout_native_full_screen, null),
    onAdsClick: (() -> Unit)? = null
) {
    if (checkConditionShowAds(context, spaceNameConfig)) {
        AdsConstant.listConfigAds[spaceNameConfig]?.let { config ->



            var newAdChoice = adChoice
            var newViewAdsInflateFromXml = viewAdsInflateFromXml

            if (isResetConfig){
                config.getConfigNative(
                    context = context,
                    default = ConfigNative(
                        ratio = "360:94",
                        adChoice = AdsConstant.TOP_LEFT
                    )
                ).let { configNative ->

                    if (newAdChoice == null) {
                        newAdChoice = configNative.adChoice
                    }
                    if (newViewAdsInflateFromXml == null){
                        newViewAdsInflateFromXml = configNative.viewAds
                    }

                }

                //set cta color
                runCatching {
                    if (newViewAdsInflateFromXml != null){
                        val colorStateList = ColorStateList.valueOf(Color.parseColor(config.ctaColor))
                        val ctaButton =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_call_to_action)
                        ctaButton.backgroundTintList = colorStateList
                        ctaButton.setTextColor(Color.parseColor(config.textCTAColor))
                    }

                }

                //set background color
                runCatching {
                    if (newViewAdsInflateFromXml != null){
                        val adViewHolder =
                            newViewAdsInflateFromXml!!.findViewById<ConstraintLayout>(R.id.adViewHolder)
                        adViewHolder.setBackgroundColor(Color.parseColor(config.backGroundColor))
                    }
                }

                //set content text color
                runCatching {
                    if (newViewAdsInflateFromXml != null){
                        val headLineText =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_headline)
                        val bodyText = newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_body)
                        headLineText.setTextColor(Color.parseColor(config.textContentColor))
                        bodyText.setTextColor(Color.parseColor(config.textContentColor))
                    }

                }
            }

            AdsController.getInstance().showLoadedAds(
                spaceName = spaceName,
                includeHasBeenOpened = includeHasBeenOpened,
                layoutToAttachAds = layoutToAttachAds,
                viewAdsInflateFromXml = newViewAdsInflateFromXml,
                adChoice = newAdChoice,
                adCallback = object : AdCallback {
                    override fun onAdShow() {}

                    override fun onAdClose() {}

                    override fun onAdFailToLoad(messageError: String?) {}

                    override fun onAdOff() {}

                    override fun onAdClick() {
                        AdsController.isBlockOpenAds = true
                        onAdsClick?.invoke()
                    }
                }
            )
        }

    } else {
        layoutToAttachAds.visibility = View.GONE
        layoutContainAds?.visibility = View.GONE
    }

}

fun Fragment.loadAndShowNativeFullScreen(
    spaceNameConfig: String,
    spaceName: String,
    adChoice: Int? = AdsConstant.TOP_LEFT,
    isResetConfig : Boolean = true,
    layoutToAttachAds: ViewGroup,
    layoutContainAds: ViewGroup? = null,
    viewAdsInflateFromXml: View? = LayoutInflater.from(context).inflate(R.layout.layout_native_full_screen, null),
    onAdsClick: (() -> Unit)? = null
) {
    if (checkConditionShowAds(context, spaceNameConfig)) {
        AdsConstant.listConfigAds[spaceNameConfig]?.let { config ->



            var newAdChoice = adChoice
            var newViewAdsInflateFromXml = viewAdsInflateFromXml

            if (isResetConfig){
                config.getConfigNative(
                    context = context,
                    default = ConfigNative(
                        ratio = "360:94",
                        adChoice = AdsConstant.TOP_LEFT
                    )
                ).let { configNative ->

                    if (newAdChoice == null) {
                        newAdChoice = configNative.adChoice
                    }
                    if (newViewAdsInflateFromXml == null){
                        newViewAdsInflateFromXml = configNative.viewAds
                    }

                }

                //set cta color
                runCatching {
                    if (newViewAdsInflateFromXml != null){
                        val colorStateList = ColorStateList.valueOf(Color.parseColor(config.ctaColor))
                        val ctaButton =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_call_to_action)
                        ctaButton.backgroundTintList = colorStateList
                        ctaButton.setTextColor(Color.parseColor(config.textCTAColor))
                    }

                }

                //set background color
                runCatching {
                    if (newViewAdsInflateFromXml != null){
                        val adViewHolder =
                            newViewAdsInflateFromXml!!.findViewById<ConstraintLayout>(R.id.adViewHolder)
                        adViewHolder.setBackgroundColor(Color.parseColor(config.backGroundColor))
                    }
                }

                //set content text color
                runCatching {
                    if (newViewAdsInflateFromXml != null){
                        val headLineText =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_headline)
                        val bodyText = newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_body)
                        headLineText.setTextColor(Color.parseColor(config.textContentColor))
                        bodyText.setTextColor(Color.parseColor(config.textContentColor))
                    }

                }
            }

            AdsController.getInstance().loadAndShow(
                spaceName = spaceName,
                layoutToAttachAds = layoutToAttachAds,
                viewAdsInflateFromXml = newViewAdsInflateFromXml,
                adChoice = newAdChoice,
                adCallback = object : AdCallback {
                    override fun onAdShow() {}

                    override fun onAdClose() {}

                    override fun onAdFailToLoad(messageError: String?) {}

                    override fun onAdOff() {}

                    override fun onAdClick() {
                        AdsController.isBlockOpenAds = true
                        onAdsClick?.invoke()
                    }
                }
            )
        }

    } else {
        layoutToAttachAds.visibility = View.GONE
        layoutContainAds?.visibility = View.GONE
    }

}

fun Fragment.show3NativeFullScreen(
    spaceNameConfig: String,
    spaceName1: String,
    spaceName2: String,
    spaceName3: String,
    adChoice: Int? = null,
    isResetConfig: Boolean = true,
    layoutToAttachAds: ViewGroup,
    layoutContainAds: ViewGroup? = null,
    viewAdsInflateFromXml: View? = LayoutInflater.from(context).inflate(R.layout.layout_native_full_screen, null),
    onAdsClick: (() -> Unit)? = null
) {
    if (checkConditionShowAds(context, spaceNameConfig)){

        //lay data ve view
        var newAdChoice = adChoice
        var newViewAdsInflateFromXml = viewAdsInflateFromXml

        AdsConstant.listConfigAds[spaceNameConfig]?.let { config ->

            if (isResetConfig){
                config.getConfigNative(
                    context = context,
                    default = ConfigNative(
                        ratio = "360:94",
                        adChoice = AdsConstant.TOP_LEFT,
                        viewAds = LayoutInflater.from(context)
                            .inflate(R.layout.layout_native_medium_logotop_ctabot, null)
                    )
                ).let { configNative ->

                    if (newAdChoice == null) {
                        newAdChoice = configNative.adChoice
                    }

                    if (newViewAdsInflateFromXml == null) {
                        newViewAdsInflateFromXml = configNative.viewAds
                    }
                }

                //set cta color
                runCatching {
                    if (newViewAdsInflateFromXml != null) {
                        val colorStateList = ColorStateList.valueOf(Color.parseColor(config.ctaColor))
                        val ctaButton =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_call_to_action)
                        ctaButton.backgroundTintList = colorStateList
                        ctaButton.setTextColor(Color.parseColor(config.textCTAColor))
                    }
                }

                //set background color
                runCatching {
                    if (newViewAdsInflateFromXml != null) {
                        val adViewHolder =
                            newViewAdsInflateFromXml!!.findViewById<ConstraintLayout>(R.id.adViewHolder)
                        adViewHolder.setBackgroundColor(Color.parseColor(config.backGroundColor))
                    }
                }

                //set content text color
                runCatching {
                    if (newViewAdsInflateFromXml != null) {
                        val headLineText =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_headline)
                        val bodyText = newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_body)
                        headLineText.setTextColor(Color.parseColor(config.textContentColor))
                        bodyText.setTextColor(Color.parseColor(config.textContentColor))
                    }

                }

            }

        }

        var stateNative1 = StateLoadAd.LOADING
        var stateNative2 = StateLoadAd.LOADING
        var stateNative3 = StateLoadAd.LOADING
        var isAnyShow = false

        fun checkShowNative(){
            if (isAnyShow) return

            if (stateNative1 == StateLoadAd.SUCCESS){
                isAnyShow = true
                showLoadedNativeFullScreen(
                    spaceNameConfig = spaceNameConfig,
                    spaceName = spaceName1,
                    adChoice = newAdChoice,
                    isResetConfig = false,
                    layoutToAttachAds = layoutToAttachAds,
                    layoutContainAds = layoutContainAds,
                    viewAdsInflateFromXml = newViewAdsInflateFromXml,
                    onAdsClick = onAdsClick)
            }else if (stateNative2 == StateLoadAd.SUCCESS){
                isAnyShow = true
                showLoadedNativeFullScreen(
                    spaceNameConfig = spaceNameConfig,
                    spaceName = spaceName2,
                    adChoice = newAdChoice,
                    isResetConfig = false,
                    layoutToAttachAds = layoutToAttachAds,
                    layoutContainAds = layoutContainAds,
                    viewAdsInflateFromXml = newViewAdsInflateFromXml,
                    onAdsClick = onAdsClick)
            }else if (stateNative3== StateLoadAd.SUCCESS){
                isAnyShow = true
                showLoadedNativeFullScreen(
                    spaceNameConfig = spaceNameConfig,
                    spaceName = spaceName3,
                    adChoice = newAdChoice,
                    isResetConfig = false,
                    layoutToAttachAds = layoutToAttachAds,
                    layoutContainAds = layoutContainAds,
                    viewAdsInflateFromXml = newViewAdsInflateFromXml,
                    onAdsClick = onAdsClick)
            }
        }


        safePreloadAds(spaceNameConfig = spaceNameConfig , spaceNameAds = spaceName1 , adChoice = newAdChoice , preloadCallback = object : PreloadCallback{
            override fun onLoadDone() {
                stateNative1 = StateLoadAd.SUCCESS
                checkShowNative()
            }
        })
        safePreloadAds(spaceNameConfig = spaceNameConfig , spaceNameAds = spaceName2 , adChoice = newAdChoice , preloadCallback = object : PreloadCallback{
            override fun onLoadDone() {
                stateNative2 = StateLoadAd.SUCCESS
                checkShowNative()
            }
        })
        safePreloadAds(spaceNameConfig = spaceNameConfig , spaceNameAds = spaceName3 , adChoice = newAdChoice , preloadCallback = object : PreloadCallback{
            override fun onLoadDone() {
                stateNative3 = StateLoadAd.SUCCESS
                checkShowNative()
            }
        })

    }else{
        layoutToAttachAds.visibility = View.GONE
        layoutContainAds?.visibility = View.GONE
    }
}


fun Fragment.show3NativeFullScreenUsePriority(
    spaceNameConfig: String,
    spaceName1: String,
    spaceName2: String,
    spaceName3: String,
    timeOut: Long = AdsConstant.timeDelayNative,
    includeHasBeenOpened: Boolean = false,
    adChoice: Int? = null,
    isResetConfig: Boolean = true,
    layoutToAttachAds: ViewGroup,
    layoutContainAds: ViewGroup? = null,
    viewAdsInflateFromXml: View? = LayoutInflater.from(context).inflate(R.layout.layout_native_full_screen, null),
    onAdsClick: (() -> Unit)? = null
) {
    if (checkConditionShowAds(context, spaceNameConfig)) {

        //lay data ve view
        var newAdChoice = adChoice
        var newViewAdsInflateFromXml = viewAdsInflateFromXml

        AdsConstant.listConfigAds[spaceNameConfig]?.let { config ->

            if (isResetConfig){
                config.getConfigNative(
                    context = context,
                    default = ConfigNative(
                        ratio = "360:94",
                        adChoice = AdsConstant.TOP_LEFT,
                        viewAds = LayoutInflater.from(context)
                            .inflate(R.layout.layout_native_medium_logotop_ctabot, null)
                    )
                ).let { configNative ->

                    if (newAdChoice == null) {
                        newAdChoice = configNative.adChoice
                    }

                    if (newViewAdsInflateFromXml == null) {
                        newViewAdsInflateFromXml = configNative.viewAds
                    }
                }

                //set cta color
                runCatching {
                    if (newViewAdsInflateFromXml != null) {
                        val colorStateList = ColorStateList.valueOf(Color.parseColor(config.ctaColor))
                        val ctaButton =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_call_to_action)
                        ctaButton.backgroundTintList = colorStateList
                        ctaButton.setTextColor(Color.parseColor(config.textCTAColor))
                    }
                }

                //set background color
                runCatching {
                    if (newViewAdsInflateFromXml != null) {
                        val adViewHolder =
                            newViewAdsInflateFromXml!!.findViewById<ConstraintLayout>(R.id.adViewHolder)
                        adViewHolder.setBackgroundColor(Color.parseColor(config.backGroundColor))
                    }
                }

                //set content text color
                runCatching {
                    if (newViewAdsInflateFromXml != null) {
                        val headLineText =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_headline)
                        val bodyText = newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_body)
                        headLineText.setTextColor(Color.parseColor(config.textContentColor))
                        bodyText.setTextColor(Color.parseColor(config.textContentColor))
                    }

                }

            }
        }


        var stateNative1 = StateLoadAd.LOADING
        var stateNative2 = StateLoadAd.LOADING
        var stateNative3 = StateLoadAd.LOADING


        var isTimeOut = false
        val timeOutRunnable = Runnable {
            isTimeOut = true
            //show 3 native
            if (stateNative1 == StateLoadAd.HAS_BEEN_OPENED || stateNative2 == StateLoadAd.HAS_BEEN_OPENED || stateNative3 == StateLoadAd.HAS_BEEN_OPENED) return@Runnable
            show3NativeFullScreen(
                spaceNameConfig = spaceNameConfig,
                spaceName1 = spaceName1,
                spaceName2 = spaceName2,
                spaceName3 = spaceName3,
                adChoice = newAdChoice,
                isResetConfig = false,
                layoutToAttachAds = layoutToAttachAds,
                layoutContainAds = layoutContainAds,
                viewAdsInflateFromXml = newViewAdsInflateFromXml,
                onAdsClick = onAdsClick)
        }

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(timeOutRunnable, timeOut)


        fun checkShowNative() {
            if (isTimeOut) return
            if (stateNative1 == StateLoadAd.SUCCESS) {
                //show 1
                handler.removeCallbacks(timeOutRunnable)
                stateNative1 = StateLoadAd.HAS_BEEN_OPENED
                showLoadedNativeFullScreen(
                    spaceNameConfig = spaceNameConfig,
                    spaceName = spaceName1,
                    adChoice = newAdChoice,
                    isResetConfig = false,
                    layoutToAttachAds = layoutToAttachAds,
                    layoutContainAds = layoutContainAds,
                    viewAdsInflateFromXml = newViewAdsInflateFromXml,
                    onAdsClick = onAdsClick
                )
            } else if (stateNative1 == StateLoadAd.LOAD_FAILED && stateNative2 == StateLoadAd.SUCCESS) {
                //show 2
                handler.removeCallbacks(timeOutRunnable)
                stateNative2 = StateLoadAd.HAS_BEEN_OPENED
                showLoadedNativeFullScreen(
                    spaceNameConfig = spaceNameConfig,
                    spaceName = spaceName2,
                    adChoice = newAdChoice,
                    isResetConfig = false,
                    layoutToAttachAds = layoutToAttachAds,
                    layoutContainAds = layoutContainAds,
                    viewAdsInflateFromXml = newViewAdsInflateFromXml,
                    onAdsClick = onAdsClick
                )
            } else if (stateNative1 == StateLoadAd.LOAD_FAILED && stateNative2 == StateLoadAd.LOAD_FAILED && stateNative3 == StateLoadAd.SUCCESS) {
                //show 3
                handler.removeCallbacks(timeOutRunnable)
                stateNative3 = StateLoadAd.HAS_BEEN_OPENED
                showLoadedNativeFullScreen(
                    spaceNameConfig = spaceNameConfig,
                    spaceName = spaceName3,
                    adChoice = newAdChoice,
                    isResetConfig = false,
                    layoutToAttachAds = layoutToAttachAds,
                    layoutContainAds = layoutContainAds,
                    viewAdsInflateFromXml = newViewAdsInflateFromXml,
                    onAdsClick = onAdsClick
                )
            } else if (stateNative1 == StateLoadAd.LOAD_FAILED && stateNative2 == StateLoadAd.LOAD_FAILED && stateNative3 == StateLoadAd.LOAD_FAILED) {
                //ket thuc luong
                handler.removeCallbacks(timeOutRunnable)
            }
        }

        safePreloadAds(
            spaceNameConfig = spaceNameConfig,
            spaceNameAds = spaceName1,
            adChoice = newAdChoice,
            preloadCallback = object : PreloadCallback {
                override fun onLoadDone() {
                    stateNative1 = StateLoadAd.SUCCESS
                    checkShowNative()
                }

                override fun onLoadFail(error: String) {
                    stateNative1 = StateLoadAd.LOAD_FAILED
                    checkShowNative()
                }
            })
        safePreloadAds(
            spaceNameConfig = spaceNameConfig,
            spaceNameAds = spaceName2,
            adChoice = newAdChoice,
            preloadCallback = object : PreloadCallback {
                override fun onLoadDone() {
                    stateNative2 = StateLoadAd.SUCCESS
                    checkShowNative()
                }

                override fun onLoadFail(error: String) {
                    stateNative2 = StateLoadAd.LOAD_FAILED
                    checkShowNative()
                }
            })
        safePreloadAds(
            spaceNameConfig = spaceNameConfig,
            spaceNameAds = spaceName3,
            adChoice = newAdChoice,
            preloadCallback = object : PreloadCallback {
                override fun onLoadDone() {
                    stateNative3 = StateLoadAd.SUCCESS
                    checkShowNative()
                }

                override fun onLoadFail(error: String) {
                    stateNative3 = StateLoadAd.LOAD_FAILED
                    checkShowNative()
                }
            })


    } else {
        layoutToAttachAds.visibility = View.GONE
        layoutContainAds?.visibility = View.GONE
    }
}
