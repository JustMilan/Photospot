package com.example.photospot.account

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.photospot.MapsActivity
import com.example.photospot.R
import com.example.photospot.authentication.LoginActivity
import com.example.photospot.databinding.ActivityAccountBinding
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.io.Serializable

class AccountActivity : AppCompatActivity(), Serializable {
    private var binding: ActivityAccountBinding? = null
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var profilePicture: View
    private lateinit var signOutButton: Button
    private lateinit var backToMapButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_account)

        setWidgets()
    }


    /**
     * Helper method to set the widgets present in the account activity
     */
    private fun setWidgets() {
        signOutButton = findViewById(R.id.sign_out_button)
        signOutButton.setOnClickListener { signOut() }

        backToMapButton = findViewById(R.id.back_to_maps_activity)
        backToMapButton.setOnClickListener { toMapsActivity() }

        profilePicture = findViewById(R.id.profile_picture)
//        profilePicture = firebaseUser.photoUrl
    }

    /**
     * Signs out the user from firebase and googleSignIn and redirects the user to the login screen
     */
    private fun signOut() {
        //TODO: Fix logout
//        firebaseAuth.signOut()
//        googleSignInClient.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    /**
     * Changes activity to maps Activity
     */
    private fun toMapsActivity() {
        intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }
}