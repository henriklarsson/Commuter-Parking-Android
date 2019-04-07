package se.larsson.parking.views

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.location.Location
import android.util.Log
import se.larsson.parking.network.AccessToken
import se.larsson.parking.network.HttpServices
import se.larsson.parking.network.models.ParkingLot
import se.larsson.parking.network.models.ResponseResult

class ParkingSpacesViewModel: ViewModel() {
    private val TAG = ParkingSpacesActivity::class.java.simpleName
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
            return if (tokenResponse.isSuccessful){
                token = tokenResponse.body()
                token
            } else {
                responseCode.postValue(ResponseResult.Error(tokenResponse.code(), tokenResponse.message()))
                null
            }
        }
    }

    fun clearParking(){
        parkingLots.postValue(mutableListOf())
    }

    suspend fun getParkingLots(userLat: Double? = null, userLong: Double? = null, dist: Int? = searchDistance, max: Int? = null){
        val parkingService = HttpServices().getParkingService()
        val accessToken = getAccessToken()
        accessToken?.access_token?.let {
            val parkingSpacesResponse = parkingService.getParkings(authorization = "Bearer $it",
                    format = "json", lat = userLat, lon = userLong, dist = dist, max = max).await()
            parkingSpacesResponse.body()?.forEach { parkingAreas ->
                Log.d(TAG, parkingAreas.toString())
            }
            if (parkingSpacesResponse.isSuccessful){
                Log.d(TAG, "parking response success ${parkingSpacesResponse.code()}")
                val receivedParkingLots = mutableListOf<ParkingLot>()
                parkingSpacesResponse.body()?.forEach { receivedParkingLots.addAll(it.ParkingLots)  }
                // sort list by distance
                receivedParkingLots.sortBy { calculateDistance(it, userLat!!, userLong!!) }
                parkingLots.postValue(receivedParkingLots)
                responseCode.postValue(ResponseResult.Success(parkingSpacesResponse.code()))
            } else {
                Log.d(TAG, "parking response failed ${parkingSpacesResponse.code()}")
                clearParking()
                responseCode.postValue(ResponseResult.Error(parkingSpacesResponse.code(), parkingSpacesResponse.message()))
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