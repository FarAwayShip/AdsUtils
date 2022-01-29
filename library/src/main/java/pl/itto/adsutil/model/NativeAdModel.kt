package pl.itto.adsutil.model

class NativeAdModel(adNetworkType: NetworkType = NetworkType.UN_DEFINED) : BaseAdModel() {
    init {
        this.adNetworkType = adNetworkType
    }
}