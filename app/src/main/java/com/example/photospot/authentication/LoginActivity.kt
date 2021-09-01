package com.example.photospot.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.photospot.MapsActivity
import com.example.photospot.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var signInButton: SignInButton
    private lateinit var loadingProgressBar: RelativeLayout

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data

                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                task.addOnCompleteListener {
                    if (it.isSuccessful) {
                        try {
                            val account = task.getResult(ApiException::class.java)
                            if (account != null) firebaseAuthWithGoogle(account)
                        } catch (e: ApiException) {
                            Toast.makeText(this, "SigninActivityFailed: 81", Toast.LENGTH_SHORT)
                                .show()
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        signInButton = findViewById(R.id.sign_in_button)
        findViewById<SignInButton>(R.id.sign_in_button).setOnClickListener(this)

        loadingProgressBar = findViewById(R.id.loadingPanel)
        loadingProgressBar.visibility = View.GONE

        firebaseAuth = FirebaseAuth.getInstance()

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        GoogleSignInClientWrapper(mGoogleSignInClient)

        if (AuthenticationHolder.firebaseUser != null) updateUI()
        if (GoogleSignIn.getLastSignedInAccount(this) != null)
            firebaseAuthWithGoogle(GoogleSignIn.getLastSignedInAccount(this)!!)
    }

    /**
     * Sets the size of the sign in options buttons
     */
    override fun onStart() {
        super.onStart()
        signInButton.setSize(SignInButton.SIZE_STANDARD)
    }

    /**
     * Start the MapsActivity if the sign in has been successful
     */
    private fun updateUI() {
        if (AuthenticationHolder.firebaseUser != null)
            startActivity(Intent(this, MapsActivity::class.java))
    }

    private fun googleSignIn() = resultLauncher.launch(mGoogleSignInClient.signInIntent)

    /**
     * Firebase sign in with google sign in credentials
     */
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        AuthenticationHolder.googleSignInClient = GoogleSignInClientWrapper(mGoogleSignInClient)
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) AuthenticationHolder.firebaseUser = task.result.user
            }.continueWith {
                AuthenticationHolder.googleSignInAccount = account
                loadingProgressBar.visibility = View.VISIBLE
                updateUI()
            }
    }

    /**
     * Chooses which sign in has to be started based on the id from the clicked button
     */
    override fun onClick(v: View) {
        when (v.id) {
            R.id.sign_in_button -> googleSignIn()
        }
    }
}
