/**
* Smart Commuter Parking (SPP)
* Provides access to Västtrafik commuter parking API
*
* OpenAPI spec version: 3.0
* 
*
* NOTE: This class is auto generated by the swagger code generator program.
* https://github.com/swagger-api/swagger-codegen.git
* Do not edit the class manually.
*/
package se.larsson.parking.network.models

/**
 * 
 * @param Name 
 * @param TotalCapacity 
 * @param ParkingType 
 * @param Lat 
 * @param Id 
 * @param ParkingCameras Only available if ParkingType Name is SMARTCARPARK
 * @param Lon 
 * @param IsRestrictedByBarrier 
 * @param FreeSpaces Number of free spaces. Only available if ParkingType Name is SMARTCARPARK
 */
data class ParkingLot (
    val Name: kotlin.String,
    val TotalCapacity: kotlin.Int,
    val ParkingType: ParkingType,
    val Lat: kotlin.Double,
    val Id: kotlin.Int,
    val Lon: kotlin.Double,
    val IsRestrictedByBarrier: kotlin.Boolean,
    /* Only available if ParkingType Name is SMARTCARPARK */
    val ParkingCameras: kotlin.Array<ParkingCamera>? = null,
    /* Number of free spaces. Only available if ParkingType Name is SMARTCARPARK */
    val FreeSpaces: kotlin.Int? = null
)

