package se.larsson.parking.views

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import se.larsson.parking.network.oauth.HttpServices
import se.larsson.parking.network.oauth.models.ParkingArea

class ParkingsViewModel: ViewModel() {
    private val TAG = ParkingsActivity::class.java.simpleName
    val parkingAreas: MutableLiveData<List<ParkingArea>> by lazy {
        MutableLiveData<List<ParkingArea>>()
    }
    fun getParkingAreas(){
        GlobalScope.launch {
            val oAuthService = HttpServices().getOAuthService()
            val accessToken = oAuthService.getAccessToken()
            val parkingService = HttpServices().getParkingService()
            val tokenResponse = accessToken.await()
            Log.d(TAG, tokenResponse.body()?.access_token)
            val parkings =
                parkingService.getParkings(authorization = "Bearer ${tokenResponse?.body()?.access_token!!}",
                    format = "json")
            val await = parkings.await()
            await.body()!!.forEach {
                Log.d(TAG, it.toString())
            }
//                Log.d(TAG, "Timestamp: ${await.timestamp}")
            Log.d(TAG, "Timestamp2: ${System.currentTimeMillis()}")
            parkingAreas.postValue(await.body())

        }
    }
}