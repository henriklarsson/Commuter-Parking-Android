package se.larsson.parking.views

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import se.larsson.parking.network.oauth.HttpServices
import se.larsson.parking.network.oauth.models.ParkingLot

class ParkingsViewModel: ViewModel() {
    private val TAG = ParkingsActivity::class.java.simpleName
    val parkingLots = MutableLiveData<MutableList<ParkingLot>>()

    fun getParkingLots(){
        GlobalScope.launch {// activity should create scope
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
            val reciviedParkingLots = mutableListOf<ParkingLot>()
            val body = await.body()?.forEach { reciviedParkingLots.addAll(it.ParkingLots)  }

            parkingLots.postValue(reciviedParkingLots)

        }
    }
}