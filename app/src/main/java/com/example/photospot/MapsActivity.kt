package com.example.photospot

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.example.photospot.utils.MapUtils.HandlePermissions
import com.example.photospot.databinding.ActivityMapsBinding
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import java.util.*


private val GoogleMap?.MoveCamera: Unit
    get() {}

class MapsActivity : FragmentActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private var binding: ActivityMapsBinding? = null
    private val mGoogleApiClient: GoogleApiClient? = null


    // Create persistent LocationManager reference
//    private val locationManager: LocationManager? = getSystemService(LOCATION_SERVICE) as LocationManager?


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        //TODO: enable center button

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        Objects.requireNonNull(mapFragment)!!.getMapAsync(this)
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
            HandlePermissions(this)
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

    fun moveCamera(location: Location) {
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
    }
}