package com.example.homehaven

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class dataStore(context: Context) : SQLiteOpenHelper(context, "friendsDB", null, 1) {
    private var friends =
        "CREATE TABLE FRIENDS(f_id INTEGER PRIMARY KEY AUTOINCREMENT,f_s_id INTEGER,f_email TEXT,f_name TEXT);"
    private var db: SQLiteDatabase? = null

    init {
        db = writableDatabase
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(friends)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }


}