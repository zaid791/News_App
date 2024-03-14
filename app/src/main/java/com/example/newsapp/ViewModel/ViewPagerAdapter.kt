package com.example.newsapp.ViewModel

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.Model.Article
import com.example.newsapp.R
import com.example.newsapp.Utils.DBHelper
import com.example.newsapp.View.fragments.ArticleFragment
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ViewPagerAdapter(
    private val context: Context,
    private var articlesArray: List<Article>,
    private var navController: NavController
) : RecyclerView.Adapter<ViewPagerAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val photoImageView: ImageView = itemView.findViewById(R.id.photoImageView)
        val titleView : TextView = itemView.findViewById(R.id.titleTextView)
        val date : TextView = itemView.findViewById(R.id.tvPublishDate)
        val bookmark : MaterialButton = itemView.findViewById(R.id.ivBookmark)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.view_pager_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return articlesArray.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = articlesArray[position]

        val date = currentItem.publishedAt

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val parsedDate = LocalDateTime.parse(date, formatter)

        val outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.getDefault())
        val formattedDate = parsedDate.format(outputFormatter)

        holder.date.text = formattedDate

        val photo = currentItem.urlToImage
        val title = currentItem.title

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

        if (!title.isNullOrEmpty()){
            holder.titleView.text = title
        } else {
            holder.titleView.text = "Unavailable"
        }

        if (!photo.isNullOrEmpty()) {
            Picasso.get().load(photo).placeholder(R.drawable.news)
                .into(holder.photoImageView)
        } else {
            holder.photoImageView.setImageResource(R.drawable.news)
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
}