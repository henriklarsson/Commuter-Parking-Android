package se.larsson.parking.views

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import kotlinx.android.synthetic.main.activity_main.*
import se.larsson.parking.R
import se.larsson.parking.network.oauth.models.ParkingCamera
import se.larsson.parking.network.oauth.models.ParkingLot
import android.view.*
import se.larsson.parking.dialog.ImageDialogFragment





class ParkingsActivity : AppCompatActivity(), OnItemClickListener {
    var viewModel: ParkingsViewModel? = null
    override fun onItemClick(item: ParkingLot, camera: ParkingCamera) {
        Log.d(TAG, "On item clicked ${item.toString()}")
        showImage("${item.Id}/${camera.Id}")
    }

    private val TAG = ParkingsActivity::class.java.simpleName
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: Int = 2001
//    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ParkingAreaAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(ParkingsViewModel::class.java)
        checkLocationPermission()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                Log.d(TAG, "location: ${location.toString()}")
                viewModel?.getParkingLots(userLat = location?.latitude, userLong = location?.longitude)
            }


        viewManager = LinearLayoutManager(this)
        viewAdapter = ParkingAreaAdapter(parkingLots = viewModel!!.parkingLots.value ?: mutableListOf(), listener = this, context = this)
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
        viewModel!!.parkingLots.observe(this, nameObserver)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            viewModel!!.getParkingLots()
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            viewAdapter.notifyDataSetChanged()


        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(se.larsson.parking.R.menu.menu_main, menu)
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

    fun showImage(handle: String) {
        val manager = supportFragmentManager
        val args = Bundle()
        val alertDialogFragment =  ImageDialogFragment()
        viewModel?.token?.let{
            args.putString(ImageDialogFragment.TOKEN, it)
        }
        args.putString(ImageDialogFragment.CAMERA_NUMBER, handle)

        alertDialogFragment.arguments = args
        alertDialogFragment.show(manager, "ImageDialogFragment")


    }

    private fun checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }
}
