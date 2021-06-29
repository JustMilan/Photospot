package com.example.photospot

import android.Manifest
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.example.photospot.utils.MapUtils.checkPermissions
import com.example.photospot.databinding.ActivityMapsBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.*


private val GoogleMap?.MoveCamera: Unit
    get() {}

class MapsActivity : FragmentActivity(), OnMapReadyCallback {

    private var cancelationToken: CancellationTokenSource = CancellationTokenSource()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mMap: GoogleMap? = null
    private var binding: ActivityMapsBinding? = null

    // widgets
    private lateinit var centerButton: ImageView

    private var lastLocation: Location = Location(LocationManager.NETWORK_PROVIDER)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        Objects.requireNonNull(mapFragment)!!.getMapAsync(this)

        initializeLocation()

        centerButton = findViewById(R.id.center_button)
        centerButton.setOnClickListener { centerMap() }
    }

    private fun initializeLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    lastLocation = location
                }
            }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        try {
            checkPermissions(this)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace() // TODO: handle error
        }

        //TODO: remove this as it is already being called in the utils
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        mMap!!.isMyLocationEnabled = true

        //TODO: get current location


        //TODO: move map


        //TODO: Update documentation
    }

    fun moveMap(location: Location) {
        getCurrentLocation()
        val latLng = LatLng(location.latitude, location.longitude)
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
    }

    fun centerMap() {
        getCurrentLocation()
        moveMap(lastLocation)
    }

    fun getCurrentLocation() {
        checkPermissions(this)
//        Log.e("yes", fusedLocationClient.getCurrentLocation(-1, cancelationToken.token).toString())

    }
}