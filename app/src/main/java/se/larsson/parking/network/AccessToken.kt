package se.larsson.parking.network

class AccessToken(val scope: String,
                       val token_type: String,
                       val expires_in: Int,
                       val refresh_token: String,
                       val access_token: String) {
    var timestamp: Long = 0
    fun isValid(): Boolean{
        return System.currentTimeMillis() < timestamp + TTL
    }
    companion object {
        val TTL: Int = 3600
    }
}
