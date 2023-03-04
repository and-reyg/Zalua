package com.hortopan.seloinfo.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hortopan.seloinfo.R
import com.hortopan.seloinfo.data.RepositoryImpl
import com.hortopan.seloinfo.databinding.ActivityChooseLocationBinding
import com.hortopan.seloinfo.domain.entity.Regions
import com.hortopan.seloinfo.domain.entity.UserDataByGmailAuth
import com.hortopan.seloinfo.domain.repository.Repository
import kotlinx.coroutines.*


class ChooseLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseLocationBinding
    private lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("TAG", "ChooseSeloActivity Created")
        // Initialize dependencies
        repository = RepositoryImpl()

        //
        loadRegions()
        addNewUser()

    }
    private fun loadRegions() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                delay(5000)
                val regions = withContext(Dispatchers.IO) {
                    repository.getRegions()
                }
                Log.d("TAG", "Regions: $regions}")
                initSpinner(regions)
            } catch (exception: Exception) {
                Log.e("TAG", "Error getting regions: $exception")
            }
        }
    }

    private fun initSpinner(regions: List<Regions>) {
        val spinner = findViewById<Spinner>(R.id.spinner_choose_region)
        val regionNames = regions.map { it.name }
        val adapter = ArrayAdapter(this@ChooseLocationActivity, android.R.layout.simple_spinner_item, regionNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }


    private fun addNewUser() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                //delay(5000) // задержка на 5 секунд
                val userDataByGmail = withContext(Dispatchers.IO) {
                    repository.getUserData()
                }
                Log.d("TAG", "User name: ${userDataByGmail.name}")
                Log.d("TAG", "User email: ${userDataByGmail.email}")
                Log.d("TAG", "User id: ${userDataByGmail.id}")
                repository.saveUserData(userDataByGmail)
                Log.d("TAG", "Нового юзера додано")
                Toast.makeText(this@ChooseLocationActivity, "Нового юзера додано", Toast.LENGTH_SHORT).show()
            } catch (exception: Exception) {
                Log.e("TAG", "Error getting user documents: $exception")
            }
        }
    }
}