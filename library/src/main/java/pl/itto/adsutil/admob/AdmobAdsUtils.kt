package pl.itto.adsutil.admob

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import pl.itto.adsutil.BaseAdsUtils
import pl.itto.adsutil.R
import pl.itto.adsutil.callback.InterstitialAdCallback
import pl.itto.adsutil.callback.NativeAdCallback
import pl.itto.adsutil.callback.OpenAppCallback
import pl.itto.adsutil.model.InterstitialAdModel
import pl.itto.adsutil.model.NativeAdModel
import pl.itto.adsutil.model.NetworkType


class AdmobAdsUtils private constructor(application: Application) : BaseAdsUtils {
    companion object {
        const val TAG = "AdmobAdsUtils"
        private var _instance: AdmobAdsUtils? = null
        private var appOpenManager: AppOpenManager? = null
        fun getInstance(application: Application): AdmobAdsUtils {
            if (_instance == null) {
                _instance = AdmobAdsUtils(application)
            }
            return _instance!!
        }

        fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
            // Set the media view.
            adView.mediaView = adView.findViewById(R.id.ad_media)

            // Set other ad assets.
            adView.headlineView = adView.findViewById(R.id.ad_headline)
            adView.bodyView = adView.findViewById(R.id.ad_body)
            adView.bodyView?.isClickable = false
            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
            adView.iconView = adView.findViewById(R.id.ad_app_icon)
            adView.priceView = adView.findViewById(R.id.ad_price)
            adView.priceView?.isClickable = false
            adView.starRatingView = adView.findViewById(R.id.ad_stars)
            adView.starRatingView?.isClickable = false
            adView.storeView = adView.findViewById(R.id.ad_store)
            adView.storeView?.isClickable = false
            adView.advertiserView = adView.findViewById(R.id.ad_advertiser)
            adView.advertiserView?.isClickable = false
            // The headline and media content are guaranteed to be in every UnifiedNativeAd.
            (adView.headlineView as TextView).text = nativeAd.headline
            adView.mediaView?.mediaContent = nativeAd.mediaContent

            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
            if (nativeAd.body == null) {
                adView.bodyView?.visibility = View.INVISIBLE
            } else {
                adView.bodyView?.visibility = View.VISIBLE
                (adView.bodyView as TextView).text = nativeAd.body
            }

            if (nativeAd.callToAction == null) {
                adView.callToActionView?.visibility = View.INVISIBLE
            } else {
                adView.callToActionView?.visibility = View.VISIBLE
                (adView.callToActionView as Button).text = nativeAd.callToAction
            }

            if (nativeAd.icon == null) {
                adView.iconView?.visibility = View.GONE
            } else {
                (adView.iconView as ImageView).setImageDrawable(
                    nativeAd.icon?.drawable
                )
                adView.iconView?.visibility = View.VISIBLE
            }

            if (nativeAd.price == null) {
                adView.priceView?.visibility = View.INVISIBLE
            } else {
                adView.priceView?.visibility = View.VISIBLE
                (adView.priceView as TextView).text = nativeAd.price
            }

            if (nativeAd.store == null) {
                adView.storeView?.visibility = View.INVISIBLE
            } else {
                adView.storeView?.visibility = View.VISIBLE
                (adView.storeView as TextView).text = nativeAd.store
            }

            if (nativeAd.starRating == null) {
                adView.starRatingView?.visibility = View.INVISIBLE
            } else {
                (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
                adView.starRatingView?.visibility = View.VISIBLE
            }

            if (nativeAd.advertiser == null) {
                adView.advertiserView?.visibility = View.INVISIBLE
            } else {
                (adView.advertiserView as TextView).text = nativeAd.advertiser
                adView.advertiserView?.visibility = View.VISIBLE
            }

            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad.
            adView.setNativeAd(nativeAd)

            // Get the video controller for the ad. One will always be provided, even if the ad doesn't
            // have a video asset.
            val vc = nativeAd.mediaContent?.videoController

            // Updates the UI to say whether or not this ad has a video asset.
            if (vc?.hasVideoContent() == true) {

                // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
                // VideoController will call methods on this object when events occur in the video
                // lifecycle.
                vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
                    override fun onVideoEnd() {
                        // Publishers should allow native ads to complete video playback before
                        // refreshing or replacing them with another ad in the same UI location.
                        super.onVideoEnd()
                    }
                }
            } else {
            }
        }

