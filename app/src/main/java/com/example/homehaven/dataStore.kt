package com.example.homehaven

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*

class dataStore(context: Context) : SQLiteOpenHelper(context, "homehavenDB", null, 1) {
    private var friends =
        "CREATE TABLE ROOMS(id INTEGER PRIMARY KEY AUTOINCREMENT,room_index INTEGER,name TEXT," +
                "device_types TEXT, state INTEGER);"
    private var db: SQLiteDatabase? = null

    init {
        db = writableDatabase
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(friends)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun addRoom(room_index:Int, state:Int, name:String , types:String): Long {
        val tableName = "Rooms"
        val values = ContentValues()
        values.put("room_index", room_index)
        values.put("name", name)
        values.put("device_types", types)
        values.put("state", state)
        return db!!.insert(tableName, null, values)
    }

    fun clearRoom(){
        val tableName = "Rooms"
        db!!.execSQL("delete from "+ tableName);
    }

    fun getRoomNames(): Vector<String>? {
        val rooms: Vector<String> = Vector<String>()
        val roomCursor = db!!.rawQuery("SELECT name FROM ROOMS;", null)
        while (roomCursor.moveToNext()) {
            rooms.add(
                    roomCursor.getString(0)
            )
        }
        return rooms
    }

    fun getRoomObj(index: Int): roomClass{
        val roomCursor = db!!.rawQuery("SELECT * FROM ROOMS WHERE room_index = ${index};", null)

        if (roomCursor.moveToNext()) {
                return roomClass(roomCursor.getInt(1),roomCursor.getString(2),
                    roomCursor.getString(3),roomCursor.getInt(4))
        }
        return roomClass(0, "Room name", "00000", 0)
    }
}