package pl.itto.adsutil.model

import android.util.Log
import com.google.gson.Gson
import java.lang.Exception

/***
 * Contain ad units of all network type
 * Format as below:
{
"ad_unit_name":{
"admob":"sadsaldasd",
"applovin":"asiduasdasd"
},
...
}
 */
class AdUnitConfigMap : HashMap<String, HashMap<String, String>>() {
    /**
     * return ad unit Id for AdUnitName + NetworkName
     * return empty string for not exists
     */
    fun getAdsId(adUnitName: String, networkName: String): String {
        if (containsKey(adUnitName)) {
            val adUnitNameMap = get(adUnitName)
            adUnitNameMap?.apply {
                if (containsKey(networkName)) {
                    return get(networkName) ?: ""
                }
            }
        }
        return ""
    }

    companion object {
        private const val TAG = "AdUnitConfigMap"
        fun fromJson(jsonString: String): AdUnitConfigMap {
            val result = AdUnitConfigMap()
            try {
                return Gson().fromJson(jsonString, AdUnitConfigMap::class.java)
            } catch (e: Exception) {
                Log.e(TAG, "Create AdUnitConfigMap from json", e)
            }

            return result
        }
    }
}