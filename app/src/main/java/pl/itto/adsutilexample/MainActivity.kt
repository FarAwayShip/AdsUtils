package pl.itto.adsutilexample

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import pl.itto.adsutil.AdLoadCallback
import pl.itto.adsutil.AdsManager
import pl.itto.adsutil.applovin.AppLovinUtils
import pl.itto.adsutilexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var mBinding: ActivityMainBinding
    var mInterstitialAd: InterstitialAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(mBinding.root)
    }

    fun loadNativeBanner(view: View) {
        mBinding.bannerAds.load(object: AdLoadCallback<NativeAd> {
            override fun onAdLoaded(ads: NativeAd) {
                Log.d(TAG, "load banner ads successfully")
            }

            override fun onAdFailedToLoad() {
                Log.d(TAG, "load banner ads failed")
            }

        })
    }

    fun openNative(view: View) {
        mBinding.nativeAds.load(object: AdLoadCallback<NativeAd> {
            override fun onAdLoaded(ads: NativeAd) {
                Log.d(TAG, "load native ads successfully")
            }

            override fun onAdFailedToLoad() {
                Log.d(TAG, "load native ads failed")
            }

        })
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
        loadInterstitialAppLovin()
    }

    fun loadInterstitialAppLovin(){
        AppLovinUtils.getInstance(application).createInterstitialAd(this)
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
}