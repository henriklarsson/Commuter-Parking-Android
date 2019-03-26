package se.larsson.parking.views

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import se.larsson.parking.R
import se.larsson.parking.network.oauth.models.ParkingLot
import android.R.attr.data




class ParkingAreaAdapter (private var parkingLots: List<ParkingLot>) :
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
        val titleTextView: TextView = view.findViewById(R.id.parking_area_item_textview_title)
        val infoTextView: TextView = view.findViewById(R.id.parking_area_item_textview_info)
        titleTextView.text = item.Name

        infoTextView.text = "Total spaces ${item.TotalCapacity} Free spaces: ${item.FreeSpaces}"
    }

    fun setData(newData: List<ParkingLot>) {
        this.parkingLots = newData
        notifyDataSetChanged()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = parkingLots.size
}