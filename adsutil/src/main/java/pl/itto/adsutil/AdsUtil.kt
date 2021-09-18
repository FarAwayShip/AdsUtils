package pl.itto.adsutil

import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.huawei.hms.ads.HwAds

object AdsUtil {
    enum class AdProviderType {
        GOOGLE, HUAWEI
    }

    lateinit var adProviderType: AdProviderType
    fun init(context: Context, adProviderType: AdProviderType) {
        this.adProviderType
        when(adProviderType){
            AdProviderType.GOOGLE-> MobileAds.initialize(context)
            AdProviderType.HUAWEI->HwAds.init(context)
        }
    }
}