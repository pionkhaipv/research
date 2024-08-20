package pion.tech.pionbase.framework.presentation.common.lifecycleCallback

import android.app.Activity
import android.app.Application
import android.os.Bundle
import timber.log.Timber

class ActivityLifecycleCallbacksImpl : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        Timber.tag(activity.componentName.shortClassName).i("onActivityCreated")
    }

    override fun onActivityStarted(activity: Activity) {
        Timber.tag(activity.componentName.shortClassName).i("onActivityStarted")
    }

    override fun onActivityResumed(activity: Activity) {
        Timber.tag(activity.componentName.shortClassName).i("onActivityResumed")
    }

    override fun onActivityPaused(activity: Activity) {
        Timber.tag(activity.componentName.shortClassName).i("onActivityPaused")
    }

    override fun onActivityStopped(activity: Activity) {
        Timber.tag(activity.componentName.shortClassName).i("onActivityStopped")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Timber.tag(activity.componentName.shortClassName).i("onActivitySaveInstanceState")
    }

    override fun onActivityDestroyed(activity: Activity) {
        Timber.tag(activity.componentName.shortClassName).i("onActivityDestroyed")
    }
}