package pl.itto.adsutilexample

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import pl.itto.adsutil.AdsUtil
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
        mBinding.cardBanner.isVisible = true
        mBinding.cardNative.isVisible = false
        AdsUtil.instance.loadNativeAdsSmall(
            this,
            mBinding.cardBanner.findViewById(R.id.ads_container) as FrameLayout,
            layoutInflater,
            getString(R.string.ads_test_native_id),
            object : AdsUtil.NativeAdsCallback {
                override fun adLoaded(nativeAd: NativeAd) {

                }

                override fun adLoadFailed() {
                }

            })
    }

    fun openNative(view: View) {
        mBinding.cardBanner.isVisible = false
        mBinding.cardNative.isVisible = true
        AdsUtil.instance.loadNativeAds(
            this,
            mBinding.cardNative.findViewById(R.id.ads_container) as FrameLayout,
            layoutInflater,
            getString(R.string.ads_test_native_id),
            object : AdsUtil.NativeAdsCallback {
                override fun adLoaded(nativeAd: NativeAd) {
                    Toast.makeText(this@MainActivity, "Ad was loaded.", Toast.LENGTH_SHORT).show()
                }

                override fun adLoadFailed() {
                    Toast.makeText(this@MainActivity, "Ad load failed.", Toast.LENGTH_SHORT).show()
                }

            })
    }

    fun interstitial(view: View) {
        mBinding.cardBanner.isVisible = false
        mBinding.cardNative.isVisible = false
        AdsUtil.instance.loadInterstitial(
            this,
            getString(R.string.ads_test_inter_id),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(p0: InterstitialAd) {
                    super.onAdLoaded(p0)
                    mInterstitialAd = p0
                    mInterstitialAd?.fullScreenContentCallback = mFullScreenAdsCallback
                    if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                        mInterstitialAd?.show(this@MainActivity)
                    }
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    mInterstitialAd = null
                    // Do something after ads load failed
                }
            })
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