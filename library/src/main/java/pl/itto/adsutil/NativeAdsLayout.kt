package pl.itto.adsutil

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAd
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoader
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoaderCallback
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdEventCallback
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdRequest
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdView
import pl.itto.adsutil.Constants.PREF_NAME
import pl.itto.adsutil.Constants.SHOW_ADS

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
    private var adRequest: NativeAdRequest? = null

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
            adRequest = NativeAdRequest.Builder(
                it,
                listOf(NativeAd.NativeAdType.NATIVE)
            ).build()
            Log.d(TAG, "load ads initialized for: $it")
        }

        arr.recycle()
    }

    fun load(callback: AdLoadCallback<NativeAd>) {
        this.callback = callback
        val showAds = pref.getBoolean(SHOW_ADS, true)
        val handler = Handler(Looper.getMainLooper())
        
        if (showAds && adRequest != null) {
            val inflater = LayoutInflater.from(context)
            
            NativeAdLoader.load(
                adRequest!!,
                object : NativeAdLoaderCallback {
                    override fun onNativeAdLoaded(nativeAd: NativeAd) {
                        nativeAd.adEventCallback = object : NativeAdEventCallback {
                            override fun onAdClicked() {
                                Log.d(TAG, "Ad clicked")
                            }

                            override fun onAdImpression() {
                                Log.d(TAG, "Ad impression")
                            }

                            override fun onAdDismissedFullScreenContent() {
                                Log.d(TAG, "Ad closed")
                            }
                        }
                        
                        handler.post {
                            callback.onAdLoaded(nativeAd)
                            
                            val adsLayoutId = if (isNA) {
                                R.layout.ads_native_home
                            } else {
                                R.layout.ads_native_banner
                            }
                            val adView = inflater.inflate(adsLayoutId, null) as NativeAdView
                            populateNativeAdView(nativeAd, adView, isNA)
                            
                            visibility = View.VISIBLE
                            removeAllViews()
                            addView(adView)
                        }
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        val error = "code: ${loadAdError.code}, message: ${loadAdError.message}"
                        Log.e(TAG, "onAdFailedToLoad: $error")
                        handler.post {
                            visibility = View.GONE
                            callback.onAdFailedToLoad()
                        }
                    }
                }
            )
        } else {
            visibility = View.GONE
        }
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView, isNA: Boolean) {
        val headline = adView.findViewById<TextView>(R.id.ad_headline)
        adView.headlineView = headline
        headline.text = nativeAd.headline

        val body = adView.findViewById<TextView>(R.id.ad_body)
        adView.bodyView = body
        if (nativeAd.body == null) {
            body?.visibility = View.INVISIBLE
        } else {
            body?.visibility = View.VISIBLE
            body?.text = nativeAd.body
        }

        val callToAction = adView.findViewById<Button>(R.id.ad_call_to_action)
        adView.callToActionView = callToAction
        if (nativeAd.callToAction == null) {
            callToAction?.visibility = View.INVISIBLE
        } else {
            callToAction?.visibility = View.VISIBLE
            callToAction?.text = nativeAd.callToAction
        }

        val icon = adView.findViewById<ImageView>(R.id.ad_app_icon)
        adView.iconView = icon
        if (nativeAd.icon == null) {
            icon?.visibility = View.GONE
        } else {
            icon?.visibility = View.VISIBLE
            icon?.setImageDrawable(nativeAd.icon?.drawable)
        }

        val starRating = adView.findViewById<RatingBar>(R.id.ad_stars)
        adView.starRatingView = starRating
        if (nativeAd.starRating != null) {
            starRating?.visibility = View.VISIBLE
            starRating?.rating = nativeAd.starRating!!.toFloat()
        } else {
            starRating?.visibility = View.INVISIBLE
        }

        val mediaView = adView.findViewById<com.google.android.libraries.ads.mobile.sdk.nativead.MediaView>(R.id.ad_media)

        if (isNA) {
            val price = adView.findViewById<TextView>(R.id.ad_price)
            adView.priceView = price
            if (nativeAd.price == null) {
                price?.visibility = View.INVISIBLE
            } else {
                price?.visibility = View.VISIBLE
                price?.text = nativeAd.price
            }

            val store = adView.findViewById<TextView>(R.id.ad_store)
            adView.storeView = store
            if (nativeAd.store != null) {
                store?.visibility = View.VISIBLE
                store?.text = nativeAd.store
            } else {
                store?.visibility = View.INVISIBLE
            }

            val advertiser = adView.findViewById<TextView>(R.id.ad_advertiser)
            adView.advertiserView = advertiser
            if (nativeAd.advertiser == null) {
                advertiser?.visibility = View.INVISIBLE
            } else {
                advertiser?.text = nativeAd.advertiser
                advertiser?.visibility = View.VISIBLE
            }
        }

        adView.registerNativeAd(nativeAd, mediaView)
    }
}