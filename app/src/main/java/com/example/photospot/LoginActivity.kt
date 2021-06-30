package com.example.photospot

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

private const val GOOGLE_SIGN_IN = 9001

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var signInButton: SignInButton


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

        if (firebaseAuth.getCurrentUser() != null) {
            val user: FirebaseUser = firebaseAuth.getCurrentUser()!!
            updateUI(user)
        }
    }

    override fun onStart() {
        super.onStart()
        signInButton.setSize(SignInButton.SIZE_STANDARD)
    }

    private fun updateUI(account: FirebaseUser?) {
        if (account == null) {
            setContentView(R.layout.login)
        } else {
            val intent = Intent(this, MapsActivity::class.java).apply {
                putExtra("Account", account)
            }
            startActivity(intent)
        }
    }

    private fun googleSignIn() {
        val signIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signIntent, GOOGLE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                    updateUI(firebaseAuth.currentUser)
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "SigninActivityFailed: 81", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider
            .getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = firebaseAuth.currentUser
                    updateUI(user)
                } else {
                    Toast.makeText(this, "SignIn failed!", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sign_in_button -> googleSignIn()
        }
    }
}