package com.example.meuprimeiroapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()


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
            //val itent = ItemDetailActivity.newIntent(this, item)
           // startActivity(itent)

        }
    }


}