package com.example.photospot

import android.Manifest
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.example.photospot.authentication.LoginActivity
import com.example.photospot.databinding.ActivityMapsBinding
import com.example.photospot.utils.MapUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class MapsActivity : FragmentActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private lateinit var centerButton: ImageView
    private lateinit var signOutButton: Button
    private var binding: ActivityMapsBinding? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastLocation: Location = Location(LocationManager.NETWORK_PROVIDER)
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    /**
     * Register the permissions callback, which handles the user's response to the
     * system permissions dialog. Save the return value, an instance of
     * ActivityResultLauncher. You can use either a val, as shown in this snippet,
     * or a lateinit var in your onAttach() or onCreate() method.
     */
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isSignedIn()) run {
            goToLoginScreen()
        }

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        firebaseUser = intent.getParcelableExtra("Account")!!

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        initializeLocation()

        centerButton = findViewById(R.id.center_button)
        centerButton.setOnClickListener { centerMap() }
        signOutButton = findViewById(R.id.sign_out_button)
        signOutButton.setOnClickListener { signOut() }

        // Begin Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        firebaseAuth = FirebaseAuth.getInstance()

        //  Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun initializeLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkPermission()
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
        checkPermission()
        when {
            MapUtils.checkPermissionEnabled(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) -> mMap!!.isMyLocationEnabled = true
        }
        centerMap()
    }

    /**
     * Moves the map to the given location.
     */
    private fun moveMap(location: Location) {
        currentLocation()
        val latLng = LatLng(location.latitude, location.longitude)
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    /**
     * Centers the map on device location.
     */
    private fun centerMap() {
        currentLocation()
        moveMap(lastLocation)
    }

    /**
     * Updates the lastLocation attribute.
     */
    private fun currentLocation() {
        checkPermission()
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    lastLocation = location
                }
            }
    }

    /**
     * Calls the permission check and request from the Utils.
     */
    private fun checkPermission() {
        MapUtils.checkPermission(this, requestPermissionLauncher)
    }

    private fun signOut() {
        firebaseAuth.signOut()
        googleSignInClient.signOut()
        goToLoginScreen()
    }

    private fun isSignedIn(): Boolean {
        return GoogleSignIn.getLastSignedInAccount(this) != null
    }

    private fun goToLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}