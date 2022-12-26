package pl.itto.adsutilexample

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import pl.itto.adsutil.AdsManager
import pl.itto.adsutil.callback.InterstitialAdCallback
import pl.itto.adsutil.callback.NativeAdCallback
import pl.itto.adsutil.callback.OpenAppCallback
import pl.itto.adsutil.model.AdUnitConfigMap
import pl.itto.adsutil.model.InterstitialAdModel
import pl.itto.adsutil.model.NativeAdModel
import pl.itto.adsutilexample.databinding.ActivityMainBinding
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var mBinding: ActivityMainBinding
    var mInterstitialAd: InterstitialAd? = null

    var nativeAdMediumModel: NativeAdModel? = null
    var nativeAdBannerModel: NativeAdModel? = null
    var interstitialAdModel: InterstitialAdModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(mBinding.root)


//        val x = "{\n" +
//                "    \"inter_splash\": {\n" +
//                "        \"admob\": \"asdagsdasd\",\n" +
//                "        \"applovin\": \"asdasdasd\"\n" +
//                "    },\n" +
//                "    \"native_home\": {\n" +
//                "        \"admob\": \"asdagsdasd\",\n" +
//                "        \"applovin\": \"asdasdasd\"\n" +
//                "    },\n" +
//                "    \"open_app\": {\n" +
//                "        \"admob\": \"ca-app-pub-3940256099942544/3419835294\",\n" +
//                "        \"applovin\": \"asdasdasd\"\n" +
//                "    }\n" +
//                "}"
//        AdUnitConfigMap.fromJson(x)

    }


    fun loadNativeBanner(view: View) {
//        mBinding.bannerAds.load(object : AdLoadCallback<NativeAd> {
//            override fun onAdLoaded(ads: NativeAd) {
//                Log.d(TAG, "load banner ads successfully")
//            }
//
//            override fun onAdFailedToLoad() {
//                Log.d(TAG, "load banner ads failed")
//            }
//
//        })
        AdsManager.getInstance(application)
            .loadNativeSmallAds(
                "native_home",
                mBinding.adsNativeBannerContainer,
                this,
                nativeAdBannerModel,
                object : NativeAdCallback {
                    override fun onAdLoaded(adModel: NativeAdModel) {
                        nativeAdBannerModel = adModel
                    }

                    override fun onAdClicked() {
                    }

                    override fun onAdLoadFailed(message: String?) {
                        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                    }

                    override fun onAdImpression() {
                    }

                    override fun onAdClosed() {
                    }

                    override fun onAdDisabled() {
                    }
                })
    }

    fun openNative(view: View) {
//        mBinding.nativeAds.load(object : AdLoadCallback<NativeAd> {
//            override fun onAdLoaded(ads: NativeAd) {
//                Log.d(TAG, "load native ads successfully")
//            }
//
//            override fun onAdFailedToLoad() {
//                Log.d(TAG, "load native ads failed")
//            }
//
//        })
        AdsManager.getInstance(application)
            .loadNativeAds(
                "native_setting",
                mBinding.adsContainer,
                this,
                nativeAdMediumModel,
                object : NativeAdCallback {
                    override fun onAdLoaded(adModel: NativeAdModel) {
                        nativeAdMediumModel = adModel
                    }

                    override fun onAdClicked() {
                    }

                    override fun onAdLoadFailed(message: String?) {
                        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                    }

                    override fun onAdImpression() {
                    }

                    override fun onAdClosed() {
                    }

                    override fun onAdDisabled() {
                    }

                }, "native_home_alt"
            )
    }

    fun interstitial(view: View) {
//        AdsManager.getInstance(application).showInterstitial(
//            this,
//            getString(R.string.ads_test_inter_id),
//            object : InterstitialAdLoadCallback() {
//                override fun onAdLoaded(p0: InterstitialAd) {
//
//                    Log.d(TAG, "Load interstitial successfully")
//
//                    super.onAdLoaded(p0)
//                    mInterstitialAd = p0
//                    mInterstitialAd?.fullScreenContentCallback = mFullScreenAdsCallback
//                    if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
//                        mInterstitialAd?.show(this@MainActivity)
//                    }
//                }
//
//                override fun onAdFailedToLoad(p0: LoadAdError) {
//                    super.onAdFailedToLoad(p0)
//
//                    Log.d(TAG, "Load interstitial failed")
//
//                    mInterstitialAd = null
//                    // Do something after ads load failed
//                }
//            })
//        loadInterstitialAppLovin()
        if (interstitialAdModel != null) {
            interstitialAdModel?.show(this)
        } else {
            AdsManager.getInstance(application)
                .loadInterstitialAds("inter_splash", this, true, object : InterstitialAdCallback {
                    override fun onAdLoaded(ad: InterstitialAdModel) {
                        Log.d(TAG, "onAdLoaded: ")
                        interstitialAdModel = ad
                    }

                    override fun onAdLoadFailed(message: String?) {
                        Log.d(TAG, "onAdLoadFailed: ")

                    }

                    override fun onAdClicked() {
                        Log.d(TAG, "onAdClicked: ")
                    }

                    override fun onAdDismissed() {
                        Log.d(TAG, "onAdHidden: ")
                    }

                    override fun onAdDisplayed() {
                        Log.d(TAG, "onAdDisplayed: ")
                    }

                    override fun onAdImpression() {
                        Log.d(TAG, "onAdImpression: ")
                    }

                    override fun onAdDisplayFailed() {
                        Log.d(TAG, "onAdDisplayFailed: ")
                    }

                    override fun onAdDisabled() {
                    }

                })
        }
    }

    fun loadInterstitialAppLovin() {
        AdsManager.getInstance(application)
            .loadInterstitialAds("inter_splash", this, false)
    }

    val mFullScreenAdsCallback = object : FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {
            Log.d(TAG, "Ad was dismissed.")
            Toast.makeText(this@MainActivity, "Ad was dismissed.", Toast.LENGTH_SHORT).show()
            // Don't forget to set the ad reference to null so you
            // don't show the ad a second time.
            mInterstitialAd = null
        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
            Log.d(TAG, "Ad failed to show.")
            Toast.makeText(this@MainActivity, "Ad failed to show", Toast.LENGTH_SHORT).show()
            // Don't forget to set the ad reference to null so you
            // don't show the ad a second time.
            mInterstitialAd = null
        }

        override fun onAdShowedFullScreenContent() {
            Log.d(TAG, "Ad showed fullscreen content.")
            Toast.makeText(this@MainActivity, "Ad showed fullscreen content.", Toast.LENGTH_SHORT)
                .show()
            // Called when ad is dismissed.
//            goToMainScreen()
//            mViewModel.onAdsShow()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AdsManager.getInstance(application).destroyNativeAd(nativeAdBannerModel)
        AdsManager.getInstance(application).destroyNativeAd(nativeAdMediumModel)
    }

    fun appOpen(view: View) {
        Log.d(TAG, "test: ")
        AdsManager.getInstance(application).showOpenApp("open_app", this, object : OpenAppCallback {
            override fun onAdDismissed() {
                Log.d(TAG, "onAdDismissed: ")
            }

            override fun onAdDisplayed() {
                Log.d(TAG, "onAdDisplayed: ")
            }

            override fun onAdDisplayFailed() {
                Log.d(TAG, "onAdDisplayFailed: ")
            }

            override fun onAdLoadFailed(msg: String?) {
                Log.d(TAG, "onAdLoadFailed: $msg")
            }

            override fun onAdDisabled() {
                Log.d(TAG, "onAdDisabled: ")
            }

        })
    }
}