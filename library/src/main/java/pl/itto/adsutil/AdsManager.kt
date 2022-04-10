package pl.itto.adsutil

/**
 * Created by PL_itto-PC on 10/27/2021
 **/

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.FrameLayout
import androidx.annotation.RawRes
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import pl.itto.adsutil.Constants.PREF_NAME
import pl.itto.adsutil.Constants.SHOW_ADS

import pl.itto.adsutil.admob.AdmobAdsUtils
import pl.itto.adsutil.callback.InterstitialAdCallback
import pl.itto.adsutil.callback.NativeAdCallback
import pl.itto.adsutil.model.AdUnitConfigMap
import pl.itto.adsutil.model.NativeAdModel
import pl.itto.adsutil.model.NetworkType


class AdsManager private constructor(val application: Application) {

    companion object {
        const val TAG = "AdsManager"
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

    private lateinit var adUnitConfigMap: AdUnitConfigMap
    fun initAdsSdk(context: Context) {
        try {
            Log.d(TAG, "initAdsSdk: ")
            AdmobAdsUtils.getInstance(application).initSdk(context)
        } catch (e: Exception) {
            Log.e(TAG, "initAdsSdk Failed", e)
        }
    }

    /**
     * Load all ad units into a Map
     */
    fun loadAdUnitsConfig(jsonString: String) {
        Log.d(TAG, "loadAdUnitsConfig: ")
        adUnitConfigMap = AdUnitConfigMap.fromJson(jsonString)
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
        get() = pref.getBoolean(SHOW_ADS, true)

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
        if (!isAdEnabled) {
            Log.i(TAG, "Ads disabled, ignore loading ads")
            return
        }
        val adsId = adUnitConfigMap.getAdsId(adUnitName, networkType.getName())
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
        callback: NativeAdCallback? = null
    ) {
        if (!isAdEnabled) {
            Log.i(TAG, "Ads disabled, ignore loading ads")
            return
        }
        Log.d(TAG, "loadNativeAds: $adUnitName")
        val adsId = adUnitConfigMap.getAdsId(adUnitName, networkType.getName())
        when (networkType) {
            NetworkType.ADMOB -> {
                AdmobAdsUtils.getInstance(application)
                    .loadNativeAds(adsId, adsContainer, activity, existAdModel, callback)
            }
            else -> {
                Log.e(TAG, "Not found Network type for $networkType")
            }
        }
    }

    fun showInterstitial(context: Context, adsId: String, listener: InterstitialAdLoadCallback) {
        val showAds = pref.getBoolean(SHOW_ADS, true)
        if (showAds) {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(context, adsId, adRequest, listener)
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
                        Log.e(TAG, "destroyNativeAd Failed by no valid NetworkType: " )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error when destroy NativeAd: ", e)
        }

    }


}
