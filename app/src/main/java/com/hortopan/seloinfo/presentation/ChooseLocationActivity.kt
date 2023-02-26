package com.hortopan.seloinfo.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.hortopan.seloinfo.data.RepositoryImpl
import com.hortopan.seloinfo.databinding.ActivityAuthorisationBinding
import com.hortopan.seloinfo.databinding.ActivityChooseLocationBinding
import com.hortopan.seloinfo.domain.entity.UserDataByGmailAuth
import com.hortopan.seloinfo.domain.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ChooseLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseLocationBinding
    private lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize dependencies
        repository = RepositoryImpl()


        CoroutineScope(Dispatchers.IO).launch {
            addNewUser()
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