package com.hortopan.seloinfo.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hortopan.seloinfo.data.RepositoryImpl
import com.hortopan.seloinfo.databinding.ActivityAuthorisationBinding
import com.hortopan.seloinfo.databinding.ActivityChooseLocationBinding
import com.hortopan.seloinfo.domain.entity.UserDataByGmailAuth
import com.hortopan.seloinfo.domain.entity.UsersDocuments
import com.hortopan.seloinfo.domain.repository.Repository
import kotlinx.coroutines.*


class ChooseLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseLocationBinding
    private lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize dependencies
        repository = RepositoryImpl()
        //isUserInDatabase()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Use withContext to run the repository method in a background thread
                val usersDocumentList = withContext(Dispatchers.IO) {
                    repository.getUsersDocumentsID()
                }
                Log.d("TAG", "Users document IDs: $usersDocumentList")

            } catch (exception: Exception) {
                Log.e("TAG", "Error getting user documents: $exception")
            }
        }


    }

    //Перевіри чи є юзер в базі

    private fun isUserInDatabase() {
        // Initialize Firestore
        val firestore = Firebase.firestore

        // Get reference to the "Users" collection
        val usersCollection = firestore.collection("Users")

        // Query Firestore for documents in the "Users" collection
        usersCollection.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("TAG", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
            }
    }

    private suspend fun getUserDataByGmail(): UserDataByGmailAuth {
        val userData = repository.getUserData()
        Log.d("TAG", userData.toString())
        return userData
    }
    private suspend fun addNewUser(){
        val userDataByGmail = getUserDataByGmail()
        // Print user data to logs
        Log.d("TAG", "User name: ${userDataByGmail.name}")
        Log.d("TAG", "User email: ${userDataByGmail.email}")
        Log.d("TAG", "User id: ${userDataByGmail.id}")
        repository.saveUserData(userDataByGmail)
    }
}