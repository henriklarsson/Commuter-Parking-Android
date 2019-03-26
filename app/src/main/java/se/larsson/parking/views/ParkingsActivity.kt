package se.larsson.parking.views

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import se.larsson.parking.R
import se.larsson.parking.network.oauth.models.ParkingLot

class ParkingsActivity : AppCompatActivity() {
    private val TAG = ParkingsActivity::class.java.simpleName
//    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ParkingAreaAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewModel = ViewModelProviders.of(this).get(ParkingsViewModel::class.java)
        viewManager = LinearLayoutManager(this)
        viewAdapter = ParkingAreaAdapter(parkingLots = viewModel.parkingLots.value ?: mutableListOf<ParkingLot>())
        parkings_recycler_view.adapter = viewAdapter
        parkings_recycler_view.layoutManager = viewManager
        // Create the observer which updates the UI.
        val nameObserver = Observer<MutableList<ParkingLot>> { parkings ->
            // Update the UI, in this case, a TextView.
            Log.d(TAG, "Parkinglots found: ${parkings?.size}")
            parkings?.let {
                viewAdapter.setData(parkings)
            }

        }
        viewModel.parkingLots.observe(this, nameObserver)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            viewModel.getParkingLots()
            viewAdapter.notifyDataSetChanged()


        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
