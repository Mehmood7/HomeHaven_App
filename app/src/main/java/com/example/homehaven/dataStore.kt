package com.example.homehaven

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class dataStore(context: Context) : SQLiteOpenHelper(context, "homehavenDB", null, 1) {
    private var friends =
        "CREATE TABLE ROOMS(id INTEGER PRIMARY KEY AUTOINCREMENT,room_index INTEGER,name TEXT," +
                "device_types TEXT, state INTEGER, );"
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