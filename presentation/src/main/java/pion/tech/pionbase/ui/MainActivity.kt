package pion.tech.pionbase.ui

import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import pion.datlt.libads.AdsController
import pion.tech.pionbase.BuildConfig
import pion.tech.pionbase.R
import pion.tech.pionbase.ui.common.LoadingDialog
import pion.tech.pionbase.ui.common.lifecycleCallback.FragmentLifecycleCallbacksImpl

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.registerFragmentLifecycleCallbacks(
            FragmentLifecycleCallbacksImpl(),
            true
        )
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

    fun initAppResumeAds() {
        AdsController.getInstance().initResumeAds(
            lifecycle = lifecycle,
            listSpaceName = listOf("appresume_openad1", "appresume_openad2", "appresume_openad3"),
            onShowOpenApp = {
                findViewById<TextView>(R.id.viewShowOpenApp).isVisible = true
            },
            onStartToShowOpenAds = {
                findViewById<TextView>(R.id.viewShowOpenApp).isVisible = true
            },
            onCloseOpenApp = {
                findViewById<TextView>(R.id.viewShowOpenApp).isVisible = false
            },
            onPaidEvent = {
                //do nothing
            })
    }


    private fun getNavHost(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerMain) as NavHostFragment
        return navHostFragment.navController
    }
}