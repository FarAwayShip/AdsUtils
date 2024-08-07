package pl.itto.adsutil

/**
 * Created by PL_itto-PC on 10/27/2021
 **/

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import pl.itto.adsutil.Constants.PREF_NAME
import pl.itto.adsutil.Constants.SHOW_ADS
import pl.itto.adsutil.admob.AdmobAdsUtils
import pl.itto.adsutil.callback.InterstitialAdCallback
import pl.itto.adsutil.callback.NativeAdCallback
import pl.itto.adsutil.callback.OpenAppCallback
import pl.itto.adsutil.model.AdUnitConfigMap
import pl.itto.adsutil.model.InterstitialAdModel
import pl.itto.adsutil.model.NativeAdModel
import pl.itto.adsutil.model.NetworkType


class AdsManager private constructor(val application: Application) {

    companion object {
        const val TAG = "AdsManager"
        private var _adsSdkInitialized = false
        val isAdsSdkInitialized: Boolean
            get() = _adsSdkInitialized

        private var _instance: AdsManager? = null
        fun getInstance(application: Application): AdsManager {
            if (_instance == null) {
                _instance = AdsManager(application)
            }
            return _instance!!
        }
    }

    var networkType = NetworkType.UN_DEFINED
    val isNetworkDefined: Boolean = networkType != NetworkType.UN_DEFINED

    private var adUnitConfigMap: AdUnitConfigMap? = null
    fun initAdsSdk(context: Context, initAppOpen: Boolean = false) {
        try {
            Log.d(TAG, "initAdsSdk: ")
            AdmobAdsUtils.getInstance(application).initSdk(context)
            if (initAppOpen) {
                AdmobAdsUtils.getInstance(application).initAppOpen(application)
            }
            _adsSdkInitialized = true
        } catch (e: Exception) {
            Log.e(TAG, "initAdsSdk Failed", e)
        }
    }

    /**
     * Load all ad units into a Map
     */
    fun loadAdUnitsConfig(jsonString: String) {
        Log.d(TAG, "loadAdUnitsConfig from json: ")
        adUnitConfigMap = AdUnitConfigMap.fromJson(jsonString)
    }

    fun loadAdUnitsConfig(configMap: Map<String, HashMap<String, String>>) {
        Log.d(TAG, "loadAdUnitsConfig from Map: ")
        adUnitConfigMap = AdUnitConfigMap.fromMap(configMap)
    }

    fun loadAdUnitsConfigFromResource(@RawRes rawResId: Int, context: Context = application) {
        Log.d(TAG, "loadAdUnitsConfigFromResource: ")
        val inputStream = context.resources.openRawResource(rawResId)
        val text = inputStream.bufferedReader().use {
            it.readText()
        }

        // Load config text done, parse to map
        loadAdUnitsConfig(jsonString = text)
    }

    private val pref: SharedPreferences =
        application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun enableShowAds(isShow: Boolean) {
        pref.edit().putBoolean(SHOW_ADS, isShow).apply()
    }

    val isAdEnabled: Boolean
        get() = pref.getBoolean(SHOW_ADS, false)

    fun loadOpenApAds(
        adUnitName: String,
        activity: AppCompatActivity,
        showNow: Boolean = true,
        callback: OpenAppCallback? = null
    ) {
        Log.d(TAG, "loadInterstitialAds: $adUnitName --showNow: $showNow")
        if (!isAdsSdkInitialized) {
            Log.e(TAG, "Ads Sdk not initialized, ignore load ads")
            callback?.onAdLoadFailed("Not init sdk")
            return
        }
        if (adUnitConfigMap == null) {
            val msg = "Ads Config map not initialized, call failed"
            Log.e(TAG, msg)
            callback?.onAdLoadFailed(msg)
            return
        }
        if (!isAdEnabled) {
            Log.i(TAG, "Ads disabled, ignore loading ads")
            callback?.onAdDisabled()
            return
        }

        val adsId = adUnitConfigMap!!.getAdsId(adUnitName, networkType.getName())
        if (adsId.isEmpty()) {
            callback?.onAdLoadFailed("Ad unit id empty")
            return
        }

        when (networkType) {
            NetworkType.ADMOB -> {
                AdmobAdsUtils.getInstance(application)
                    .loadAppOpenAds(adsId, activity, callback, showNow)
            }

            else -> {
                Log.e(TAG, "Not found Network type for $networkType")
            }
        }


    }

