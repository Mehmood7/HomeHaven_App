package com.example.homehaven

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView

private lateinit var roomList:ListView ;

class home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        roomList = findViewById(R.id.rooms_list);

        var db = dataStore(this)
        var roomsAdaptor = roomAdapter(this, db.getRoomNames()!!)
        roomList.setAdapter(roomsAdaptor)
        roomList.setOnItemClickListener(
            object : AdapterView.OnItemClickListener {
                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    //val fid: Int = (roomsAdaptor.getItem(position)).server_id
                    //val chatIntent = Intent(this@FriendList, ChatBox::class.java)
                    //chatIntent.putExtra("fid", fid)
                    //startActivity(chatIntent)
                }
            })
    }
}