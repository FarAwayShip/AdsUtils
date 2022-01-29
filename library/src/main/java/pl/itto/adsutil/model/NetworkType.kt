package pl.itto.adsutil.model

enum class NetworkType(private val id: Int, private val networkName: String) {
    UN_DEFINED(-1, "undefined"), ADMOB(0, "admob"), APPLOVIN(1, "applovin");

    fun getId(): Int {
        return id
    }

    fun getName(): String {
        return networkName
    }

}