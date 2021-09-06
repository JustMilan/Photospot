package com.example.photospot.account

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.photospot.MapsActivity
import com.example.photospot.R
import com.example.photospot.authentication.AuthenticationHolder
import com.example.photospot.authentication.LoginActivity
import com.example.photospot.databinding.ActivityAccountBinding
import com.google.firebase.auth.FirebaseAuth
import java.io.Serializable
import android.app.ActivityManager.RunningTaskInfo

import android.app.ActivityManager
import android.content.Context
import android.util.Log


class AccountActivity : AppCompatActivity(), Serializable {
    private var binding: ActivityAccountBinding? = null

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
        Glide
            .with(profilePicture)
            .load(AuthenticationHolder.googleSignInAccount?.photoUrl)
            .centerCrop()
            .placeholder(R.drawable.account_white)
            .into(profilePicture as ImageView)

        emailTextView = findViewById(R.id.email_holder)
        emailTextView.text = FirebaseAuth.getInstance().currentUser?.email
    }

    /**
     * Signs out the user from firebase and googleSignIn and redirects the user to the login screen
     */
    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        AuthenticationHolder.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Changes activity to maps Activity
     */
    private fun toMapsActivity() = onBackPressed()
}