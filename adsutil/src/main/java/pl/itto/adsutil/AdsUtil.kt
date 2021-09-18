package pl.itto.adsutil

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.gms.ads.MobileAds
import com.huawei.hms.ads.AdListener
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.BannerAdSize
import com.huawei.hms.ads.HwAds
import com.huawei.hms.ads.banner.BannerView
import com.huawei.hms.ads.nativead.NativeAdLoader

object AdsUtil {
    enum class AdProviderType {
        GOOGLE, HUAWEI
    }

    lateinit var adProviderType: AdProviderType

    fun init(context: Context, adProviderType: AdProviderType) {
        this.adProviderType
        when (adProviderType) {
            AdProviderType.GOOGLE -> MobileAds.initialize(context)
            AdProviderType.HUAWEI -> HwAds.init(context)
        }
    }
}

object HwdAdsUtil {
    fun loadBannerAds(
        container: FrameLayout,
        adsId: String = "testw6vs28auh3",
        listener: AdListener? = null
    ) {
        // "testw6vs28auh3" is a dedicated test ad unit ID. Before releasing your app, replace the test ad unit ID with the formal one.
        val bannerView: BannerView? = BannerView(container.context)
        bannerView!!.adId = adsId
        bannerView.bannerAdSize = BannerAdSize.BANNER_SIZE_SMART
        container.removeAllViews()
        val adParam = AdParam.Builder().build()
        if (listener != null) {
            bannerView.adListener = listener
        }
        bannerView.loadAd(adParam)
        container.addView(bannerView)
    }

    fun loadNativeAds(
        container: FrameLayout,
        adsId: String = "testy63txaom86",
        listener: AdListener? = null
    ) {
        val builder = NativeAdLoader.Builder(container.context, "adsId")
        builder.setNativeAdLoadedListener { nativeAd ->
            // Called when an ad is loaded successfully.
        }.setAdListener(listener)
        val nativeAdLoader = builder.build()
        //TODO: not done
    }


}