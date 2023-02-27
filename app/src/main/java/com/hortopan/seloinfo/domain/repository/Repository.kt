package com.hortopan.seloinfo.domain.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.hortopan.seloinfo.domain.entity.UserDataByGmailAuth
import com.hortopan.seloinfo.domain.entity.UsersDocuments

interface Repository {
    suspend fun isAuthorized(): Boolean
    suspend fun signUpByGoogle(account: GoogleSignInAccount)

    suspend fun getUserData(): UserDataByGmailAuth

    suspend fun saveUserData(userData: UserDataByGmailAuth)

    suspend fun getUsersDocumentsID(): List<String>
}