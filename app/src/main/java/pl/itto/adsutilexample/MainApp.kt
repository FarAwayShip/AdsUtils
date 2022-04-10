package pl.itto.adsutilexample

import android.app.Application
import pl.itto.adsutil.AdsManager
import pl.itto.adsutil.model.NetworkType

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AdsManager.getInstance(this).let {
            it.initAdsSdk(this)
            it.loadAdUnitsConfigFromResource(R.raw.ad_units)
            it.networkType = NetworkType.ADMOB
        }
    }
}