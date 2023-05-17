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