        fun populateNativeSmallAdView(nativeAd: NativeAd, adView: NativeAdView) {
            // Set the media view.
            adView.mediaView = adView.findViewById(R.id.ad_media)

            // Set other ad assets.
            adView.headlineView = adView.findViewById(R.id.ad_headline)
            adView.bodyView = adView.findViewById(R.id.ad_body)
            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
            adView.iconView = adView.findViewById(R.id.ad_app_icon)
            adView.starRatingView = adView.findViewById(R.id.ad_stars)
            // The headline and media content are guaranteed to be in every UnifiedNativeAd.
            (adView.headlineView as TextView).text = nativeAd.headline

            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
            if (nativeAd.body == null) {
                adView.bodyView?.visibility = View.INVISIBLE
            } else {
                adView.bodyView?.visibility = View.VISIBLE
                (adView.bodyView as TextView).text = nativeAd.body
            }

            if (nativeAd.callToAction == null) {
                adView.callToActionView?.visibility = View.INVISIBLE
            } else {
                adView.callToActionView?.visibility = View.VISIBLE
                (adView.callToActionView as Button).text = nativeAd.callToAction
            }

            if (nativeAd.icon == null) {
                adView.iconView?.visibility = View.GONE
            } else {
                (adView.iconView as ImageView).setImageDrawable(
                    nativeAd.icon?.drawable
                )
                adView.iconView?.visibility = View.VISIBLE
            }

            if (nativeAd.starRating == null) {
                adView.starRatingView?.visibility = View.INVISIBLE
            } else {
                (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
                adView.starRatingView?.visibility = View.VISIBLE
            }

            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad.
            adView.setNativeAd(nativeAd)
        }


        const val TEST_INTER_AD_ID = "ca-app-pub-3940256099942544/1033173712"
        const val TEST_NATIVE_AD_ID = "ca-app-pub-3940256099942544/2247696110"
        const val TEST_OPEN_APP_AD_ID = "ca-app-pub-3940256099942544/9257395921"
    }

    override fun initSdk(context: Context) {
        Log.d(TAG, "initSdk: ")
        MobileAds.initialize(
            context
        ) {
        }
    }

    fun initAppOpen(application: Application) {
        Log.d(TAG, "initAppOpen: ")
        appOpenManager = AppOpenManager(application)
    }

    fun loadAppOpenAds(
        adsId: String,
        activity: AppCompatActivity? = null,
        callback: OpenAppCallback? = null,
        showNow: Boolean = false
    ) {
        Log.d(TAG, "loadAppOpenAds: $adsId")
        appOpenManager?.fetchAd(adsId, activity = activity, callback = callback, showNow = showNow)
    }

