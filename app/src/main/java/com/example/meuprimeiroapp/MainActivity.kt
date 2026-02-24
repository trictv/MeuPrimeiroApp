package com.example.meuprimeiroapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meuprimeiroapp.adapter.ItemAdapter
import com.example.meuprimeiroapp.databinding.ActivityMainBinding
import com.example.meuprimeiroapp.model.Item
import com.example.meuprimeiroapp.service.RetrofitClient
import com.example.meuprimeiroapp.service.safeApiCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.meuprimeiroapp.service.Result
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var locationPermissionLauncher  : ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        requestLocationPermission()
    }

    override fun onResume() {
        super.onResume()
        fetItems()
    }

    private fun setupView(){
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetItems()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.addCta.setOnClickListener {

        }

    }

    private fun requestLocationPermission()
    {
        // Inicializa o FusedLocationPermission
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //configura o activity result launcher para obter a permissão de localização
        locationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        )
        { isGranted: Boolean ->
            if (isGranted){
                getLastLocation()
            } else {
                Toast.makeText(
                    this,
                    "Permissão de localização negada", Toast.LENGTH_SHORT).show()
            }
        }

        checkLocationPermissionAndRequest()
    }

    private fun checkLocationPermissionAndRequest()
    {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                getLastLocation()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION) -> {
                locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            else -> {
                locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }

        }
    }

    private fun getLastLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }

        fusedLocationClient.lastLocation.addOnCompleteListener { task: Task<Location> ->
            if (task.isSuccessful && task.result != null) {
                val location = task.result
                val latitude = location.latitude
                val longitude = location.longitude
                Toast.makeText(this, "Latitude: $latitude, Longitude: $longitude", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Não foi possível obter a localização", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun fetItems() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = safeApiCall { RetrofitClient.itemApiService.getItems()  }

            withContext(Dispatchers.Main){
                binding.swipeRefreshLayout.isRefreshing = false
                when(result){
                    is Result.Success -> {
                        val adapter = handleOnSuccess(result.data)
                    }
                    is Result.Error -> {
                        // Handle error
                    }
                }
            }
        }
    }

    private fun handleOnSuccess(items: List<Item>) {
        binding.recyclerView.adapter = ItemAdapter(items) { item ->
            val itent = ItemDetailActivity.newIntent(this, item.id)
            startActivity(itent)

        }
    }


}