package pl.itto.adsutil.callback

import pl.itto.adsutil.model.InterstitialAdModel

interface InterstitialAdCallback {
    fun onAdLoaded(ad: InterstitialAdModel)
    fun onAdLoadFailed(message: String? = "")
    fun onAdClicked()
    fun onAdHidden()
    fun onAdDisplayed()
    fun onAdDisplayFailed()
}