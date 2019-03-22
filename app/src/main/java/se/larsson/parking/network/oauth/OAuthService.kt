package se.larsson.parking.network.oauth

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*

interface OAuthService {
    @FormUrlEncoded
    @Headers("Authorization: Basic U0E1WjNxUnNpYlByRWloNkQ2YTY4SkZqNzZ3YTpQVnRmZzlCa2NZSzd5aUhjMjgyQktseHFPblVh")
    @POST("/token")
    fun getAccessToken(@Field("grant_type") grantType: String = "client_credentials"): Deferred<Response<AccessToken>>
}