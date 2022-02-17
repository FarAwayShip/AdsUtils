package pl.itto.adsutil.admob

import android.app.Application
import android.content.Context
import com.google.android.gms.ads.MobileAds
import pl.itto.adsutil.BaseAdsUtils


class AdmobAdsUtils private constructor(application: Application) :BaseAdsUtils{
    companion object {
        const val TAG = "AdmobAdsUtils"
        private var _instance: AdmobAdsUtils? = null
        fun getInstance(application: Application): AdmobAdsUtils {
            if (_instance == null) {
                _instance = AdmobAdsUtils(application)
            }
            return _instance!!
        }
    }

    override fun initSdk(context: Context) {
        MobileAds.initialize(context)
    }

}