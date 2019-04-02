package se.larsson.parking.views

import android.content.Context
import android.opengl.Visibility
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import se.larsson.parking.network.oauth.models.ParkingLot
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.model.GlideUrl
import se.larsson.parking.R
import se.larsson.parking.network.oauth.models.ParkingType
import se.larsson.parking.network.oauth.models.ParkingType.*

class ParkingAreaAdapter (private var parkingLots: List<ParkingLot>, private val listener: OnItemClickListener, private val context: Context) :
    RecyclerView.Adapter<ParkingAreaAdapter.ParkingAreaViewHolder>() {


    class ParkingAreaViewHolder(val view: View) : RecyclerView.ViewHolder(view)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ParkingAreaAdapter.ParkingAreaViewHolder {
        // create a new view
        val cardView = LayoutInflater.from(parent.context).inflate(R.layout.parking_area_item, parent, false) as CardView
        // set the view's size, margins, paddings and layout parameters

        return ParkingAreaViewHolder(cardView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ParkingAreaViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val view = holder.view
        val item = parkingLots[position]
        val imageView: ImageView = view.findViewById(R.id.parking_area_item_imageview_parking)
        val cameraOne: ImageView = view.findViewById(R.id.parking_area_item_imageview_camera_one)
        val cameraTwo: ImageView = view.findViewById(R.id.parking_area_item_imageview_camera_two)

        val titleTextView: TextView = view.findViewById(R.id.parking_area_item_textview_title)
        val numberOfParkingsTextView: TextView = view.findViewById(R.id.parking_area_item_textview_number_of_parkings)
        val freeParkingsTextView: TextView = view.findViewById(R.id.parking_area_item_textview_free_parkings)
        titleTextView.text = item.Name

        when (item.ParkingType.Number){

            1 -> imageView.setImageResource(R.drawable.parking_icon_96) // parking
            2 -> imageView.setImageResource(R.drawable.smart_parking_icon_96) // smart parking
        }

        numberOfParkingsTextView.text = "Total spaces ${item.TotalCapacity}"
        if (item.FreeSpaces == null){
            freeParkingsTextView.visibility = View.GONE
        } else {
            freeParkingsTextView.text = "Free spaces ${item.FreeSpaces}"
            freeParkingsTextView.visibility = View.VISIBLE
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


//            Glide.with(context).load(getUrl()).into(imageView);
//                view.setOnClickListener { listener.onItemClick(parkingLots[position])
//
//                }
        }



    }


    fun setData(newData: List<ParkingLot>) {
        this.parkingLots = newData
        notifyDataSetChanged()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = parkingLots.size
}