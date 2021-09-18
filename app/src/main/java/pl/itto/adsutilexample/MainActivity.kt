package pl.itto.adsutilexample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import pl.itto.adsutil.BannerActivity
import pl.itto.adsutil.InterstitialActivity
import pl.itto.adsutil.NativeActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun openBanner(view: View) {
        val intent = Intent(this, BannerActivity::class.java)
        startActivity(intent)
    }

    fun openNative(view: View) {
        val intent = Intent(this, NativeActivity::class.java)
        startActivity(intent)
    }

    fun interstitial(view: View) {
        val intent = Intent(this, InterstitialActivity::class.java)
        startActivity(intent)
    }
}