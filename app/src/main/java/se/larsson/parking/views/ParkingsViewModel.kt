package se.larsson.parking.views

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.location.Location
import android.util.Log
import se.larsson.parking.network.AccessToken
import se.larsson.parking.network.HttpServices
import se.larsson.parking.network.models.ParkingLot
import se.larsson.parking.network.models.ResponseResult

class ParkingsViewModel: ViewModel() {
    private val TAG = ParkingsActivity::class.java.simpleName
    var token: AccessToken? = null
    val parkingLots = MutableLiveData<MutableList<ParkingLot>>()
    val responseCode = MutableLiveData<ResponseResult>()

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
            val parkingsResponse =
                parkingService.getParkings(authorization = "Bearer $it",
                    format = "json", lat = userLat, lon = userLong, dist = dist, max = max).await()
            parkingsResponse.body()!!.forEach {
                Log.d(TAG, it.toString())
            }
            if (parkingsResponse.isSuccessful){
                Log.d(TAG, "parking response success ${parkingsResponse.code()}")
                val reciviedParkingLots = mutableListOf<ParkingLot>()
                parkingsResponse.body()?.forEach { reciviedParkingLots.addAll(it.ParkingLots)  }
                // sort list by distance
                reciviedParkingLots.sortBy { calculateDistance(it, userLat!!, userLong!!) }
                parkingLots.postValue(reciviedParkingLots)
                responseCode.postValue(ResponseResult.Success(parkingsResponse.code()))
            } else {
                Log.d(TAG, "parking response failed ${parkingsResponse.code()}")
                clearParking()
                responseCode.postValue(ResponseResult.Error(parkingsResponse.code(), parkingsResponse.message()))
            }

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