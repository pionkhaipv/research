package pion.datlt.libads.model

import android.content.Context
import android.view.LayoutInflater
import com.google.gson.annotations.SerializedName
import pion.datlt.libads.R
import pion.datlt.libads.utils.AdDef
import pion.datlt.libads.utils.AdsConstant

class ConfigAds {
    @SerializedName("nameConfig")
    val nameConfig: String = ""

    @SerializedName("isOn")
    val isOn: Boolean = false

    @SerializedName("type")
    val type: String = "interstitial"

    @SerializedName("ctaColor")
    val ctaColor: String = "#2F8FE6"

    @SerializedName("network")
    val network: String = AdDef.NETWORK.GOOGLE

    @SerializedName("timeDelayShowInter")
    val timeDelayShowInter: Int? = null

    @SerializedName("layoutTemplate")
    val layoutTemplate: String = SMALL_LOGOTOP_CTABOT

    @SerializedName("backGroundColor")
    val backGroundColor: String = "#E8E6E6"

    @SerializedName("textContentColor")
    val textContentColor: String = "#444444"

    @SerializedName("textCTAColor")
    val textCTAColor: String = "#FFFFFF"


    fun getConfigNative(
        context: Context?,
        default: ConfigNative
    ): ConfigNative {
        val adChoice: Int
        val ratio: String
        val viewAds = when (this.layoutTemplate) {
            LARGER_CTA_BOT -> {
                ratio = "360:228"
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_larger_cta_bot, null)
                }
            }

            LARGER_CTA_BOT_NO_PADDING -> {
                ratio = "300:200"
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_larger_cta_bot_no_padding, null)
                }
            }

            MEDIUM_CTA_MIDDLE -> {
                ratio = "360:192"
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_medium_cta_middle, null)
                }
            }

            MEDIUM_CTA_MIDDLE_NO_PADDING -> {
                ratio = "300:161"
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_medium_cta_middle_no_padding, null)
                }
            }

            MEDIUM_CTA_RIGHT -> {
                ratio = "360:189"
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_medium_cta_right, null)
                }
            }

            MEDIUM_CTA_RIGHT_NO_PADDING -> {
                ratio = "360:162"
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_medium_cta_right_no_padding, null)
                }
            }

            SMALL_CTA_RIGHT -> {
                ratio = "360:103"
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_small_cta_right, null)
                }
            }

            SMALL_LOGOTOP_CTABOT -> {
                ratio = "360:94"
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_medium_logotop_ctabot, null)
                }
            }

            SMALL_LOGOTOP_CTATOP -> {
                ratio = "360:94"
                adChoice = AdsConstant.TOP_RIGHT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_medium_logotop_ctatop, null)
                }
            }

            SMALL_LOGOTOP_CTABOT_NO_ICON -> {
                ratio = "360:125"
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_small_logotop_ctabot_no_icon, null)
                }
            }

            SMALL_LOGOTOP_CTATOP_NO_ICON -> {
                ratio = "360:125"
                adChoice = AdsConstant.BOTTOM_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_small_logotop_ctatop_no_icon, null)
                }
            }


            //collapsible
            MEDIUM_ICON_CTAMIDDLE_COLLAPSIBLE -> {
                ratio = "360:159"
                adChoice = AdsConstant.TOP_RIGHT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_medium_icon_ctamidle_collapsible, null)
                }
            }

            MEDIUM_ICON_CTABOT_COLLAPSIBLE -> {
                ratio = "360:108"
                adChoice = AdsConstant.TOP_RIGHT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_medium_icon_ctabot_collapsible, null)
                }
            }

            MEDIUM_CTABOT_COLLAPSIBLE -> {
                ratio = "360:108"
                adChoice = AdsConstant.TOP_RIGHT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_medium_ctabot_collapsible, null)
                }
            }

            SMALL_ICON_CTARIGHT_COLLAPSIBLE -> {
                ratio = "360:63"
                adChoice = AdsConstant.TOP_RIGHT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_small_icon_ctaright_collapsible, null)
                }
            }


            else -> {
                ratio = default.ratio
                adChoice = default.adChoice
                default.viewAds
            }
        }
        return ConfigNative(
            ratio = ratio,
            adChoice = adChoice,
            viewAds = viewAds
        )
    }

    companion object {
        //layout ads
        const val LARGER_CTA_BOT = "Larger_cta_bot"
        const val LARGER_CTA_BOT_NO_PADDING = "Larger_cta_bot_no_padding"
        const val MEDIUM_CTA_MIDDLE = "Medium_cta_middle"
        const val MEDIUM_CTA_MIDDLE_NO_PADDING = "Medium_cta_middle_no_padding"
        const val MEDIUM_CTA_RIGHT = "Medium_cta_right"
        const val MEDIUM_CTA_RIGHT_NO_PADDING = "Medium_cta_right_no_padding"
        const val SMALL_CTA_RIGHT = "Small_cta_right"
        const val SMALL_LOGOTOP_CTABOT = "Small_logotop_ctabot"
        const val SMALL_LOGOTOP_CTATOP = "Small_logotop_ctatop"
        const val SMALL_LOGOTOP_CTABOT_NO_ICON = "Small_logotop_ctabot_noicon"
        const val SMALL_LOGOTOP_CTATOP_NO_ICON = "Small_logotop_ctatop_noicon"

        //collapsible
        const val MEDIUM_ICON_CTAMIDDLE_COLLAPSIBLE = "Medium_icon_ctamiddle_collapsible"
        const val MEDIUM_ICON_CTABOT_COLLAPSIBLE = "Medium_Icon_ctabot_collapsible"
        const val MEDIUM_CTABOT_COLLAPSIBLE = "Medium_ctabot_collapsible"
        const val SMALL_ICON_CTARIGHT_COLLAPSIBLE = "Small_icon_ctaright_collapsible"


    }
}