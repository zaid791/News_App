package com.example.newsapp.View.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.ViewModel.BookmarkAdapter
import com.example.newsapp.Model.BookmarkDataClass
import com.example.newsapp.Utils.DBHelper
import com.example.newsapp.databinding.FragmentBookmarkBinding

class Bookmark : Fragment() {

    private lateinit var binding: FragmentBookmarkBinding
    private lateinit var adapter: BookmarkAdapter
    private lateinit var dbHelper: DBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvBookmark.layoutManager = LinearLayoutManager(requireContext())


        dbHelper = DBHelper(requireContext(), null)

        val articleList = getArticleListFromDb()
        val navController = findNavController()


        adapter = BookmarkAdapter(articleList.toMutableList(), navController)

        binding.rvBookmark.adapter = adapter

    }

    private fun getArticleListFromDb(): List<BookmarkDataClass> {
        val cursor = dbHelper.getArticle()
        val articleList = mutableListOf<BookmarkDataClass>()
        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndexOrThrow(DBHelper.ID_COL))
                val title = it.getString(it.getColumnIndexOrThrow(DBHelper.TITLE_COL))
                val date = it.getString(it.getColumnIndexOrThrow(DBHelper.DATE_COL))
                val imageUrl = it.getString(it.getColumnIndexOrThrow(DBHelper.IMG_URL_COL))
                val content = it.getString(it.getColumnIndexOrThrow(DBHelper.CONTENT_COL))
                val url = it.getString(it.getColumnIndexOrThrow(DBHelper.URL_COL))
                val article = BookmarkDataClass(
                    id = id,
                    title = title,
                    date = date,
                    imageUrl = imageUrl,
                    content = content,
                    url = url
                )
                articleList.add(article)
            }
        }
        return articleList
    }
}