package se.larsson.parking.views
import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import se.larsson.parking.network.models.ParkingLot
import se.larsson.parking.R

class ParkingAreaAdapter (private var parkingLots: List<ParkingLot>, private val listener: OnItemClickListener, private val context: Context) :
    RecyclerView.Adapter<ParkingAreaAdapter.ParkingAreaViewHolder>() {
    class ParkingAreaViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingAreaAdapter.ParkingAreaViewHolder {
        val cardView = LayoutInflater.from(parent.context).inflate(R.layout.parking_area_item, parent,
            false) as CardView
        return ParkingAreaViewHolder(cardView)
    }
    override fun onBindViewHolder(holder: ParkingAreaViewHolder, position: Int) {
        val view = holder.view
        val item = parkingLots[position]
        val cameraOne: ImageView = view.findViewById(R.id.parking_area_item_imageview_camera_one)
        val cameraTwo: ImageView = view.findViewById(R.id.parking_area_item_imageview_camera_two)
        val titleTextView: TextView = view.findViewById(R.id.parking_area_item_textview_title)
        val numberOfParkingSpacesTextView: TextView =
            view.findViewById(R.id.parking_area_item_textview_number_of_parkings)
        val freeParkingSpacesTextView: TextView = view.findViewById(R.id.parking_area_item_textview_free_parkings)
        titleTextView.text = item.Name
        numberOfParkingSpacesTextView.text =
            String.format(context.resources.getString(R.string.parking_area_parking_spaces), item.TotalCapacity)
        if (item.FreeSpaces == null){
            freeParkingSpacesTextView.visibility = View.GONE
        } else {
            freeParkingSpacesTextView.text =
                String.format(context.resources.getString(R.string.parking_area_free_parking_spaces), item.FreeSpaces)
            freeParkingSpacesTextView.visibility = View.VISIBLE
        }
        cameraOne.visibility = View.GONE
        cameraTwo.visibility = View.GONE
        if (item.ParkingCameras?.isNotEmpty() == true){
            if (item.ParkingCameras.size == 1){
                cameraOne.visibility = View.VISIBLE
                cameraOne.setOnClickListener {
                    listener.onItemClick(parkingLots[position], parkingLots[position].ParkingCameras!![0]!!)
                }
            } else if  (item.ParkingCameras.size > 1){
                cameraOne.setOnClickListener {
                    listener.onItemClick(parkingLots[position], parkingLots[position].ParkingCameras!![0]!!)
                }
                cameraTwo.setOnClickListener {
                    listener.onItemClick(parkingLots[position], parkingLots[position].ParkingCameras!![1]!!)
                }
                cameraOne.visibility = View.VISIBLE
                cameraTwo.visibility = View.VISIBLE
            }
        }
    }

    fun setData(newData: List<ParkingLot>) {
        this.parkingLots = newData
        notifyDataSetChanged()
    }

    override fun getItemCount() = parkingLots.size
}