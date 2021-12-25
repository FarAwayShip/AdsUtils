package pl.itto.adsutil

/**
 * Created by PL_itto-PC on 10/27/2021
 **/

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

class AdsUtil private constructor() {

    interface NativeAdsCallback {
        fun adLoaded(nativeAd: NativeAd)
        fun adLoadFailed()
    }

    companion object {
        const val TAG = "AdsUtil"
        val instance: AdsUtil by lazy { AdsUtil() }
    }


    enum class AdType {
        SPLASH, SCAN, SETTING, NATIVE_RESULT, RECEIVING, HISTORY, IMAGE, CREATE
    }

    fun loadInterstitial(context: Context, adsId: String, listener: InterstitialAdLoadCallback) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, adsId, adRequest, listener)
    }

    /**
     * Load Native ads in Small size (use for list)
     */
    fun loadNativeAdsSmall(
        context: Activity,
        parentView: FrameLayout,
        layoutInflater: LayoutInflater,
        adsId: String,
        adsCallBack: NativeAdsCallback? = null
    ) {
        val builder = AdLoader.Builder(context, adsId)
            .forNativeAd { nativeAd ->
                Log.d(TAG, "loadNativeAds: ")
                adsCallBack?.adLoaded(nativeAd)
                if (context.isDestroyed) {
                    nativeAd.destroy()
                    return@forNativeAd
                }
                // Assumes that your ad layout is in a file call native_ad_layout.xml
                // in the res/layout folder
                val adView = layoutInflater
                    .inflate(R.layout.ads_native_banner, null) as NativeAdView
                // This method sets the text, images and the native ad, etc into the ad
                // view.
                populateNativeAdSmallView(nativeAd, adView)
                // Assumes you have a placeholder FrameLayout in your View layout
                // (with id ad_frame) where the ad is to be placed.
                parentView.visibility = View.VISIBLE
                parentView.removeAllViews()
                parentView.addView(adView)
            }

        val videoOptions = VideoOptions.Builder()
            .setStartMuted(true)
            .build()

        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()

        builder.withNativeAdOptions(adOptions)
        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdClosed() {
                Log.d(TAG, "onAdClosed: ")
                super.onAdClosed()
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                val error =
                    """
           domain: ${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}
          """"
                Log.e(TAG, "onAdFailedToLoad: $error")
                adsCallBack?.adLoadFailed()
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun loadNativeAds(
        context: Activity,
        parentView: FrameLayout,
        layoutInflater: LayoutInflater,
        adsId: String,
        adsCallBack: NativeAdsCallback? = null
    ) {

        val builder = AdLoader.Builder(context, adsId)
            .forNativeAd { nativeAd ->
                Log.d(TAG, "loadNativeAds: ")
                adsCallBack?.adLoaded(nativeAd)
                if (context.isDestroyed) {
                    nativeAd.destroy()
                    return@forNativeAd
                }
                // Assumes that your ad layout is in a file call native_ad_layout.xml
                // in the res/layout folder
                val adView = layoutInflater
                    .inflate(R.layout.ads_native_home, null) as NativeAdView
                // This method sets the text, images and the native ad, etc into the ad
                // view.
                populateNativeAdView(nativeAd, adView)
                // Assumes you have a placeholder FrameLayout in your View layout
                // (with id ad_frame) where the ad is to be placed.
                parentView.visibility = View.VISIBLE
                parentView.removeAllViews()
                parentView.addView(adView)
            }

        val videoOptions = VideoOptions.Builder()
            .setStartMuted(true)
            .build()

        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()

        builder.withNativeAdOptions(adOptions)
        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdClosed() {
                Log.d(TAG, "onAdClosed: ")
                super.onAdClosed()
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                val error =
                    """
           domain: ${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}
          """"
                Log.e(TAG, "onAdFailedToLoad: $error")
                adsCallBack?.adLoadFailed()
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        // Set the media view.
        adView.mediaView = adView.findViewById(R.id.ad_media)

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // The headline and media content are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView.setMediaContent(nativeAd.mediaContent)

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView.visibility = View.INVISIBLE
        } else {
            adView.bodyView.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView.visibility = View.INVISIBLE
        } else {
            adView.callToActionView.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon.drawable
            )
            adView.iconView.visibility = View.VISIBLE
        }

        if (nativeAd.price == null) {
            adView.priceView.visibility = View.INVISIBLE
        } else {
            adView.priceView.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }

        if (nativeAd.store == null) {
            adView.storeView.visibility = View.INVISIBLE
        } else {
            adView.storeView.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }

        if (nativeAd.starRating == null) {
            adView.starRatingView.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView.visibility = View.VISIBLE
        }

        if (nativeAd.advertiser == null) {
            adView.advertiserView.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        val vc = nativeAd.mediaContent.videoController

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {

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

    private fun populateNativeAdSmallView(nativeAd: NativeAd, adView: NativeAdView) {
        // Set the media view.

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
            adView.bodyView.visibility = View.INVISIBLE
        } else {
            adView.bodyView.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView.visibility = View.INVISIBLE
        } else {
            adView.callToActionView.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon.drawable
            )
            adView.iconView.visibility = View.VISIBLE
        }


        if (nativeAd.starRating == null) {
            adView.starRatingView.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
    }
}
