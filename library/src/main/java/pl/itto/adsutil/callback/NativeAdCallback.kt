package pl.itto.adsutil.callback

import pl.itto.adsutil.model.NativeAdModel

interface NativeAdCallback {
    fun onAdLoaded(adModel: NativeAdModel)
    fun onAdClicked()
    fun onAdLoadFailed(message: String? = null)
    fun onAdImpression()
    fun onAdClosed()
    fun onAdDisabled()
}