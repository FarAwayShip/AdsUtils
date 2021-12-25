package pl.itto.adsutilexample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import pl.itto.adsutil.AdsUtil

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    private fun loadAds() {
//            AdsUtil.instance.loadNativeAdsSmall(
//                this,
//                findViewById(R.id.ads_container),
//                layoutInflater,
//
//                object : AdsUtil.NativeAdsCallback {
//                    override fun adLoaded(nativeAd: NativeAd) {
//                        mBinding.adsContainer.isVisible = true
//                    }
//
//                    override fun adLoadFailed() {
//                        mBinding.adsContainer.isVisible = false
//                    }
//                }
//            )
//        } else {
//            mBinding.adsContainer.isVisible = false
    }

    fun openBanner(view: View) {
//        val intent = Intent(this, BannerActivity::class.java)
//        startActivity(intent)
    }

    fun openNative(view: View) {
//        val intent = Intent(this, NativeActivity::class.java)
//        startActivity(intent)
    }

    fun interstitial(view: View) {
//        val intent = Intent(this, InterstitialActivity::class.java)
//        startActivity(intent)
    }
}