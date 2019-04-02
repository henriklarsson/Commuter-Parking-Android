package se.larsson.parking.views

import se.larsson.parking.network.oauth.models.ParkingCamera
import se.larsson.parking.network.oauth.models.ParkingLot

interface OnItemClickListener {
    fun onItemClick(item: ParkingLot, camera: ParkingCamera)
}