package pl.itto.adsutil.unity

import android.app.Application
import android.content.Context
import pl.itto.adsutil.BaseAdsUtils

class UnityAdsUtils private constructor(application: Application) : BaseAdsUtils{
    companion object {
        const val TAG = "UnityUtils"
        private var _instance: UnityAdsUtils? = null
        fun getInstance(application: Application): UnityAdsUtils {
            if (_instance == null) {
                _instance = UnityAdsUtils(application)
            }
            return _instance!!
        }
    }

    override fun initSdk(context: Context) {
        
    }


}