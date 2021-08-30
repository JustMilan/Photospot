package com.example.photospot.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var signInButton: SignInButton

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

        // Build a GoogleSignInClient with the options specified by gso.
        signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        findViewById<SignInButton>(R.id.sign_in_button).setOnClickListener(this)

        firebaseAuth = FirebaseAuth.getInstance()

        val googleSignInOptions = GoogleSignInOptions.Builder()
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        if (firebaseAuth.currentUser != null) {
            val user: FirebaseUser = firebaseAuth.currentUser!!
            updateUI(user)
        }
    }

    override fun onStart() {
        super.onStart()
        signInButton.setSize(SignInButton.SIZE_STANDARD)
    }

    private fun updateUI(account: FirebaseUser?) {
        if (account != null) {
            val intent = Intent(this, MapsActivity::class.java).apply {
                putExtra("Account", account)
            }
            startActivity(intent)
        }
    }

    private fun googleSignIn() {
        val intent = mGoogleSignInClient.signInIntent
        resultLauncher.launch(intent)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    updateUI(task.result.user)
                }
            }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sign_in_button -> googleSignIn()
        }
    }
}
