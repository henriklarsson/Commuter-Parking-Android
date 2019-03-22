package se.larsson.parking.network.oauth

import android.util.Log

class AccessToken(val scope: String,
                       val token_type: String,
                       val expires_in: Int,
                       val refresh_token: String,
                       val access_token: String) {

    var timestamp: Long = 4
    init {
        timestamp = System.currentTimeMillis()
        Log.d("Henrik", "t: $timestamp")
    }


}