    fun showAppOpenAds(adsId: String, activity: AppCompatActivity, callback: OpenAppCallback) {
        Log.d(TAG, "showAppOpenAds: ")
        appOpenManager?.showAdIfAvailable(activity, adsId, callback)
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
//                    val content_callback = object : FullScreenContentCallback() {
//                        override fun onAdDismissedFullScreenContent() {
//                            Log.d(TAG, "Ad was dismissed.")
//                            // Don't forget to set the ad reference to null so you
//                            // don't show the ad a second time.
//                            callback?.onAdDismissed()
//                        }
//
//                        override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
//                            Log.d(TAG, "Ad failed to show.")
//                            // Don't forget to set the ad reference to null so you
//                            // don't show the ad a second time.
//                            callback?.onAdDisplayFailed()
//                        }
//
//                        override fun onAdShowedFullScreenContent() {
//                            Log.d(TAG, "Ad showed fullscreen content.")
//                            callback?.onAdDisplayed()
//                            // Actually we don't need to deal with this method, no need to process
//                        }
//
//                        override fun onAdImpression() {
//                            callback?.onAdImpression()
//                        }
//                    }
                    adModel.show(activity, callback)
                }
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                Log.e(TAG, "on Inter AdFailedToLoad: ${p0.message} - ${p0.code}")
            }
        })
    }

    fun loadNativeAds(
        adId: String, adsContainer: FrameLayout, activity: Activity,
        existAdModel: NativeAdModel? = null,
        callback: NativeAdCallback? = null,
        altAdId: String? = null
    ) {
        Log.d(TAG, "loadNativeAds: ")
        var builder = AdLoader.Builder(activity, adId)
            .forNativeAd { nativeAd ->
                onNativeAdLoaded(callback, activity, nativeAd, adsContainer)
            }
        val nativeOption =
            NativeAdOptions.Builder().setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                .build()
        builder.withNativeAdOptions(
            nativeOption
        )

        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdClosed() {
                Log.d(TAG, "onAdClosed: ")
                super.onAdClosed()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                callback?.onAdClicked()
            }

            override fun onAdImpression() {
                Log.d(TAG, "onAdImpression: ")
                callback?.onAdImpression()
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                val error =
                    """
           domain: ${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}
          """"
                Log.e(TAG, "on Native Ad failed to load: $error")
                if (!altAdId.isNullOrEmpty()) {
                    Log.d(TAG, "Alt ad id defined, so reload with alt ad id")
                    loadNativeAds(altAdId, adsContainer, activity, existAdModel, callback)
                    return
                }
                callback?.onAdLoadFailed(error)
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())

    }

    /**
     * Called after a native ad is loaded successfully
     */
    private fun onNativeAdLoaded(
        callback: NativeAdCallback?,
        activity: Activity,
        nativeAd: NativeAd,
        adsContainer: FrameLayout
    ) {
        Log.d(TAG, "Native ad loaded")
        val nativeAdModel = NativeAdModel(NetworkType.ADMOB)
        nativeAdModel.adObject
        callback?.onAdLoaded(nativeAdModel)

        if (activity.isDestroyed) {
            nativeAd.destroy()
            return
        }
        // Assumes that your ad layout is in a file call native_ad_layout.xml
        // in the res/layout folder
        val layoutInflater = activity.layoutInflater
        val adView = layoutInflater
            .inflate(R.layout.ads_native_home, null) as NativeAdView
        // This method sets the text, images and the native ad, etc into the ad
        // view.
        populateNativeAdView(nativeAd, adView)
        // Assumes you have a placeholder FrameLayout in your View layout
        // (with id ad_frame) where the ad is to be placed.
        adsContainer.visibility = View.VISIBLE
        adsContainer.removeAllViews()
        adsContainer.addView(adView)
        callback?.onAdPopulated()
    }

    fun loadNativeSmallAds(
        adId: String, adsContainer: FrameLayout, activity: Activity,
        existAdModel: NativeAdModel? = null,
        callback: NativeAdCallback? = null,
        altAdId: String? = null
    ) {
        Log.d(TAG, "loadNativeAds: ")
        val builder = AdLoader.Builder(activity, adId)
            .forNativeAd { nativeAd ->
                onNativeSmallAdLoaded(callback, activity, nativeAd, adsContainer)
            }
        val nativeOption =
            NativeAdOptions.Builder().setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                .build()
        builder.withNativeAdOptions(
            nativeOption
        )


        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdClosed() {
                Log.d(TAG, "onAdClosed: ")
                super.onAdClosed()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                callback?.onAdClicked()
            }

            override fun onAdImpression() {
                Log.d(TAG, "onAdImpression: ")
                callback?.onAdImpression()
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                val error =
                    """
           domain: ${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}
          """"
                Log.e(TAG, "onAdFailedToLoad: $error")
                if (!altAdId.isNullOrEmpty()) {
                    Log.d(TAG, "Alt adId defined, so reload with alt adId")
                    loadNativeSmallAds(altAdId, adsContainer, activity, existAdModel, callback)
                    return
                }
                callback?.onAdLoadFailed(error)
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())

    }

    private fun onNativeSmallAdLoaded(
        callback: NativeAdCallback?,
        activity: Activity,
        nativeAd: NativeAd,
        adsContainer: FrameLayout
    ) {
        Log.d(TAG, "Native small ad loaded")
        val nativeAdModel = NativeAdModel(NetworkType.ADMOB)
        nativeAdModel.adObject
        callback?.onAdLoaded(nativeAdModel)

        if (activity.isDestroyed) {
            nativeAd.destroy()
            return
        }
        // Assumes that your ad layout is in a file call native_ad_layout.xml
        // in the res/layout folder
        val layoutInflater = activity.layoutInflater
        val adView = layoutInflater
            .inflate(R.layout.ads_native_banner, null) as NativeAdView
        // This method sets the text, images and the native ad, etc into the ad
        // view.
        populateNativeSmallAdView(nativeAd, adView)
        // Assumes you have a placeholder FrameLayout in your View layout
        // (with id ad_frame) where the ad is to be placed.
        adsContainer.visibility = View.VISIBLE
        adsContainer.removeAllViews()
        adsContainer.addView(adView)
        callback?.onAdPopulated()
    }
}