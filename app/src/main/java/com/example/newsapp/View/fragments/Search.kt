package com.example.newsapp.View.fragments

import com.example.newsapp.ViewModel.SearchAdapter
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.NetworkService.ApiService
import com.example.newsapp.Model.Article
import com.example.newsapp.Model.MyData
import com.example.newsapp.databinding.FragmentSearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Search : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchAdapter: SearchAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        initUi()

        setupRecyclerView()

        return binding.root
    }

    private fun initUi() {

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    searchNews(query)

                    hideKeyboard()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // You can perform actions as the text changes if needed
                return true
            }
        })
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun setupRecyclerView() {
        val navController = findNavController()
        // Initialize the adapter with an empty list
        searchAdapter = SearchAdapter(requireContext(), emptyList(), navController)

        // Set the adapter to the RecyclerView
        binding.recyclerView2.adapter = searchAdapter

        // Set the layout manager
        binding.recyclerView2.layoutManager = LinearLayoutManager(requireContext())
    }

    //    @OptIn(DelicateCoroutinesApi::class)
    private fun searchNews(query: String) {
        binding.searchProgressBar.visibility = View.VISIBLE

        val retrofit = Retrofit.Builder()
            .baseUrl("https://newsapi.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create an instance of the service interface
        val newsApiService = retrofit.create(ApiService::class.java)

        val call: Call<MyData> = newsApiService.searchForNews(query)

        call.enqueue(object : Callback<MyData> {
            override fun onResponse(call: Call<MyData>, response: Response<MyData>) {
                binding.searchProgressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val myData = response.body()
                    val articles = myData?.articles ?: emptyList()
                    updateAdapterWithData(articles)
                } else {
                    // Handle error response
                    // Example: showToast("Failed to fetch data")
                }
            }

            override fun onFailure(call: Call<MyData>, t: Throwable) {
                binding.searchProgressBar.visibility = View.GONE
                // Handle network errors
                // Example: showToast("Network error occurred")
            }
        })
    }


    private fun updateAdapterWithData(articles: List<Article>) {
        // Assuming you have a method in your adapter to update the dataset
        // and notify the adapter of changes
        searchAdapter.updateData(articles)
    }

}