package pion.tech.pionbase.util

import com.tencent.mmkv.MMKV
import pion.tech.pionbase.MyApplication

class MMKVUtils {

    companion object {

        lateinit var mmkv: MMKV
        fun init(application: MyApplication) {
            MMKV.initialize(application)
            mmkv = MMKV.defaultMMKV()
        }

        var isPremium: Boolean
            get() = try {
                mmkv.decodeBool("isPremium", false)
            } catch (e: Exception) {
                false
            }
            set(value) {
                try {
                    mmkv.encode("isPremium", value)
                } catch (e: Exception) {

                }
            }

        var isFO: Boolean
            get() = try {
                mmkv.decodeBool("isFO", true)
            } catch (e: Exception) {
                false
            }
            set(value) {
                try {
                    mmkv.encode("isFO", value)
                } catch (e: Exception) {

                }
            }

        var sampleData: String?
            get() = try {
                mmkv.decodeString("sampleData", "sample")
            } catch (e: Exception) {
                "sample"
            }
            set(value) {
                try {
                    mmkv.encode("sampleData", value)
                } catch (e: Exception) {

                }
            }
    }
}