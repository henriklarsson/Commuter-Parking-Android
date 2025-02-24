package se.larsson.parking.network

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*
import se.larsson.parking.network.models.ParkingArea


interface ParkingService {
    @FormUrlEncoded
    @Headers("Authorization: Basic U0E1WjNxUnNpYlByRWloNkQ2YTY4SkZqNzZ3YTpQVnRmZzlCa2NZSzd5aUhjMjgyQktseHFPblVh")
    @POST("/token")
    fun getAccessTokenAsync(@Field("grant_type") grantType: String = "client_credentials"): Deferred<Response<AccessToken>>

    @GET("/spp/v3/parkings")
    fun getParkingSpacesAsync(@Header("Authorization" ) authorization: String,
                              @Query("format") format: String = "json",
                              @Query( "lat") lat: Double? = null,
                              @Query( "lon") lon: Double? = null,
                              @Query("dist") dist: Int? = null,
                              @Query("max") max: Int? = null): Deferred<Response<List<ParkingArea>>>
}