package se.larsson.parking.network.oauth

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ParkingService {
    @GET("/parkings")
    fun getParkings(@Header("Authorization" ) authorization: String, @Query("format") format: String = "json") : Deferred<Response<AccessToken>>
}