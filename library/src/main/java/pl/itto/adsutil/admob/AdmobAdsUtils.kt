package pl.itto.adsutil.admob

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAdEventCallback
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAd
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoader
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoaderCallback
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdEventCallback
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdRequest
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdView
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
                body?.isClickable = false
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
                icon?.setImageDrawable(nativeAd.icon?.drawable)
                icon?.visibility = View.VISIBLE
            }

            val price = adView.findViewById<TextView>(R.id.ad_price)
            adView.priceView = price
            if (nativeAd.price == null) {
                price?.visibility = View.INVISIBLE
            } else {
                price?.visibility = View.VISIBLE
                price?.text = nativeAd.price
                price?.isClickable = false
            }

            val store = adView.findViewById<TextView>(R.id.ad_store)
            adView.storeView = store
            if (nativeAd.store == null) {
                store?.visibility = View.INVISIBLE
            } else {
                store?.visibility = View.VISIBLE
                store?.text = nativeAd.store
                store?.isClickable = false
            }

            val starRating = adView.findViewById<RatingBar>(R.id.ad_stars)
            adView.starRatingView = starRating
            if (nativeAd.starRating == null) {
                starRating?.visibility = View.INVISIBLE
            } else {
                starRating?.rating = nativeAd.starRating!!.toFloat()
                starRating?.visibility = View.VISIBLE
                starRating?.isClickable = false
            }

            val advertiser = adView.findViewById<TextView>(R.id.ad_advertiser)
            adView.advertiserView = advertiser
            if (nativeAd.advertiser == null) {
                advertiser?.visibility = View.INVISIBLE
            } else {
                advertiser?.text = nativeAd.advertiser
                advertiser?.visibility = View.VISIBLE
                advertiser?.isClickable = false
            }

            val mediaView = adView.findViewById<com.google.android.libraries.ads.mobile.sdk.nativead.MediaView>(R.id.ad_media)

            adView.registerNativeAd(nativeAd, mediaView)
        }

        fun populateNativeSmallAdView(nativeAd: NativeAd, adView: NativeAdView) {
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
                icon?.setImageDrawable(nativeAd.icon?.drawable)
                icon?.visibility = View.VISIBLE
            }

            val starRating = adView.findViewById<RatingBar>(R.id.ad_stars)
            adView.starRatingView = starRating
            if (nativeAd.starRating == null) {
                starRating?.visibility = View.INVISIBLE
            } else {
                starRating?.rating = nativeAd.starRating!!.toFloat()
                starRating?.visibility = View.VISIBLE
            }

            adView.registerNativeAd(nativeAd, null)
        }

        const val TEST_INTER_AD_ID = "ca-app-pub-3940256099942544/1033173712"
        const val TEST_NATIVE_AD_ID = "ca-app-pub-3940256099942544/2247696110"
        const val TEST_OPEN_APP_AD_ID = "ca-app-pub-3940256099942544/9257395921"
    }

    override fun initSdk(context: Context) {
        Log.d(TAG, "initSdk: ")
        Thread {
            try {
                val appId = context.getString(R.string.ads_test_app_id)
                val config = com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig.Builder(appId).build()
                MobileAds.initialize(context, config) { status ->
                    Log.d(TAG, "GMA Next-Gen SDK Initialized successfully: $status")
                }
            } catch (e: Exception) {
                Log.e(TAG, "GMA Next-Gen SDK Initialization failed", e)
            }
        }.start()
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

        val adRequest = AdRequest.Builder(adsId).build()
        val handler = Handler(Looper.getMainLooper())
        
        InterstitialAd.load(
            adRequest,
            object : AdLoadCallback<InterstitialAd> {
                override fun onAdLoaded(p0: InterstitialAd) {
                    Log.d(TAG, "on Inter AdLoaded: ")
                    val adModel = InterstitialAdModel(NetworkType.ADMOB).apply {
                        adObject = p0
                    }
                    handler.post {
                        callback?.onAdLoaded(adModel)
                    }
                    if (showNow) {
                        adModel.show(activity, callback)
                    }
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    Log.e(TAG, "on Inter AdFailedToLoad: ${p0.message} - ${p0.code}")
                    handler.post {
                        callback?.onAdLoadFailed(p0.message)
                    }
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
        val handler = Handler(Looper.getMainLooper())
        val adRequest = NativeAdRequest.Builder(
            adId,
            listOf(NativeAd.NativeAdType.NATIVE)
        ).build()

        NativeAdLoader.load(
            adRequest,
            object : NativeAdLoaderCallback {
                override fun onNativeAdLoaded(nativeAd: NativeAd) {
                    onNativeAdLoaded(callback, activity, nativeAd, adsContainer)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    val error = "code: ${loadAdError.code}, message: ${loadAdError.message}"
                    Log.e(TAG, "on Native Ad failed to load: $error")
                    if (!altAdId.isNullOrEmpty()) {
                        Log.d(TAG, "Alt ad id defined, so reload with alt ad id")
                        loadNativeAds(altAdId, adsContainer, activity, existAdModel, callback, null)
                        return
                    }
                    handler.post {
                        callback?.onAdLoadFailed(error)
                    }
                }
            }
        )
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
        val handler = Handler(Looper.getMainLooper())
        val nativeAdModel = NativeAdModel(NetworkType.ADMOB).apply {
            adObject = nativeAd
        }

        nativeAd.adEventCallback = object : NativeAdEventCallback {
            override fun onAdClicked() {
                handler.post {
                    callback?.onAdClicked()
                }
            }

            override fun onAdImpression() {
                handler.post {
                    callback?.onAdImpression()
                }
            }

            override fun onAdDismissedFullScreenContent() {
                handler.post {
                    callback?.onAdClosed()
                }
            }
        }

        handler.post {
            callback?.onAdLoaded(nativeAdModel)

            if (activity.isDestroyed) {
                nativeAd.destroy()
                return@post
            }

            val layoutInflater = activity.layoutInflater
            val adView = layoutInflater
                .inflate(R.layout.ads_native_home, null) as NativeAdView

            populateNativeAdView(nativeAd, adView)

            adsContainer.visibility = View.VISIBLE
            adsContainer.removeAllViews()
            adsContainer.addView(adView)
            callback?.onAdPopulated()
        }
    }

    fun loadNativeSmallAds(
        adId: String, adsContainer: FrameLayout, activity: Activity,
        existAdModel: NativeAdModel? = null,
        callback: NativeAdCallback? = null,
        altAdId: String? = null
    ) {
        Log.d(TAG, "loadNativeSmallAds: ")
        val handler = Handler(Looper.getMainLooper())
        val adRequest = NativeAdRequest.Builder(
            adId,
            listOf(NativeAd.NativeAdType.NATIVE)
        ).build()

        NativeAdLoader.load(
            adRequest,
            object : NativeAdLoaderCallback {
                override fun onNativeAdLoaded(nativeAd: NativeAd) {
                    onNativeSmallAdLoaded(callback, activity, nativeAd, adsContainer)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    val error = "code: ${loadAdError.code}, message: ${loadAdError.message}"
                    Log.e(TAG, "onAdFailedToLoad (Small): $error")
                    if (!altAdId.isNullOrEmpty()) {
                        Log.d(TAG, "Alt adId defined, so reload with alt adId")
                        loadNativeSmallAds(altAdId, adsContainer, activity, existAdModel, callback, null)
                        return
                    }
                    handler.post {
                        callback?.onAdLoadFailed(error)
                    }
                }
            }
        )
    }

    private fun onNativeSmallAdLoaded(
        callback: NativeAdCallback?,
        activity: Activity,
        nativeAd: NativeAd,
        adsContainer: FrameLayout
    ) {
        Log.d(TAG, "Native small ad loaded")
        val handler = Handler(Looper.getMainLooper())
        val nativeAdModel = NativeAdModel(NetworkType.ADMOB).apply {
            adObject = nativeAd
        }

        nativeAd.adEventCallback = object : NativeAdEventCallback {
            override fun onAdClicked() {
                handler.post {
                    callback?.onAdClicked()
                }
            }

            override fun onAdImpression() {
                handler.post {
                    callback?.onAdImpression()
                }
            }

            override fun onAdDismissedFullScreenContent() {
                handler.post {
                    callback?.onAdClosed()
                }
            }
        }

        handler.post {
            callback?.onAdLoaded(nativeAdModel)

            if (activity.isDestroyed) {
                nativeAd.destroy()
                return@post
            }

            val layoutInflater = activity.layoutInflater
            val adView = layoutInflater
                .inflate(R.layout.ads_native_banner, null) as NativeAdView

            populateNativeSmallAdView(nativeAd, adView)

            adsContainer.visibility = View.VISIBLE
            adsContainer.removeAllViews()
            adsContainer.addView(adView)
            callback?.onAdPopulated()
        }
    }
}