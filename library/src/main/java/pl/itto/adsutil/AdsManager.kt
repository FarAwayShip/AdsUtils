package pl.itto.adsutil

/**
 * Created by PL_itto-PC on 10/27/2021
 **/

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import pl.itto.adsutil.Constants.PREF_NAME
import pl.itto.adsutil.Constants.SHOW_ADS
import com.applovin.sdk.AppLovinSdkConfiguration

import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdk.SdkInitializationListener


class AdsManager private constructor(application: Application) {

    companion object {
        const val TAG = "AdsManager"
        fun getInstance(application: Application): AdsManager = AdsManager(application)
    }

    fun initAdsSdk(context: Context) {
        Log.d(TAG, "initAdsSdk: ")
        MobileAds.initialize(context)
        AppLovinSdk.getInstance(context).mediationProvider = "max"
        AppLovinSdk.initializeSdk(context, SdkInitializationListener {
            // AppLovin SDK is initialized, start loading ads
        })
        AppLovinSdk.getInstance(context).settings.testDeviceAdvertisingIds = arrayListOf("62b93127-cdeb-4f68-9cda-50342f9b3a3f")
    }

    private val pref: SharedPreferences =
        application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun enableShowAds(isShow: Boolean) {
        pref.edit().putBoolean(SHOW_ADS, isShow).apply()
    }

    fun showInterstitial(context: Context, adsId: String, listener: InterstitialAdLoadCallback) {
        val showAds = pref.getBoolean(SHOW_ADS, true)
        if (showAds) {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(context, adsId, adRequest, listener)
        }
    }
}
