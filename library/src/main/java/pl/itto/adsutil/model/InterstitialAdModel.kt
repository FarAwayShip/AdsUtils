package pl.itto.adsutil.model

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAdEventCallback
import pl.itto.adsutil.callback.InterstitialAdCallback

class InterstitialAdModel(adNetworkType: NetworkType = NetworkType.UN_DEFINED) : BaseAdModel() {
    init {
        this.adNetworkType = adNetworkType
    }

    fun show(activity: FragmentActivity?, callback: InterstitialAdCallback? = null) {
        Log.d(TAG, "show: $adNetworkType")
        if (activity == null) {
            Log.d(TAG, "InterstitialAd not showed due to activity null: ")
            return
        }
        val handler = Handler(Looper.getMainLooper())
        
        if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            when (adNetworkType) {
                NetworkType.ADMOB -> {
                    adObject?.let {
                        val ad = it as InterstitialAd
                        ad.adEventCallback = object : InterstitialAdEventCallback {
                            override fun onAdShowedFullScreenContent() {
                                handler.post {
                                    callback?.onAdDisplayed()
                                }
                            }

                            override fun onAdDismissedFullScreenContent() {
                                handler.post {
                                    callback?.onAdDismissed()
                                }
                            }

                            override fun onAdFailedToShowFullScreenContent(error: FullScreenContentError) {
                                handler.post {
                                    callback?.onAdDisplayFailed()
                                }
                            }

                            override fun onAdClicked() {
                                handler.post {
                                    callback?.onAdClicked()
                                }
                            }

                            override fun onAdImpression() {
                                handler.post {
                                    callback?.onAdImpression()
                                }
                            }
                        }
                        Log.i(TAG, "showing ad")
                        ad.show(activity)
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