package pl.itto.adsutil

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import pl.itto.adsutil.Constants.PREF_NAME
import pl.itto.adsutil.Constants.SHOW_ADS
//import pl.itto.adsutil.extension.dpToPx

class NativeAdsLayout : CardView {

    companion object {
        val TAG = NativeAdsLayout::class.simpleName
    }

    private val pref: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    init {
        radius = 10f
    }

    private lateinit var callback: AdLoadCallback<NativeAd>
    private lateinit var adLoader: AdLoader


    private var adsId: String? = null
    private var isNA = true

    constructor(context: Context) : super(context)

    @SuppressLint("CustomViewStyleable")
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val inflater = LayoutInflater.from(context)
        val arr = context.obtainStyledAttributes(attrs, R.styleable.NativeAdView)
        adsId = arr.getString(R.styleable.NativeAdView_ads_id)
        isNA = arr.getBoolean(R.styleable.NativeAdView_is_na, true)

        val shimmerLayoutId = if (isNA) {
            R.layout.load_fb_native
        } else {
            R.layout.load_fb_banner
        }
        val shimmer = inflater.inflate(shimmerLayoutId, null)
        addView(shimmer)

        adsId?.let {
            val builder = AdLoader.Builder(context, it)
                .forNativeAd { nativeAd ->
                    callback.onAdLoaded(nativeAd)
                    // Assumes that your ad layout is in a file call native_ad_layout.xml
                    // in the res/layout folder
                    val adsLayoutId = if (isNA) {
                        R.layout.ads_native_home
                    } else {
                        R.layout.ads_native_banner
                    }
                    val adView =
                        inflater.inflate(adsLayoutId, null) as NativeAdView
                    // This method sets the text, images and the native ad, etc into the ad
                    // view.
                    populateNativeAdView(nativeAd, adView, isNA)
                    // Assumes you have a placeholder FrameLayout in your View layout
                    // (with id ad_frame) where the ad is to be placed.
                    visibility = View.VISIBLE
                    removeAllViews()
                    addView(adView)
                }

            val videoOptions = VideoOptions.Builder()
                .setStartMuted(true)
                .build()

            val adOptions = NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build()

            builder.withNativeAdOptions(adOptions)
            adLoader = builder.withAdListener(object : AdListener() {
                override fun onAdClosed() {
                    Log.d(AdsManager.TAG, "onAdClosed: ")
                    super.onAdClosed()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    val error =
                        """
           domain: ${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}
          """"
                    Log.e(AdsManager.TAG, "onAdFailedToLoad: $error")
                    visibility = View.GONE
                    callback.onAdFailedToLoad()
                }
            }).build()

            Log.d(TAG, "load ads")
        }

        arr.recycle()
    }

    fun load(callback: AdLoadCallback<NativeAd>) {
        this.callback = callback
        val showAds = pref.getBoolean(SHOW_ADS, true)
        if (showAds) {
            adLoader.loadAd(AdRequest.Builder().build())
        } else {
            visibility = View.GONE
        }
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView, isNA: Boolean) {
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
        adView.bodyView?.apply {
            if (nativeAd.body == null) {
                visibility = View.INVISIBLE
            } else {
                visibility = View.VISIBLE
                (this as TextView).text = nativeAd.body
            }
        }

        adView.callToActionView?.apply {
            if (nativeAd.callToAction == null) {
                visibility = View.INVISIBLE
            } else {
                visibility = View.VISIBLE
                (adView.callToActionView as Button).text = nativeAd.callToAction
            }
        }

        adView.iconView?.apply {
            if (nativeAd.icon == null) {
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE
                nativeAd.icon?.let {
                    (this as ImageView).setImageDrawable(drawable)
                }
            }
        }

        adView.starRatingView?.apply {
            if (nativeAd.starRating != null) {
                visibility = View.VISIBLE
                (this as RatingBar).rating = nativeAd.starRating!!.toFloat()
            } else {
                visibility = View.INVISIBLE
            }
        }

        if (isNA) {
            // Set the media view.
            adView.mediaView = adView.findViewById(R.id.ad_media)
            adView.priceView = adView.findViewById(R.id.ad_price)
            adView.storeView = adView.findViewById(R.id.ad_store)
            adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

            adView.mediaView?.apply {
                nativeAd.mediaContent?.let {
                    this.setMediaContent(it)
                    // Get the video controller for the ad. One will always be provided, even if the ad doesn't
                    // have a video asset.
                    val vc = it.videoController

                    // Updates the UI to say whether or not this ad has a video asset.
                    if (vc.hasVideoContent()) {
                        // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
                        // VideoController will call methods on this object when events occur in the video
                        // lifecycle.
                        vc.videoLifecycleCallbacks =
                            object : VideoController.VideoLifecycleCallbacks() {
                                override fun onVideoEnd() {
                                    // Publishers should allow native ads to complete video playback before
                                    // refreshing or replacing them with another ad in the same UI location.
                                    super.onVideoEnd()
                                }
                            }
                    }
                }
            }

            adView.priceView?.apply {
                if (nativeAd.price == null) {
                    visibility = View.INVISIBLE
                } else {
                    visibility = View.VISIBLE
                    (this as TextView).text = nativeAd.price
                }
            }

            adView.storeView?.apply {
                if (nativeAd.store != null) {
                    visibility = View.VISIBLE
                    (this as TextView).text = nativeAd.store
                } else {
                    visibility = View.INVISIBLE
                }
            }

            adView.advertiserView?.apply {
                if (nativeAd.advertiser == null) {
                    visibility = View.INVISIBLE
                } else {
                    (this as TextView).text = nativeAd.advertiser
                    visibility = View.VISIBLE
                }
            }
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)
    }
}