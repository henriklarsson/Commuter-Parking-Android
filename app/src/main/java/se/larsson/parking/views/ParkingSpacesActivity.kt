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
import se.larsson.parking.network.models.ParkingCamera
import se.larsson.parking.network.models.ParkingLot
import android.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import se.larsson.parking.dialog.ImageDialogFragment
import android.graphics.drawable.AnimationDrawable
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import se.larsson.parking.dialog.InfoDialogFragment
import se.larsson.parking.network.models.ResponseResult
import android.hardware.usb.UsbDevice.getDeviceId
import android.content.Context.TELEPHONY_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import android.telephony.TelephonyManager
import android.content.Context
import com.google.android.gms.ads.AdView


class ParkingSpacesActivity : AppCompatActivity(), OnItemClickListener {
    var viewModel: ParkingSpacesViewModel? = null
    var location: Location? = null
    lateinit var mAdView : AdView

    override fun onItemClick(item: ParkingLot, camera: ParkingCamera) {
        Log.d(TAG, "On item clicked $item")
        showImageDialog("${item.Id}/${camera.Id}")
    }

    private val TAG = ParkingSpacesActivity::class.java.simpleName
    private var fabAnimation: AnimationDrawable? = null
    private lateinit var viewAdapter: ParkingAreaAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val telephonyManager1 = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//        val imei = telephonyManager1.deviceId
        setContentView(R.layout.activity_main)
        MobileAds.initialize(this, "ca-app-pub-8384659766694860~7157465096")
        val adRequest = AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
            .addTestDevice(imei).build()
        mAdView = adView

        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        val statusAfterInit = MobileAds.getInitializationStatus()
        statusAfterInit.adapterStatusMap.forEach { t, u ->

            Log.d(TAG, "MobileAds description: ${u.description} , initializationState:" +
                    " ${u.initializationState} latency: ${u.latency}")
        }






        viewModel = ViewModelProviders.of(this).get(ParkingSpacesViewModel::class.java)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        viewManager = LinearLayoutManager(this)
        viewAdapter = ParkingAreaAdapter(parkingLots = viewModel?.parkingLots?.value ?: mutableListOf(), listener = this, context = this)
        parkings_recycler_view.adapter = viewAdapter
        parkings_recycler_view.layoutManager = viewManager
        getLocation()
        // Create the observer which updates the UI.
        val parkingSpacesObserver = Observer<MutableList<ParkingLot>> { parkingSpaces ->
            // Update the UI, in this case, a TextView.
            Log.d(TAG, "Parking spaces found: ${parkingSpaces?.size}")
            parkingSpaces?.let {
                viewAdapter.setData(parkingSpaces)
            }
            parkingSpaces?.size?.let {
                if (it > 0){
                    parkings_recycler_view.visibility = View.VISIBLE
                    background_imageview.visibility = View.INVISIBLE
                } else {
                    parkings_recycler_view.visibility = View.INVISIBLE
                    background_imageview.visibility = View.VISIBLE
                }
            }
        }

        val responseObserver = Observer<ResponseResult> { responseResult ->
            fabAnimation?.stop()
            when (responseResult){
                is ResponseResult.Error -> {
                    Snackbar.make(coordinatorLayout,"Response code: ${responseResult.responseCode} error message: ${responseResult.errorMessage}", Snackbar.LENGTH_SHORT ).show()
                }
            }
        }
        viewModel?.responseCode?.observe(this, responseObserver)
        fabAnimation = fab.drawable as AnimationDrawable
        viewModel?.parkingLots?.observe(this, parkingSpacesObserver)
        fab.setOnClickListener {
            viewModel?.clearParking()
            fabAnimation?.start()
            getLocation()
        }

        info_imageview.setOnClickListener { view ->
//            showInfoDialog()

            val statusAfterInit = MobileAds.getInitializationStatus()
            statusAfterInit.adapterStatusMap.forEach { t, u ->

                Log.d(TAG, "MobileAds description: ${u.description} , initializationState:" +
                        " ${u.initializationState} latency: ${u.latency}")
            }
            initAds()
        }
    }

    private fun getLocation(){
        checkLocationPermission()
        this.fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
                Log.d(TAG, "location: ${location.toString()}")
                this.location = location
                getParkingLots()
            }
    }

    private fun getParkingLots(){
        GlobalScope.launch {// activity should create scope
            viewModel?.getParkingLots(userLong = location?.longitude, userLat = location?.latitude)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(se.larsson.parking.R.menu.menu_main, menu)
        return true
    }

    private fun showImageDialog(handle: String) {
        val manager = supportFragmentManager
        val args = Bundle()
        val alertDialogFragment =  ImageDialogFragment()
        GlobalScope.launch {// activity should create scope
            viewModel?.getAccessToken()?.access_token?.let{
                args.putString(ImageDialogFragment.TOKEN, it)
            }
            args.putString(ImageDialogFragment.CAMERA_NUMBER, handle)
            alertDialogFragment.arguments = args
            alertDialogFragment.show(manager, "ImageDialogFragment")
        }
    }
    private fun showInfoDialog() {
        val manager = supportFragmentManager
        val alertDialogFragment =  InfoDialogFragment()
        alertDialogFragment.show(manager, "InfoDialogFragment")
    }

    private fun checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation()

                } else {
                    getParkingLots()
                }
                return
            }
        }
    }

    private fun initAds(){
        val adRequest =  AdRequest.Builder().addTestDevice("3BD86B228C1860DB6BEF7E6B1CE0F99B").build()
        adView.loadAd(AdRequest.Builder().addTestDevice("3BD86B228C1860DB6BEF7E6B1CE0F99B").build() )

    }

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: Int = 2001
    }

}
