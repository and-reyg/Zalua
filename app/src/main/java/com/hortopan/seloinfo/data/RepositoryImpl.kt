package com.hortopan.seloinfo.data

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.hortopan.seloinfo.domain.entity.UserDataByGmailAuth
import com.hortopan.seloinfo.domain.entity.User
import com.hortopan.seloinfo.domain.repository.Repository
import kotlinx.coroutines.tasks.await

class RepositoryImpl : Repository {

    private val auth = FirebaseAuth.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()

    override suspend fun isAuthorized(): Boolean {
        val currentUser = auth.currentUser
        return currentUser != null
    }

    override suspend fun signUpByGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
    }

    override suspend fun getUserData(): UserDataByGmailAuth {
        val currentUser = auth.currentUser
        val email = currentUser?.email ?: throw Exception("User is not authenticated")
        val name = currentUser.displayName ?: "Unknown"
        val id = currentUser.uid
        return UserDataByGmailAuth(name, email, id)
    }

    override suspend fun saveUserData(userData: UserDataByGmailAuth) {
        val db = FirebaseFirestore.getInstance()
        val docId = "user_${userData.id}"
        val user = User(userData.name, userData.id)
        db.collection("Users")
            .document(docId)
            .set(user)
            .await()
    }
}