    /**
     * Show open app ads
     */
    fun showOpenApp(adUnitName: String, activity: AppCompatActivity, callback: OpenAppCallback) {
        Log.d(TAG, "showOpenApp: $adUnitConfigMap")
        Log.d(TAG, "ad unit name: $adUnitName")
        if (!isAdsSdkInitialized) {
            Log.e(TAG, "Ads Sdk not initialized, ignore load ads")
            callback?.onAdLoadFailed("Not init sdk")
            return
        }
        if (adUnitConfigMap == null) {
            val msg = "Ads Config map not initialized, call failed"
            Log.e(TAG, msg)
            callback.onAdLoadFailed("adUnitConfigMap not initialized")
            return
        }

        if (!isAdEnabled) {
            Log.i(TAG, "Ads disabled, ignore loading ads")
            callback.onAdDisabled()
            return
        }

        try {
            val adsId = adUnitConfigMap!!.getAdsId(adUnitName, networkType.getName())
            if (adsId.isEmpty()) {
                callback.onAdLoadFailed("Ad unit id empty")
                return
            }
            when (networkType) {
                NetworkType.ADMOB -> {
                    AdmobAdsUtils.getInstance(application)
                        .showAppOpenAds(adsId, activity, callback)
                }

                else -> {
                    Log.e(TAG, "Not found Network type for $networkType")
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "initAdsSdk Failed", e)
        }
    }


    fun loadInterstitialAds(
        adUnitName: String,
        activity: FragmentActivity,
        showNow: Boolean = true,
        callback: InterstitialAdCallback? = null
    ) {
        /**
         * Load interstitial ad
         */
        Log.d(TAG, "loadInterstitialAds: $adUnitName --showNow: $showNow")
        if (!isAdsSdkInitialized) {
            Log.e(TAG, "Ads Sdk not initialized, ignore load ads")
            callback?.onAdLoadFailed("Not init sdk")
            return
        }
        if (adUnitConfigMap == null) {
            val msg = "Ads Config map not initialized, call failed"
            Log.e(TAG, msg)
            callback?.onAdLoadFailed(msg)
            return
        }
        if (!isAdEnabled) {
            Log.i(TAG, "Ads disabled, ignore loading ads")
            callback?.onAdDisabled()
            return
        }
        val adsId = adUnitConfigMap!!.getAdsId(adUnitName, networkType.getName())
        if (adsId.isEmpty()) {
            callback?.onAdLoadFailed("Ad unit id empty")
            return
        }

        when (networkType) {
            NetworkType.ADMOB -> {
                AdmobAdsUtils.getInstance(application)
                    .loadInterstitialAds(adsId, activity, showNow, callback)
            }

            else -> {
                Log.e(TAG, "Not found Network type for $networkType")
            }
        }
    }

    /**
     * Load Native ads,
     * We will pass a adUnitName, from here adsId will be parse by AdUnitName + AdNetworkType
     */
    fun loadNativeAds(
        adUnitName: String,
        adsContainer: FrameLayout,
        activity: Activity,
        existAdModel: NativeAdModel? = null,
        callback: NativeAdCallback? = null,
        adUnitAltName: String? = null
    ) {
        if (!isAdsSdkInitialized) {
            Log.e(TAG, "Ads Sdk not initialized, ignore load ads")
            callback?.onAdLoadFailed("Not init sdk")
            return
        }
        if (adUnitConfigMap == null) {
            val msg = "Ads Config map not initialized, call failed"
            Log.e(TAG, msg)
            callback?.onAdLoadFailed(msg)
            return
        }
        if (!isAdEnabled) {
            Log.i(TAG, "Ads disabled, ignore loading ads")
            callback?.onAdDisabled()
            return
        }
        Log.d(TAG, "loadNativeAds: $adUnitName")
        val adsId = adUnitConfigMap!!.getAdsId(adUnitName, networkType.getName())
        if (adsId.isEmpty()) {
            callback?.onAdLoadFailed("Ad unit id empty")
            return
        }
        val adsIdAlt = adUnitAltName?.let { adUnitConfigMap!!.getAdsId(it, networkType.getName()) }
        when (networkType) {
            NetworkType.ADMOB -> {
                AdmobAdsUtils.getInstance(application)
                    .loadNativeAds(adsId, adsContainer, activity, existAdModel, callback, adsIdAlt)
            }

            else -> {
                Log.e(TAG, "Not found Network type for $networkType")
            }
        }
    }


    /**
     * Load Native Banner ads,
     * We will pass a adUnitName, from here adsId will be parse by AdUnitName + AdNetworkType
     */
    fun loadNativeSmallAds(
        adUnitName: String,
        adsContainer: FrameLayout,
        activity: Activity,
        existAdModel: NativeAdModel? = null,
        callback: NativeAdCallback? = null,
        adUnitAltName: String? = null
    ) {
        if (!isAdsSdkInitialized) {
            Log.e(TAG, "Ads Sdk not initialized, ignore load ads")
            callback?.onAdLoadFailed("Not init sdk")
            return
        }
        if (adUnitConfigMap == null) {
            val msg = "Ads Config map not initialized, call failed"
            Log.e(TAG, msg)
            callback?.onAdLoadFailed(msg)
            return
        }
        if (!isAdEnabled) {
            Log.i(TAG, "Ads disabled, ignore loading ads")
            callback?.onAdDisabled()
            return
        }
        Log.d(TAG, "loadNativeSmallAds: $adUnitName")
        val adsId = adUnitConfigMap!!.getAdsId(adUnitName, networkType.getName())
        if (adsId.isEmpty()) {
            callback?.onAdLoadFailed("Ad unit id empty")
            return
        }
        val adsIdAlt = adUnitAltName?.let { adUnitConfigMap!!.getAdsId(it, networkType.getName()) }
        when (networkType) {
            NetworkType.ADMOB -> {
                AdmobAdsUtils.getInstance(application)
                    .loadNativeSmallAds(
                        adsId,
                        adsContainer,
                        activity,
                        existAdModel,
                        callback,
                        adsIdAlt
                    )
            }

            else -> {
                Log.e(TAG, "Not found Network type for $networkType")
            }
        }
    }

    /**
     * Show the loaded interstitial ads
     */
    fun showInterstitial(
        activity: FragmentActivity,
        interstitialAdModel: InterstitialAdModel,
        callback: InterstitialAdCallback? = null
    ) {
        if (!isAdsSdkInitialized) {
            Log.e(TAG, "Ads Sdk not initialized, ignore load ads")
            callback?.onAdLoadFailed("Not init sdk")
            return
        }
        if (!isAdEnabled) {
            Log.i(TAG, "Ads disabled, ignore loading ads")
            callback?.onAdDisabled()
            return
        }
        interstitialAdModel.adObject?.let {
            interstitialAdModel.show(activity, callback)
        }
    }

    /**
     * Show the loaded native ad
     */
    fun showNativeAd(
        activity: FragmentActivity,
        nativeAdsModel: NativeAdModel,
        adContainer: FrameLayout?
    ): View? {
        Log.d(TAG, "showNativeAd: ")
        when (networkType) {
            NetworkType.ADMOB -> {
                val adView = activity.layoutInflater
                    .inflate(R.layout.ads_native_home, null) as NativeAdView
                nativeAdsModel.adObject?.let {
                    AdmobAdsUtils.populateNativeAdView((it as NativeAd), adView)
                }
                adContainer?.let {
                    it.removeAllViews()
                    it.addView(adView)
                    it.isVisible = true
                }
                return adView

            }

            else -> {
                Log.e(TAG, "Not found Network type for $networkType")
                return null
            }
        }
    }

    /**
     * Show the loaded native ad
     */
    fun showNativeSmallAd(
        activity: FragmentActivity,
        nativeAdsModel: NativeAdModel,
        adContainer: FrameLayout?
    ): View? {
        Log.d(TAG, "showNativeAd: ")
        when (networkType) {
            NetworkType.ADMOB -> {
                val adView = activity.layoutInflater
                    .inflate(R.layout.ads_native_banner, null) as NativeAdView
                nativeAdsModel.adObject?.let {
                    AdmobAdsUtils.populateNativeSmallAdView((it as NativeAd), adView)
                }
                adContainer?.let {
                    it.removeAllViews()
                    it.addView(adView)
                    it.isVisible = true
                }
                return adView

            }

            else -> {
                Log.e(TAG, "Not found Network type for $networkType")
                return null
            }
        }
    }

    fun destroyNativeAd(nativeAdsModel: NativeAdModel? = null) {
        Log.d(TAG, "destroyNativeAd: $nativeAdsModel")
        try {
            nativeAdsModel?.let { it ->
                when (networkType) {
//                    NetworkType.APPLOVIN -> {
//                        it.applovinAdLoader?.let { loader ->
//                            if (it.adObject != null) {
//                                loader.destroy(it.adObject as MaxAd)
//                            }
//                        }
//                    }
                    NetworkType.ADMOB -> {
                        it.adObject?.let { adObject ->
                            (adObject as NativeAd).destroy()
                        }
                    }

                    else -> {
                        Log.e(TAG, "destroyNativeAd Failed by no valid NetworkType: ")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error when destroy NativeAd: ", e)
        }

    }
}
