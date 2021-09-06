package com.example.photospot.authentication

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient


object AuthenticationHolder {

    var googleSignInClient: GoogleSignInClientWrapper? = null
    var googleSignInAccount: GoogleSignInAccount? = null

    /**
     * Signs out the user from firebase and googleSignIn and redirects the user to the login screen
     */
    fun signOut() {
        googleSignInClient?.googleSignInClient?.signOut()
    }
}