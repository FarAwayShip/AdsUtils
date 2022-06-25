package pl.itto.adsutil.callback

interface OpenAppCallback {
    fun onAdDismissed()
    fun onAdDisplayed()
    fun onAdDisplayFailed()
    fun onAdLoadFailed(msg:String?)
    fun onAdDisabled()
}