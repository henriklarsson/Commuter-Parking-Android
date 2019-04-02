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
import android.widget.RelativeLayout
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.view.Window.FEATURE_NO_TITLE
import android.app.Dialog
import android.net.Uri
import android.view.*
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders


class ParkingsActivity : AppCompatActivity(), OnItemClickListener {
    override fun onItemClick(item: ParkingLot, camera: ParkingCamera) {
        Log.d(TAG, "On item clicked ${item.toString()}")
        showImage()
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
        checkLocationPermission()
        val viewModel = ViewModelProviders.of(this).get(ParkingsViewModel::class.java)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                Log.d(TAG, "location: ${location.toString()}")
                viewModel.getParkingLots(userLat = location?.latitude, userLong = location?.longitude)
            }


        viewManager = LinearLayoutManager(this)
        viewAdapter = ParkingAreaAdapter(parkingLots = viewModel.parkingLots.value ?: mutableListOf(), listener = this, context = this)
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

    fun showImage() {
        val builder = Dialog(this)
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE)
        builder.getWindow().setBackgroundDrawable(
            ColorDrawable(android.graphics.Color.TRANSPARENT)
        )
        builder.setOnDismissListener(DialogInterface.OnDismissListener {
            //nothing;
        })

        val imageView = ImageView(this)
        imageView.visibility = View.VISIBLE
        Glide.with(this)
            .load(getUrl())
            .into(imageView);
        builder.addContentView(
            imageView, RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        builder.show()
    }

    fun getUrl(): GlideUrl {
        return  GlideUrl(
            "https://api.vasttrafik.se/spp/v3/parkingImages/5030/1", LazyHeaders.Builder()
                .addHeader("Authorization", "Bearer 66fb8332-010a-362a-bfba-725f6cc4a733")
                .build()
        )
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
