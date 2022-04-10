package pl.itto.adsutil.model

//import com.applovin.mediation.nativeAds.MaxNativeAdLoader

class NativeAdModel(adNetworkType: NetworkType = NetworkType.UN_DEFINED) : BaseAdModel() {
    init {
        this.adNetworkType = adNetworkType
    }
//
//    /**
//     * For app login, we need preserve loader
//     */
//    var applovinAdLoader: MaxNativeAdLoader? = null
}