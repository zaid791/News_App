package com.example.newsapp.View.fragments

import com.example.newsapp.ViewModel.SearchAdapter
import android.content.res.Resources
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.newsapp.ViewModel.MyAdapter
import com.example.newsapp.R
import com.example.newsapp.ViewModel.ViewPagerAdapter
import com.example.newsapp.NetworkService.ApiService
import com.example.newsapp.Model.Article
import com.example.newsapp.Model.MyData
import com.example.newsapp.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.abs

class Home : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    lateinit var myAdapter: MyAdapter
    private lateinit var searchAdapter: SearchAdapter

    private lateinit var pagerAdapter: ViewPagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var autoScrollRunnable: Runnable


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initUi()
        return binding.root
    }

    private fun Int.dpToPx(): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()
    }

    private fun setupTransformer(articlesArray: List<Article>){
        // Set page transformer to make the center image bigger
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer { page, position ->
            val scaleFactor = 0.85f // Adjust this scaling factor as needed
            val scaleFactorForSides = 1.0f // Keep the center image at its original size
            val offset = 16.dpToPx()

            val scaleFactorForCenter = scaleFactor + (1 - scaleFactor) * (1 - abs(position))
            page.scaleY = scaleFactorForCenter

            page.alpha = if (position < -1 || position > 1) 0f else 1f // Hide off-screen pages

            // Adjust translation for horizontal movement
            page.translationX = -offset * position
            page.translationZ = -1f // Ensure proper z-ordering

            // Scale the side images
            page.scaleY = scaleFactorForSides
        }
        viewPager.setPageTransformer(compositePageTransformer)


        // Set auto-scrolling
        autoScrollRunnable = Runnable {
            val currentItem = viewPager.currentItem
            viewPager.setCurrentItem((currentItem + 1) % articlesArray.size, true)
            viewPager.postDelayed(autoScrollRunnable, 3000) // Adjust the delay here (in milliseconds)
        }
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewPager.removeCallbacks(autoScrollRunnable)
                viewPager.postDelayed(autoScrollRunnable, 3000) // Adjust the delay here (in milliseconds)
            }
        })

        // Start auto-scrolling
        viewPager.postDelayed(autoScrollRunnable, 3000)
    }

    private fun initUi() {
        val navController = findNavController()
        // Initialize the adapter with an empty list
        searchAdapter = SearchAdapter(requireContext(), emptyList(), navController)

        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://newsapi.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        val retrofitData = retrofitBuilder.getHeadlines()

        retrofitData.enqueue(object : Callback<MyData?> {
            override fun onResponse(call: Call<MyData?>, response: Response<MyData?>) {
                val responseBody = response.body()
                val articlesArray = responseBody?.articles!!
                val navController = findNavController()

                viewPager = binding.viewPager

                pagerAdapter = ViewPagerAdapter(requireContext(), articlesArray, navController)
                binding.progressBar1.visibility = View.GONE
                binding.placeholderSpace.visibility = View.GONE


                binding.tvTopHeadlines.visibility = View.VISIBLE


                viewPager.adapter = pagerAdapter
                viewPager.offscreenPageLimit = 3

                setupTransformer(articlesArray)
            }

            override fun onFailure(call: Call<MyData?>, t: Throwable) {
                Log.d("ZAID", "onFailure: " + t.message)
                viewPager.removeCallbacks(autoScrollRunnable)
            }

        })

        val everythingData = retrofitBuilder.getEverything()
        everythingData.enqueue(object : Callback<MyData?>{
            override fun onResponse(call: Call<MyData?>, response: Response<MyData?>) {
                val responseBody = response.body()
                val articlesArray = responseBody?.articles!!
                val navController = findNavController()

                binding.progressBar2.visibility = View.GONE
                binding.tvAllNews.visibility = View.VISIBLE
                myAdapter = MyAdapter(requireContext(), articlesArray, navController)

                binding.recyclerView.adapter = myAdapter

                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            }

            override fun onFailure(call: Call<MyData?>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })

        val textViews = listOf(binding.tvBusi, binding.tvPoli, binding.tvTech, binding.tvScience)
        var selectedTextView: TextView? = null

        for (textView in textViews) {
            textView.setOnClickListener {
                // Reset previous selected TextView's style
                selectedTextView?.run {
                    setTextColor(ContextCompat.getColor(context, R.color.black))
                    paintFlags = paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
                }

                // Set current TextView as selected
                selectedTextView = textView

                // Apply new style to the selected TextView
                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.prima))
                textView.paintFlags = textView.paintFlags or Paint.UNDERLINE_TEXT_FLAG

                // Get the query and fetch articles
                val query = when (textView) {
                    binding.tvBusi -> "business"
                    binding.tvPoli -> "politics"
                    binding.tvTech -> "technology"
                    binding.tvScience -> "science"
                    else -> ""
                }
                getArticles(query)
            }
        }
    }

    private fun getArticles(query: String){
        binding.viewPager.visibility = View.GONE
        binding.tvTopHeadlines.visibility = View.GONE
        binding.tvAllNews.visibility = View.GONE
        binding.recyclerView.visibility = View.INVISIBLE
        binding.progressBar3.visibility = View.VISIBLE

        val retrofit = Retrofit.Builder()
            .baseUrl("https://newsapi.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create an instance of the service interface
        val newsApiService = retrofit.create(ApiService::class.java)

        val call: Call<MyData> = newsApiService.searchForNews(query)

        call.enqueue(object : Callback<MyData> {
            override fun onResponse(call: Call<MyData>, response: Response<MyData>) {
                binding.progressBar3.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                if (response.isSuccessful) {
                    val myData = response.body()
                    val articles = myData?.articles ?: emptyList()
                    searchAdapter.updateData(articles)

                    // Set the adapter to the RecyclerView
                    binding.recyclerView.adapter = searchAdapter

                    // Set the layout manager
                    binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                } else {
                    // Handle error response
                    // Example: showToast("Failed to fetch data")
                }
            }

            override fun onFailure(call: Call<MyData>, t: Throwable) {
                // Handle network errors
                //Example: showToast("Network error occurred")
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}