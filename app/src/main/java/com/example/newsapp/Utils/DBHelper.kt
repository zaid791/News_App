package com.example.newsapp.Utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {

        val query =
            ("CREATE TABLE $TABLE_NAME ($ID_COL TEXT PRIMARY KEY, $TITLE_COL TEXT, $DATE_COL TEXT, $IMG_URL_COL TEXT, $CONTENT_COL TEXT, $URL_COL TEXT)")

        db?.execSQL(query)
        if (db != null) {
            Log.d("ZAID", db.path)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addData(
        id: String,
        title: String,
        date: String,
        imgUrl: String,
        content: String,
        url: String
    ) {

        val values = ContentValues()

        values.put(ID_COL, id)
        values.put(TITLE_COL, title)
        values.put(DATE_COL, date)
        values.put(IMG_URL_COL, imgUrl)
        values.put(CONTENT_COL, content)
        values.put(URL_COL, url)

        val db = this.writableDatabase

        db.insert(TABLE_NAME, null, values)

        db.close()

    }

    fun getArticle(): Cursor? {
        val db = this.readableDatabase

        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    fun deleteData(id: String): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NAME, "$ID_COL = ?", arrayOf(id))
        db.close()
        return result
    }

    fun isArticleBookmarked(date: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $ID_COL=?", arrayOf(date))
        val isBookmarked = cursor.count > 0
        cursor.close()
        db.close()
        return isBookmarked
    }


    companion object {
        private val DATABASE_NAME = "article_bookmarks"

        private val DATABASE_VERSION = 1

        val TABLE_NAME = "my_table"

        val ID_COL = "id"

        val TITLE_COL = "title"

        val DATE_COL = "date"

        val IMG_URL_COL = "image"

        val CONTENT_COL = "content"

        val URL_COL = "url"
    }

}