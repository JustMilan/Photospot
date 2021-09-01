package com.example.photospot.authentication

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object AuthenticationHolder {

    var firebaseUser: FirebaseUser? = null
    var firebaseAuth: FirebaseAuth? = null

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) this.firebaseUser = task.result.user
            }
    }

    /**
     * Signs out the user from firebase and googleSignIn and redirects the user to the login screen
     */
    fun signOut() {
        firebaseAuth?.signOut()
        Firebase.auth.signOut()
    }
}