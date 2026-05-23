package pl.itto.adsutil.admob

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError
import com.google.android.libraries.ads.mobile.sdk.appopen.AppOpenAd
import com.google.android.libraries.ads.mobile.sdk.appopen.AppOpenAdEventCallback
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
    private var currentActivity: Activity? = null
    private var loadTime: Long = 0L

    init {
        // application.registerActivityLifecycleCallbacks(this)
        // ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    /** Request an ad
     * Show after loading if needed */
    fun fetchAd(
        adUnitId: String,
        activity: AppCompatActivity? = null,
        callback: OpenAppCallback? = null,
        showNow: Boolean = false
    ) {
        // Have unused ad, no need to fetch another.
        if (isAdAvailable()) {
            return
        }
        Log.d(TAG, "fetchAd: ")
        
        val request = getAdRequest(adUnitId)
        val handler = Handler(Looper.getMainLooper())
        
        AppOpenAd.load(
            request,
            object : AdLoadCallback<AppOpenAd> {
                override fun onAdLoaded(ad: AppOpenAd) {
                    handler.post {
                        this@AppOpenManager.appOpenAd = ad
                        this@AppOpenManager.loadTime = Date().time

                        // Only show immediately if callback is not null
                        if (activity != null && callback != null && showNow) {
                            showAdIfAvailable(activity, adUnitId, callback)
                        }
                    }
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    handler.post {
                        this@AppOpenManager.appOpenAd = null
                        callback?.onAdLoadFailed(adError.message)
                    }
                }
            }
        )
    }

    /** Creates and returns ad request.  */
    private fun getAdRequest(adUnitId: String): AdRequest {
        return AdRequest.Builder(adUnitId).build()
    }

    /** Utility method to check if ad was loaded more than n hours ago.  */
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    /** Utility method that checks if ad exists and can be shown.  */
    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
        Log.d(TAG, "onActivityStarted: ")
        currentActivity = activity
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
        val handler = Handler(Looper.getMainLooper())
        
        handler.post {
            // Only show ad if there is not already an app open ad currently showing
            // and an ad is available.
            if (!isShowingAd && isAdAvailable()) {
                Log.d(TAG, "Activity state: ${activity.lifecycle.currentState}")
                if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                    Log.d(TAG, "Will show ad.")
                    
                    appOpenAd!!.adEventCallback = object : AppOpenAdEventCallback {
                        override fun onAdDismissedFullScreenContent() {
                            handler.post {
                                callback?.onAdDismissed()
                                appOpenAd = null
                                isShowingAd = false
                                fetchAd(adUnitId)
                            }
                        }

                        override fun onAdFailedToShowFullScreenContent(fullScreenContentError: FullScreenContentError) {
                            handler.post {
                                appOpenAd = null
                                isShowingAd = false
                                callback?.onAdDisplayFailed()
                            }
                        }

                        override fun onAdShowedFullScreenContent() {
                            handler.post {
                                callback?.onAdDisplayed()
                                appOpenAd = null
                                isShowingAd = true
                            }
                        }

                        override fun onAdClicked() {
                            // Optional callback
                        }

                        override fun onAdImpression() {
                            // Optional callback
                        }
                    }
                    appOpenAd!!.show(activity)
                }
            } else {
                Log.d(TAG, "Can not show ad.")
                fetchAd(adUnitId, activity, callback)
            }
        }
    }
}