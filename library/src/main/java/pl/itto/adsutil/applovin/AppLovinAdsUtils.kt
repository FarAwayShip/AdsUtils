package pl.itto.adsutil.applovin

import android.app.Activity
import android.app.Application
import android.os.Handler
import android.util.Log
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import pl.itto.adsutil.callback.InterstitialAdCallback
import pl.itto.adsutil.callback.NativeAdCallback
import pl.itto.adsutil.model.InterstitialAdModel
import pl.itto.adsutil.model.NativeAdModel
import pl.itto.adsutil.model.NetworkType
import java.util.concurrent.TimeUnit

class AppLovinAdsUtils private constructor(application: Application) {
    companion object {
        const val TAG = "AppLovinUtils"
        fun getInstance(application: Application): AppLovinAdsUtils = AppLovinAdsUtils(application)
    }

    private lateinit var interstitialAd: MaxInterstitialAd

    fun loadInterstitialAd(
        adUnitId: String,
        activity: FragmentActivity,
        showNow: Boolean = true,
        callback: InterstitialAdCallback? = null
    ) {
        interstitialAd = MaxInterstitialAd(adUnitId, activity)
        interstitialAd.setListener(object : MaxAdListener {
            override fun onAdLoaded(ad: MaxAd) {
                Log.d(TAG, "onAdLoaded: ")
                // Interstitial ad is ready to be shown. interstitialAd.isReady() will now return 'true'
                val adModel =InterstitialAdModel(NetworkType.APPLOVIN).apply {
                    adObject = interstitialAd
                }
                callback?.onAdLoaded(adModel)
                if (showNow) adModel.show(activity)
            }

            override fun onAdDisplayed(ad: MaxAd) {
                Log.d(TAG, "onAdDisplayed: ")
                callback?.onAdDisplayed()
            }

            override fun onAdHidden(ad: MaxAd) {
                Log.d(TAG, "onAdHidden: ")
                // Interstitial ad is hidden. Pre-load the next ad
//                interstitialAd.loadAd()
                callback?.onAdHidden()
            }

            override fun onAdClicked(ad: MaxAd) {
                Log.d(TAG, "onAdClicked: ")
                callback?.onAdClicked()
            }

            override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                Log.d(TAG, "onAdLoadFailed: ")
                // Interstitial ad failed to load
                // AppLovin recommends that you retry with exponentially higher delays up to a maximum delay (in this case 64 seconds)

//                retryAttempt++
//                val delayMillis =
//                    TimeUnit.SECONDS.toMillis(Math.pow(2.0, Math.min(6.0, retryAttempt)).toLong())
//
//                Handler().postDelayed({ interstitialAd.loadAd() }, delayMillis)
                callback?.onAdLoadFailed("Applovin: $adUnitId - $error")
            }

            override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                Log.d(TAG, "onAdDisplayFailed: ")
                // Interstitial ad failed to display. AppLovin recommends that you load the next ad.
//                interstitialAd.loadAd()
                callback?.onAdDisplayFailed()
            }
        })

        // Load the first ad
        interstitialAd.loadAd()
    }

    /**
     * Load Native Ad
     */
    fun loadNativeAd(
        nativeAdContainer: ViewGroup,
        adUnitId: String,
        activity: Activity,
        existNativeAd: NativeAdModel? = null,
        adCallback: NativeAdCallback? = null
    ) {
        val nativeAdLoader = MaxNativeAdLoader(adUnitId, activity)
        nativeAdLoader.setNativeAdListener(object : MaxNativeAdListener() {

            override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView, ad: MaxAd) {
                // Clean up any pre-existing native ad to prevent memory leaks.
                if (existNativeAd != null) {
                    existNativeAd.adObject?.let {
                        nativeAdLoader.destroy((it as MaxAd))
                    }
                }
                Log.d(TAG, "onNativeAdLoaded: ")

                // Save ad for cleanup.
                val nativeAd = NativeAdModel(NetworkType.APPLOVIN)
                nativeAd.adObject = ad

                // Add ad view to view.
                nativeAdContainer.removeAllViews()
                nativeAdContainer.addView(nativeAdView)
                adCallback?.onAdLoaded(nativeAd)
            }

            override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) {
                // We recommend retrying with exponentially higher delays up to a maximum delay
                Log.e(TAG, "onNativeAdLoadFailed: $adUnitId - ${error.code} - ${error.message}")
                adCallback?.onAdLoadFailed("Applovin - ${error.code} - ${error.message}")
            }

            override fun onNativeAdClicked(ad: MaxAd) {
                // Optional click callback
                Log.d(TAG, "onNativeAdClicked: ")
                adCallback?.onAdClicked()
            }
        })
        nativeAdLoader.loadAd()
    }


}