package com.example.photospot

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentActivity
import com.example.photospot.account.AccountActivity
import com.example.photospot.authentication.LoginActivity
import com.example.photospot.autocomplete.AutocompleteAdapter
import com.example.photospot.autocomplete.AutocompleteItemData
import com.example.photospot.databinding.ActivityMapsBinding
import com.example.photospot.utils.MapUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.io.Serializable


class MapsActivity : FragmentActivity(), OnMapReadyCallback, Serializable {

    private val autocompleteListCounter = 4

    private var mMap: GoogleMap? = null
    private var binding: ActivityMapsBinding? = null
    private var lastLocation: Location = Location(LocationManager.NETWORK_PROVIDER)

    private lateinit var searchbar: EditText
    private lateinit var centerButton: ImageView
    private lateinit var accountButton: ImageView
    private lateinit var placesClient: PlacesClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var clearSearchbarButton: Button

    // private lateinit var autocompleteList: RecyclerView
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // autocomplete
    private lateinit var autocompleteListView: ListView
    private var autocompleteList: ArrayList<AutocompleteItemData> = ArrayList()

    /**
     * Registers the permissions callback, which handles the user's response to the
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

        // Initialize places SDK
        Places.initialize(applicationContext, "AIzaSyAZE4HdO3wyjoBtXEuCv4fglaQfPEFjeXs")
        placesClient = Places.createClient(this)

        initializeLocation()
        setWidgets()

        // Begin Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        firebaseAuth = FirebaseAuth.getInstance()

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso)
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
     * Sets all the widgets in the MapsActivity
     */
    private fun setWidgets() {
        centerButton = findViewById(R.id.center_button)
        centerButton.setOnClickListener { centerMap() }
        accountButton = findViewById(R.id.account_button)
        accountButton.setOnClickListener { showAccount() }
        searchbar = findViewById(R.id.input_search)
        searchbar.setOnFocusChangeListener { _, hasFocus -> handleSearchbarFocus(hasFocus) }
        searchbar.addTextChangedListener { search() }
        clearSearchbarButton = findViewById(R.id.clear_searchbar)
        clearSearchbarButton.setOnClickListener { clearSearchbar() }


        autocompleteListView = findViewById(R.id.autocomplete_list)
        autocompleteListView.adapter = AutocompleteAdapter(this, autocompleteList)
        autocompleteListView.visibility = View.GONE
        autocompleteListView.setOnItemClickListener { parent, view, position, id ->
            onAutocompleteClick(parent, view, position, id)
        }
    }

    /**
     * Creates the fusedLocationClient, checks the permissions and requests them if nessecary
     * Than finds the last location (often the current location)
     */
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
     * Moves the map to the given location.
     */
    private fun moveMap(location: Location) {
        currentLocation()
        moveMap(LatLng(location.latitude, location.longitude))
    }

    /**
     * Moves map based on the given LatLng coordinates
     */
    private fun moveMap(latLng: LatLng) {
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    /**
     * Centers the map on device location.
     */
    private fun centerMap() {
        currentLocation()
        if (lastLocation.latitude == 0.0 && lastLocation.longitude == 0.0) {
            moveMap(LatLng(52.373169, 4.890660))
        } else {
            moveMap(lastLocation)
        }
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

    /**
     * Checks if there is a currently signed in user
     */
    private fun isSignedIn(): Boolean {
        return GoogleSignIn.getLastSignedInAccount(this) != null
    }

    /**
     * Sends the user to the loginScreen
     */
    private fun goToLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    /**
     * Performs a google places autocomplete api call with the contents of the edittext
     * Fills the recyclerview with the results afterwards
     */
    private fun search() {
        val token = AutocompleteSessionToken.newInstance()
        val request = MapUtils.autocompleteRequestBuilder(token, searchbar.text.toString())

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                val dataset: MutableList<List<String>> = emptyList<List<String>>().toMutableList()
                for ((counter, prediction) in response.autocompletePredictions.withIndex()) {
                    if (counter == autocompleteListCounter) break
                    dataset.add(
                        listOf(
                            prediction.getPrimaryText(null).toString(),
                            prediction.getSecondaryText(null).toString(),
                            prediction.placeId
                        )
                    )
                }

                val autocompleteItems: ArrayList<AutocompleteItemData> = ArrayList()
                dataset.forEach { list ->
                    autocompleteItems.add(
                        AutocompleteItemData(list[0], list[1], list[2])
                    )
                }
                autocompleteListView.adapter = AutocompleteAdapter(this, autocompleteItems)
            }.addOnFailureListener { exception: Exception? ->
                if (exception is ApiException) {
                    Log.e("autocomplete", "Place not found: " + exception.statusCode)
                }
            }
    }

    /**
     * Hides autocomplete list if the searchbar isn't in focus
     */
    private fun handleSearchbarFocus(hasFocus: Boolean) {
        if (hasFocus) {
            autocompleteListView.visibility = View.VISIBLE
            search()
        } else {
            autocompleteListView.visibility = View.GONE
        }
    }

    /**
     * onClickListener for autocomplete list
     * Extracts the info from the autocomplete item and tries to find the corresponding location,
     * moves the map towards it and hides the keyboard and autocompleteList.
     */
    private fun onAutocompleteClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val placeId =
            (autocompleteListView.adapter.getItem(position) as AutocompleteItemData).placeId
        val placeRequest =
            FetchPlaceRequest.newInstance(placeId, listOf(Place.Field.ADDRESS, Place.Field.LAT_LNG))
        val location = placesClient.fetchPlace(placeRequest)
        location.addOnSuccessListener {
            when {
                it.place.latLng != null -> {
                    moveMap(it.place.latLng!!)
                }
                it.place.address != null -> {
                    moveMap(Location(it.place.address))
                }
                else -> {
                    Toast.makeText(
                        this,
                        "Couldn't find address or coordinates, please contact us",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        hideAutocompleteList()
    }

    /**
     * Clear focus on outside click
     */
    private fun hideAutocompleteList() {
        autocompleteListView.visibility = View.GONE
        currentFocus?.apply {
            // Hide keyboard
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            if (this is EditText) clearFocus()
        }
    }

    private fun clearSearchbar() {
        searchbar.text.clear()
    }

    private fun showAccount() {
        val intent = Intent(this, AccountActivity::class.java)
        startActivity(intent)
    }
}
