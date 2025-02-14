package pion.tech.pionbase.framework

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import dagger.hilt.android.AndroidEntryPoint
import pion.datlt.libads.AdsController
import pion.tech.pionbase.BuildConfig
import pion.tech.pionbase.R
import pion.tech.pionbase.framework.presentation.common.LoadingDialog
import pion.tech.pionbase.framework.presentation.common.lifecycleCallback.FragmentLifecycleCallbacksImpl
import pion.tech.pionbase.util.Constant

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.registerFragmentLifecycleCallbacks(FragmentLifecycleCallbacksImpl(), true)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_main) //TODO: change name navhost
        initAds()
    }

    fun showLoading() {
        LoadingDialog.getInstance().show(supportFragmentManager)
    }

    fun hiddenLoading() {
        LoadingDialog.getInstance().dismiss()
    }

    private fun initAds() {
        AdsController.init(
            activity = this,
            isDebug = BuildConfig.DEBUG,
            listAppId = arrayListOf(
                getString(R.string.admob_application_id)
            ),
            packageName = packageName,
            navController = getNavHost()
        )
    }



    private fun getNavHost(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerMain) as NavHostFragment
        return navHostFragment.navController
    }
}