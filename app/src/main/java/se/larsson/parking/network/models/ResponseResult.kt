package se.larsson.parking.network.models

sealed class ResponseResult {

    class Error(val responseCode: Int, val errorMessage: String) : ResponseResult()

    class Success(val responseCode: Int) : ResponseResult()
}