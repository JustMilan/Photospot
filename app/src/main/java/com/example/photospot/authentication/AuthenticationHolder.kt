package com.example.photospot.authentication

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


object AuthenticationHolder {

    var firebaseUser: FirebaseUser? = null
    var googleSignInClient: GoogleSignInClientWrapper? = null
    var googleSignInAccount: GoogleSignInAccount? = null

    /**
     * Signs out the user from firebase and googleSignIn and redirects the user to the login screen
     */
    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        googleSignInClient?.googleSignInClient?.signOut()
        firebaseUser = null
    }
}