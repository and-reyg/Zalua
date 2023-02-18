package com.hortopan.seloinfo.data

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.hortopan.seloinfo.domain.repository.Repository

class RepositoryImpl : Repository {

    private val auth = FirebaseAuth.getInstance()

    override suspend fun isAuthorized(): Boolean {
        val currentUser = auth.currentUser
        return currentUser != null
    }

    override suspend fun signUpByGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
    }
}