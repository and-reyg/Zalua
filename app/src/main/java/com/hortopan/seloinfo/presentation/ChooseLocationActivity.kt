package com.hortopan.seloinfo.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hortopan.seloinfo.R
import com.hortopan.seloinfo.data.RepositoryImpl
import com.hortopan.seloinfo.databinding.ActivityChooseLocationBinding
import com.hortopan.seloinfo.domain.repository.Repository
import kotlinx.coroutines.*


class ChooseLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseLocationBinding
    private lateinit var repository: Repository
    private var selectedRegionId: String = ""
    private var selectedTownId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("TAG", "ChooseSeloActivity Created")
        // Initialize dependencies
        repository = RepositoryImpl()

        //
        loadRegions()
        showButton()
        onClickButton()

    }
    private fun loadRegions() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                //delay(5000)
                val regions = withContext(Dispatchers.IO) {
                    repository.getRegions().map { Pair(it.docId, it.name) }
                }
                Log.d("TAG", "Regions: $regions}")
                initSpinnerRegion(regions)
            } catch (exception: Exception) {
                Log.e("TAG", "Error getting regions: $exception")
            }
        }
    }

    private fun initSpinnerRegion(regions: List<Pair<String, String>>) {
        val spinner = binding.spinnerChooseRegion
        val adapter = ArrayAdapter(this@ChooseLocationActivity,
            android.R.layout.simple_spinner_item,
            regions.map { it.second })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Устанавливаем значение по умолчанию
        val defaultRegion = "Київська"
        val defaultRegionIndex = regions.indexOfFirst { it.second == defaultRegion }
        spinner.setSelection(defaultRegionIndex)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedRegion = regions[position]
                selectedRegionId = selectedRegion.first
                Log.d("TAG", "Selected region: ${selectedRegion.second} (id: ${selectedRegion.first})")
               /* Toast.makeText(this@ChooseLocationActivity,
                    "Selected region: ${selectedRegion.second} " +
                            "(id: ${selectedRegion.first})", Toast.LENGTH_SHORT).show()
*/
                loadTowns(selectedRegionId)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Ничего не делаем
            }
        }
    }

    private fun initSpinnerTown(towns: List<Pair<String, String>>) {
        val spinner = binding.spinnerChooseTown
        val adapter = ArrayAdapter(
            this@ChooseLocationActivity,
            android.R.layout.simple_spinner_item,
            towns.map { it.second })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedTown = towns[position]
                selectedTownId = selectedTown.first
                Log.d("TAG", "Selected region: ${selectedTown.second} (id: ${selectedTown.first})")
               /* Toast.makeText(this@ChooseLocationActivity,
                    "Selected region: ${selectedTown.second} " +
                            "(id: ${selectedTown.first})", Toast.LENGTH_SHORT).show()*/
                binding.btnNext.isEnabled = true

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Ничего не делаем
            }
        }
    }

    private fun loadTowns(selectedRegionId: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                //delay(5000)
                val towns = withContext(Dispatchers.IO) {
                    repository.getTowns(selectedRegionId).map { Pair(it.townId, it.name) }
                }
                Log.d("TAG", "Towns: $towns}")
                initSpinnerTown(towns)
            } catch (exception: Exception) {
                Log.e("TAG", "Error getting regions: $exception")
            }
        }
    }


    private fun addNewUser(selectedTownId: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                //delay(5000) // задержка на 5 секунд
                val userDataByGmail = withContext(Dispatchers.IO) {
                    repository.getUserData()
                }
                Log.d("TAG", "User name: ${userDataByGmail.name}")
                Log.d("TAG", "User email: ${userDataByGmail.email}")
                Log.d("TAG", "User id: ${userDataByGmail.id}")
                repository.saveUserData(userData =  userDataByGmail, selectedTownId =  selectedTownId)
                Log.d("TAG", "Нового юзера додано")
                Toast.makeText(this@ChooseLocationActivity, "Нового юзера додано", Toast.LENGTH_SHORT).show()

                openMainActivity()
            } catch (exception: Exception) {
                Log.e("TAG", "Error getting user documents: $exception")
                Toast.makeText(this@ChooseLocationActivity,
                    "Failed to add new user", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showButton(){
        CoroutineScope(Dispatchers.Main).launch {
            delay(4000)
            binding.btnNext.visibility = View.VISIBLE
        }
    }

    private fun onClickButton() {
        binding.btnNext.setOnClickListener {
            addNewUser(selectedTownId)
        }
    }

    private fun openMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}