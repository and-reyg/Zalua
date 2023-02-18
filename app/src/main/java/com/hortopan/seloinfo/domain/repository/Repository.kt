package com.hortopan.seloinfo.domain.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface Repository {
    suspend fun isAuthorized(): Boolean
    suspend fun signUpByGoogle(account: GoogleSignInAccount)
}