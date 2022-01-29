package pl.itto.adsutil.model

open class BaseAdModel {
    var adNetworkType: NetworkType = NetworkType.UN_DEFINED

    /**
     * Base on adNetworkType, we will return nativeAd object of each network
     */
    var adObject: Any? = null

}