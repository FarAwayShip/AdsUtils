package pl.itto.adsutil

/**
 * Created by PL_itto-PC on 10/27/2021
 **/

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.annotation.RawRes
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import pl.itto.adsutil.Constants.PREF_NAME
import pl.itto.adsutil.Constants.SHOW_ADS

import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdk.SdkInitializationListener
import pl.itto.adsutil.applovin.AppLovinAdsUtils
import pl.itto.adsutil.model.AdUnitConfigMap
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
            MobileAds.initialize(context)
            AppLovinSdk.getInstance(context).mediationProvider = "max"
            AppLovinSdk.initializeSdk(context, SdkInitializationListener {
                // AppLovin SDK is initialized, start loading ads
            })
            AppLovinSdk.getInstance(context).settings.testDeviceAdvertisingIds =
                arrayListOf("62b93127-cdeb-4f68-9cda-50342f9b3a3f")
        } catch (e: Exception) {
            Log.e(TAG, "initAdsSdk Failed", e)
        }
    }

    /**
     * Load all ad units into a Map
     */
    private fun loadAdUnitsConfig(jsonString: String) {
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

    fun loadInterstitialAds(adUnitName: String, activity: Activity) {
        Log.d(TAG, "loadInterstitialAds: $adUnitName")
        val adsId = adUnitConfigMap.getAdsId(adUnitName, networkType.getName())
        when (networkType) {
            NetworkType.APPLOVIN -> {
                AppLovinAdsUtils.getInstance(application).loadInterstitialAd(adsId, activity)
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


}
