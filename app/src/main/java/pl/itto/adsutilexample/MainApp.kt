package pl.itto.adsutilexample

import android.app.Application
import pl.itto.adsutil.AdsManager

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AdsManager.getInstance(this).initAdsSdk(this)
    }
}