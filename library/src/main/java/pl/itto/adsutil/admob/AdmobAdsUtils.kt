package pl.itto.adsutil.admob

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import pl.itto.adsutil.BaseAdsUtils
import pl.itto.adsutil.callback.InterstitialAdCallback
import pl.itto.adsutil.callback.NativeAdCallback
import pl.itto.adsutil.model.InterstitialAdModel
import pl.itto.adsutil.model.NativeAdModel
import pl.itto.adsutil.model.NetworkType


class AdmobAdsUtils private constructor(application: Application) : BaseAdsUtils {
    companion object {
        const val TAG = "AdmobAdsUtils"
        private var _instance: AdmobAdsUtils? = null
        fun getInstance(application: Application): AdmobAdsUtils {
            if (_instance == null) {
                _instance = AdmobAdsUtils(application)
            }
            return _instance!!
        }
    }

    override fun initSdk(context: Context) {
        MobileAds.initialize(context)
    }

    /***
     * Load Interstitial Ad
     */
    fun loadInterstitialAds(
        adsId: String,
        activity: FragmentActivity,
        showNow: Boolean = true,
        callback: InterstitialAdCallback? = null
    ) {
        Log.d(TAG, "loadInterstitial: $adsId")

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(activity, adsId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(p0: InterstitialAd) {
                Log.d(TAG, "on Inter AdLoaded: ")
                val adModel = InterstitialAdModel(NetworkType.ADMOB)
                adModel.adObject = p0
                callback?.onAdLoaded(adModel)
                if (showNow) {
                    val content_callback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            Log.d(TAG, "Ad was dismissed.")
                            // Don't forget to set the ad reference to null so you
                            // don't show the ad a second time.
                            callback?.onAdDismissed()
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                            Log.d(TAG, "Ad failed to show.")
                            // Don't forget to set the ad reference to null so you
                            // don't show the ad a second time.
                            callback?.onAdDisplayFailed()
                        }

                        override fun onAdShowedFullScreenContent() {
                            Log.d(TAG, "Ad showed fullscreen content.")
                            callback?.onAdDisplayed()
                            // Actually we don't need to deal with this method, no need to process
                        }

                        override fun onAdImpression() {
                            callback?.onAdImpression()
                        }
                    }
                    p0.fullScreenContentCallback = content_callback
                    adModel.show(activity)
                }
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                Log.e(TAG, "on Inter AdFailedToLoad: ${p0.message} - ${p0.code}")
            }
        })
    }

    fun loadNativeAds(adId:String, adsContainer:FrameLayout, activity: Activity,
                      existAdModel: NativeAdModel? = null,
                      callback: NativeAdCallback? = null){

    }
}