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
    var token: String? = null
    val parkingLots = MutableLiveData<MutableList<ParkingLot>>()
    private val searchDistance: Int = 15 // km

    fun getParkingLots(userLat: Double? = null, userLong: Double? = null, dist: Int? = searchDistance, max: Int? = null){
        GlobalScope.launch {// activity should create scope
            val oAuthService = HttpServices().getOAuthService()
            val accessToken = oAuthService.getAccessToken()
            val parkingService = HttpServices().getParkingService()
            val tokenResponse = accessToken.await()
            token = tokenResponse.body()?.access_token
            Log.d(TAG, tokenResponse.body()?.access_token)
            val parkings =
                parkingService.getParkings(authorization = "Bearer ${tokenResponse?.body()?.access_token!!}",
                    format = "json", lat = userLat, lon = userLong, dist = dist, max = max)
            val await = parkings.await()
            await.body()!!.forEach {
                Log.d(TAG, it.toString())
            }
//                Log.d(TAG, "Timestamp: ${await.timestamp}")
            Log.d(TAG, "Timestamp2: ${System.currentTimeMillis()}")
            val reciviedParkingLots = mutableListOf<ParkingLot>()
            val body = await.body()?.forEach { reciviedParkingLots.addAll(it.ParkingLots)  }
            // sort list by distance
            reciviedParkingLots.sortBy { calculateDistance(it, userLat!!, userLong!!) }
            parkingLots.postValue(reciviedParkingLots)

        }
    }

    fun calculateDistance(parkingLot: ParkingLot, userLat: Double, userLong: Double): Float {
        val userLocation = Location("")
        userLocation.latitude = userLat
        userLocation.longitude = userLong
        val parkingLotLocation = Location("")
        parkingLotLocation.latitude = parkingLot.Lat
        parkingLotLocation.longitude = parkingLot.Lon
        return userLocation.distanceTo(parkingLotLocation)

    }
}