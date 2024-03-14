package com.example.newsapp.ViewModel

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.Model.Article
import com.example.newsapp.R
import com.example.newsapp.Utils.DBHelper
import com.example.newsapp.View.fragments.ArticleFragment
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


class MyAdapter(
    private val context: Context,
    private val articlesArray: List<Article>,
    private val navController: NavController
) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return 10
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = articlesArray[position]
        val date = currentItem.publishedAt

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val parsedDate = LocalDateTime.parse(date, formatter)

        val outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.getDefault())
        val formattedDate = parsedDate.format(outputFormatter)

        holder.title.text = currentItem.title
        holder.date.text = formattedDate

        if (!currentItem.urlToImage.isNullOrEmpty()) {
            Picasso.get().load(currentItem.urlToImage).placeholder(R.drawable.news)
                .into(holder.image)
        } else {
            holder.image.setImageResource(R.drawable.news)
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
                    date ?: "",
                    currentItem.title ?: "",
                    formattedDate ?: "",
                    currentItem.urlToImage ?: "",
                    currentItem.content ?: "",
                    currentItem.url ?: ""
                )
                Toast.makeText(context, "Bookmark Added", Toast.LENGTH_SHORT).show()
            } else {
                holder.bookmark.setBackgroundResource(R.drawable.bookmark_disabled)
                db.deleteData(date)
                Toast.makeText(context, "Bookmark Removed", Toast.LENGTH_SHORT).show()
                bookmarkFlag = false
            }
        }

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
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ShapeableImageView
        var title: TextView
        var date: TextView
        var bookmark: Button

        init {
            image = itemView.findViewById(R.id.imageView)
            title = itemView.findViewById(R.id.tvTitle)
            date = itemView.findViewById(R.id.tvPublishDate)
            bookmark = itemView.findViewById(R.id.ivBookmark)
        }
    }
}