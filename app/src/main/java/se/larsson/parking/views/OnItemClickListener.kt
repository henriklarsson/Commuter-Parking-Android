package se.larsson.parking.views

import se.larsson.parking.network.models.ParkingCamera
import se.larsson.parking.network.models.ParkingLot

interface OnItemClickListener {
    fun onItemClick(item: ParkingLot, camera: ParkingCamera)
}