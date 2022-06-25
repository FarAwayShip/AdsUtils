package pl.itto.adsutilexample

import android.app.Application
import android.util.Log
import pl.itto.adsutil.AdsManager
import pl.itto.adsutil.model.NetworkType

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("MainApp", "onCreate: ")
        AdsManager.getInstance(this).let {
            it.initAdsSdk(this, true)
            it.loadAdUnitsConfigFromResource(R.raw.ad_units)
            it.networkType = NetworkType.ADMOB
            it.enableShowAds(true)
        }
    }
}