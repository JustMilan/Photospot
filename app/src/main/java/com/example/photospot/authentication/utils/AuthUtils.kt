package com.example.photospot.authentication.utils

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

object AuthUtils {

    @JvmStatic
    fun firebaseAuthWithGoogle(
        firebaseAuth: FirebaseAuth,
        account: GoogleSignInAccount
    ): FirebaseUser? {
        var user: FirebaseUser? = null
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user = firebaseAuth.currentUser
                }
            }
        return user
    }
}
