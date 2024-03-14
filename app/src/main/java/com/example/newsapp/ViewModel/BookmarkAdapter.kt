package com.example.newsapp.ViewModel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.Model.BookmarkDataClass
import com.example.newsapp.R
import com.example.newsapp.Utils.DBHelper
import com.example.newsapp.View.fragments.ArticleFragment
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso

class BookmarkAdapter(
    private val articleList: MutableList<BookmarkDataClass>,
    private val navController: NavController
) : RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {

    inner class BookmarkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDate: TextView = itemView.findViewById(R.id.tvPublishDate)
        val ivImage: ImageView = itemView.findViewById(R.id.imageView)
        val btnMark: MaterialButton = itemView.findViewById(R.id.ivBookmark)

        init {
            btnMark.setBackgroundResource(R.drawable.bookmark_filled)
            btnMark.setOnClickListener {
                val dbHelper = DBHelper(itemView.context, null)
                val article = articleList[adapterPosition]
                val deletedRows = dbHelper.deleteData(article.id)
                if (deletedRows > 0) {
                    // Entry deleted successfully
                    articleList.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                    Toast.makeText(itemView.context, "Bookmark Removed", Toast.LENGTH_SHORT).show()
                } else {
                    // No rows were deleted (entry with the given ID not found)
                    Toast.makeText(itemView.context, "Failed to delete entry", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return BookmarkViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val article = articleList[position]
        holder.textViewTitle.text = article.title
        holder.tvDate.text = article.date
        if (!article.imageUrl.isNullOrEmpty()) {
            Picasso.get().load(article.imageUrl).placeholder(R.drawable.news).into(holder.ivImage)
        } else {
            holder.ivImage.setImageResource(R.drawable.news)
        }

        holder.itemView.setOnClickListener {
            val fragment = ArticleFragment()

            val args = Bundle().apply {
                putString("head", article.title)
                putString("content", article.content)
                putString("ImageUrl", article.imageUrl)
                putString("url", article.url)
            }

            fragment.arguments = args

            // Replace the current fragment with the DetailsFragment
            navController.navigate(R.id.articleFragment, args)
        }
    }

    override fun getItemCount(): Int {
        return articleList.size
    }
}
