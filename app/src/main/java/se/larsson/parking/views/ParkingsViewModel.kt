package se.larsson.parking.views

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.location.Location
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import se.larsson.parking.network.oauth.AccessToken
import se.larsson.parking.network.oauth.HttpServices
import se.larsson.parking.network.oauth.models.ParkingLot

class ParkingsViewModel: ViewModel() {
    private val TAG = ParkingsActivity::class.java.simpleName
    var token: AccessToken? = null
    val parkingLots = MutableLiveData<MutableList<ParkingLot>>()
    val responseCode = MutableLiveData<Int>()

    private val searchDistance: Int = 15 // km

    private suspend fun getAccessToken(): AccessToken?{
        if (token?.isValid() == true){
            Log.d(TAG, "returning valid token")
            return token
        } else {
            Log.d(TAG, "fetching new token")
            val oAuthService = HttpServices().getOAuthService()
            val accessToken = oAuthService.getAccessToken()
            val tokenResponse = accessToken.await()
            token = tokenResponse.body()
            Log.d(TAG, tokenResponse.body()?.access_token)
            return token
        }
    }

    fun clearParking(){
        parkingLots.postValue(mutableListOf())
    }

    suspend fun getParkingLots(userLat: Double? = null, userLong: Double? = null, dist: Int? = searchDistance, max: Int? = null){
        val parkingService = HttpServices().getParkingService()

        val accessToken = getAccessToken()
        accessToken?.access_token?.let {
            val parkingAreas =
                parkingService.getParkings(authorization = "Bearer $it",
                    format = "json", lat = userLat, lon = userLong, dist = dist, max = max).await()
            parkingAreas.body()!!.forEach {
                Log.d(TAG, it.toString())
            }
//                Log.d(TAG, "Timestamp: ${await.timestamp}")
            Log.d(TAG, "Timestamp2: ${System.currentTimeMillis()}")
            val reciviedParkingLots = mutableListOf<ParkingLot>()
            parkingAreas.body()?.forEach { reciviedParkingLots.addAll(it.ParkingLots)  }
            // sort list by distance
            reciviedParkingLots.sortBy { calculateDistance(it, userLat!!, userLong!!) }
            parkingLots.postValue(reciviedParkingLots)
            responseCode.postValue(parkingAreas.code())
        }
    }

    private fun calculateDistance(parkingLot: ParkingLot, userLat: Double, userLong: Double): Float {
        val userLocation = Location("")
        userLocation.latitude = userLat
        userLocation.longitude = userLong
        val parkingLotLocation = Location("")
        parkingLotLocation.latitude = parkingLot.Lat
        parkingLotLocation.longitude = parkingLot.Lon
        return userLocation.distanceTo(parkingLotLocation)
    }
}