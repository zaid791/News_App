package com.example.newsapp.View.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.newsapp.databinding.FragmentWebViewBinding

class WebView : Fragment() {

    private lateinit var binding: FragmentWebViewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentWebViewBinding.inflate(inflater, container, false)

        initUi()
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun initUi() {
        val args = arguments
        if (args != null) {
            val url = args.getString("url")

            if (url != null) {
                binding.progressBar2.visibility = View.VISIBLE
                binding.webView.visibility = View.VISIBLE

                // Set WebViewClient to handle page loading progress
                binding.webView.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        // Page loading finished, hide progress bar
                        binding.progressBar2.visibility = View.GONE
                    }
                }

                // Load URL in WebView
                binding.webView.loadUrl(url)
            } else {
                Toast.makeText(requireContext(), "URL not present", Toast.LENGTH_SHORT).show()
            }
        }
    }
}