package pl.itto.adsutil.admob

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import pl.itto.adsutil.callback.InterstitialAdCallback
import pl.itto.adsutil.callback.OpenAppCallback
import java.util.*


private const val TAG = "AppOpenManager"

/** Prefetches App Open Ads and handles lifecycle detection. */
class AppOpenManager(private val application: Application) :
    LifecycleObserver, Application.ActivityLifecycleCallbacks {
    companion object {
        var isShowingAd: Boolean = false
    }

    private var appOpenAd: AppOpenAd? = null

    private var appCallback: AppOpenAd.AppOpenAdLoadCallback? = null
    private var currentActivity: Activity? = null
    private var loadTime: Long = 0L

    init {
//        application.registerActivityLifecycleCallbacks(this)
//        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    /** Request an ad
     * Show after loading if needed */
    fun fetchAd(
        adUnitId: String,
        activity: AppCompatActivity? = null,
        callback: OpenAppCallback? = null
    ) {
        // Have unused ad, no need to fetch another.
        if (isAdAvailable()) {
            return;
        }
        appCallback = object : AppOpenAd.AppOpenAdLoadCallback() {
            /**
             * Called when an app open ad has loaded.
             *
             * @param ad the loaded app open ad.
             */
            override fun onAdLoaded(ad: AppOpenAd) {
                this@AppOpenManager.appOpenAd = ad
                this@AppOpenManager.loadTime = Date().time

                // Only show immediately if callback is not null
                if (activity != null && callback != null) {
                    showAdIfAvailable(activity, adUnitId, callback)
                }
            }

            /**
             * Called when an app open ad has failed to load.
             *
             * @param loadAdError the error.
             */
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                this@AppOpenManager.appOpenAd = null
                callback?.onAdLoadFailed(loadAdError.message)
            }
        }
        val request = getAdRequest()
        appCallback?.let {
            AppOpenAd.load(
                application,
                adUnitId,
                request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                it
            )
        }
    }

    /** Creates and returns ad request.  */
    private fun getAdRequest(): AdRequest {
        return AdRequest.Builder().build()
    }

    /** Utility method to check if ad was loaded more than n hours ago.  */
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    /** Utility method that checks if ad exists and can be shown.  */
    fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
        Log.d(TAG, "onActivityStarted: ")
        currentActivity = activity;
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    /** Shows the ad if one isn't already showing.
     * Only show if activity is current resumed
     * */
    fun showAdIfAvailable(
        activity: AppCompatActivity,
        adUnitId: String,
        callback: OpenAppCallback?
    ) {
        Log.d(TAG, "showAdIfAvailable: ")
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (!isShowingAd && isAdAvailable()) {
            if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                Log.d(TAG, "Will show ad.")
                val fullScreenContentCallback: FullScreenContentCallback =
                    object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            // Set the reference to null so isAdAvailable() returns false.
                            // Ads dismissed, load another ads for next time
                            callback?.onAdDismissed()
                            appOpenAd = null
                            isShowingAd = false
                            fetchAd(adUnitId)
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                            callback?.onAdLoadFailed(adError?.message ?: "Ad failed to show")
                        }

                        override fun onAdShowedFullScreenContent() {
                            callback?.onAdDisplayed()
                            isShowingAd = true
                        }
                    }
                appOpenAd!!.fullScreenContentCallback = fullScreenContentCallback
                appOpenAd!!.show(activity)
            }
        } else {
            Log.d(TAG, "Can not show ad.")
            fetchAd(adUnitId, activity, callback)
        }
    }

//    /** LifecycleObserver methods  */
//    @OnLifecycleEvent(ON_START)
//    fun onStart() {
//        Log.d(TAG, "onStart")
//        showAdIfAvailable()
//    }
}