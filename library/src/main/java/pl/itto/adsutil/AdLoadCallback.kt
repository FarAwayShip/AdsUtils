package pl.itto.adsutil

interface AdLoadCallback<AdT> {

    fun onAdLoaded(ads: AdT)

    fun onAdFailedToLoad()
}