package com.example.photospot.account

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.photospot.MapsActivity
import com.example.photospot.R
import com.example.photospot.authentication.AuthenticationHolder
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
    private lateinit var emailTextView: TextView
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

        emailTextView = findViewById(R.id.email_holder)
        emailTextView.text = AuthenticationHolder.firebaseUser?.email
    }

    /**
     * Signs out the user from firebase and googleSignIn and redirects the user to the login screen
     */
    private fun signOut() {
        AuthenticationHolder.signOut()
        MapsActivity.apply { finish() }
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Changes activity to maps Activity
     */
    private fun toMapsActivity() {
        //now how to navigate to new fragment
        onBackPressed()
    }
}