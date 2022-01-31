package pl.itto.adsutil.model

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import com.applovin.mediation.ads.MaxInterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAd

class InterstitialAdModel(adNetworkType: NetworkType = NetworkType.UN_DEFINED) : BaseAdModel() {
    init {
        this.adNetworkType = adNetworkType
    }

    fun show(activity: FragmentActivity?) {
        Log.d(TAG, "show: $adNetworkType")
        if (activity == null) {
            Log.d(TAG, "InterstitialAd not showed due to activity null: ")
            return
        }
        if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            when (adNetworkType) {
                NetworkType.APPLOVIN -> {
                    adObject?.let {
                        val ad = (it as MaxInterstitialAd)
                        if (ad.isReady) {
                            Log.i(TAG, "Showing Ad")
                            ad.showAd()
                        } else {
                            Log.e(TAG, "App lovin interstitial ad not showed due to not ready")
                        }
                    }
                }

                NetworkType.ADMOB -> {
                    adObject?.let {
                        Log.i(TAG, "showing ad")
                        (it as InterstitialAd).show(activity)
                    }
                }
                else -> {
                    Log.e(TAG, "Interstitial ad not showed due to wrong network: $adNetworkType")
                }
            }
        }
    }

    companion object {
        private const val TAG = "InterstitialAdModel"
    }
}