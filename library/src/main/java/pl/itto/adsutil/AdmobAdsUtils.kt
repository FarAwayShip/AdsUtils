package pl.itto.adsutil

import android.app.Application
import pl.itto.adsutil.applovin.AppLovinAdsUtils

class AdmobAdsUtils private constructor(application: Application) {
    companion object {
        const val TAG = "AppLovinUtils"
        fun getInstance(application: Application): AdmobAdsUtils = AdmobAdsUtils(application)
    }

}