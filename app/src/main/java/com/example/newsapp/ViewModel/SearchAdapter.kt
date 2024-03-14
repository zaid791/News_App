package com.example.newsapp.ViewModel

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.Model.Article
import com.example.newsapp.Utils.DBHelper
import com.example.newsapp.View.fragments.ArticleFragment
import com.squareup.picasso.Picasso
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class SearchAdapter(
    private val context: Context,
    private var articles: List<Article>,
    private val navController: NavController
) :
    RecyclerView.Adapter<SearchAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val image: ImageView = itemView.findViewById(R.id.imageView)
        var date: TextView = itemView.findViewById(R.id.tvPublishDate)
        var bookmark: Button = itemView.findViewById(R.id.ivBookmark)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return NewsViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentItem = articles[position]
        holder.textViewTitle.text = currentItem.title
        if (!currentItem.urlToImage.isNullOrEmpty()) {
            Picasso.get().load(currentItem.urlToImage).placeholder(R.drawable.news)
                .into(holder.image)
        } else {
            holder.image.setImageResource(R.drawable.news)
        }

        val date = currentItem.publishedAt

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val parsedDate = LocalDateTime.parse(date, formatter)

        val outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.getDefault())
        val formattedDate = parsedDate.format(outputFormatter)

        holder.date.text = formattedDate

        holder.itemView.setOnClickListener {
            val fragment = ArticleFragment()

            val args = Bundle().apply {
                putString("head", currentItem.title)
                putString("content", currentItem.content)
                putString("ImageUrl", currentItem.urlToImage)
                putString("url", currentItem.url)
            }

            fragment.arguments = args

            // Replace the current fragment with the DetailsFragment
            navController.navigate(R.id.articleFragment, args)
        }

        var bookmarkFlag = false
        val db = DBHelper(context, null)

        // Check if the article is bookmarked
        val isBookmarked = db.isArticleBookmarked(date)

        // Set the bookmark icon based on the bookmark status
        if (isBookmarked) {
            holder.bookmark.setBackgroundResource(R.drawable.bookmark_filled)
            bookmarkFlag = true
        } else {
            holder.bookmark.setBackgroundResource(R.drawable.bookmark_disabled)
        }

        holder.bookmark.setOnClickListener {
            // Toggle the button appearance between filled and outline
            if (!bookmarkFlag) {
                holder.bookmark.setBackgroundResource(R.drawable.bookmark_filled)
                bookmarkFlag = true
                db.addData(
                    date ?: "", // Use an empty string if date is null
                    currentItem.title ?: "", // Use an empty string if title is null
                    formattedDate ?: "", // Use an empty string if formattedDate is null
                    currentItem.urlToImage ?: "", // Use an empty string if urlToImage is null
                    currentItem.content ?: "", // Use an empty string if content is null
                    currentItem.url ?: ""// Use an empty string if url is null
                )

                Toast.makeText(context, "Bookmark Added", Toast.LENGTH_SHORT).show()
            } else {
                holder.bookmark.setBackgroundResource(R.drawable.bookmark_disabled)
                db.deleteData(date)
                Toast.makeText(context, "Bookmark Removed", Toast.LENGTH_SHORT).show()
                bookmarkFlag = false
            }
        }
    }

    override fun getItemCount() = articles.size

    fun updateData(newArticles: List<Article>) {
        articles = newArticles
        notifyDataSetChanged()
    }
}
