package com.hortopan.seloinfo.data

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.hortopan.seloinfo.domain.entity.Region
import com.hortopan.seloinfo.domain.entity.Town
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
        val id = currentUser?.uid ?: "Unknown ID"
        return UserDataByGmailAuth(name, email, id)
    }

    override suspend fun saveUserData(userData: UserDataByGmailAuth, selectedTownId: String) {
        val fireStore = fireStore
        val docId = userData.id
        //val user = User(userData.name, userData.id)
        val user = User(userGmailName = userData.name, selectedTownId = selectedTownId)
        fireStore.collection("Users")
            .document(docId)
            .set(user)
            .await()
    }

    override suspend fun getUsersDocumentsID(): List<String> {
        val usersDocumentList: MutableList<String> = mutableListOf<String>()
        val firestore = fireStore

        // Get reference to the "Users" collection
        val usersCollection = firestore.collection("Users")

        // Query Firestore for documents in the "Users" collection
        usersCollection.get()
            .addOnSuccessListener { documents ->
                // Iterate through the documents and add their IDs to the list
                for (document in documents) {
                    val id = document.id
                    Log.d("TAG", "id = $id")
                    usersDocumentList.add(id)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
            }.await()

        return usersDocumentList
    }

    override suspend fun getRegions(): List<Region> {
        val regions = mutableListOf<Region>()
        try {
            val snapshot = fireStore.collection("Regions").get().await()
            for (document in snapshot.documents) {
                val docId = document.id
                val name = document.getString("name")
                regions.add(Region(docId = docId, name = name ?: ""))
            }
        } catch (exception: Exception) {
            Log.e("TAG", "Error getting regions: $exception")
        }
        return regions
    }

    override suspend fun getTowns(regionId: String): List<Town> {
        val towns = mutableListOf<Town>()
        try {
            val snapshot = fireStore.collection("Towns")
                .whereEqualTo("region_id", regionId).get().await()
            for (document in snapshot.documents) {
                val docId = document.id
                val name = document.getString("name")
                val town = Town(regionId = regionId, townId = docId, name = name ?: "")
                towns.add(town)
            }
        } catch (exception: Exception) {
            Log.e("TAG", "Error getting towns: $exception")
        }
        return towns
    }



}