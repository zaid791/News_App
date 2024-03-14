package com.example.newsapp.View.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentArticleBinding
import com.squareup.picasso.Picasso

internal class ArticleFragment : Fragment() {

    private lateinit var binding: FragmentArticleBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentArticleBinding.inflate(inflater, container, false)
        initUI()
        return binding.root
    }

    private fun initUI() {
        val args = arguments
        if (args != null) {
            val head = args.getString("head")
            val content = args.getString("content")
            val imageUrl = args.getString("ImageUrl")
            val url = args.getString("url")

            val index = content?.indexOf("[")
            val truncatedContent = if (index != -1) {
                if (index != null) {
                    content.substring(0, index)
                } else {
                    content
                }
            } else {
                content
            }

            // Set data to the UI components

            binding.headlineTextView.text = head
            binding.tvContent.text = truncatedContent

            // Load image using Picasso or any other image loading library
            if (!imageUrl.isNullOrEmpty()) {
                Picasso.get().load(imageUrl).placeholder(R.drawable.news)
                    .into(binding.articleImageView)
            }

            binding.btnReadMore.setOnClickListener {
                val navController = findNavController()

                val args = Bundle().apply {
                    putString("url", url)
                }
                navController.navigate(R.id.webView2, args)
            }
        }
    }
}