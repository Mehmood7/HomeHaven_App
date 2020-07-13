package com.example.homehaven

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class Room : AppCompatActivity() {

    private lateinit var dev1:View;
    private lateinit var dev2:View;
    private lateinit var dev3:View;
    private lateinit var dev4:View;
    private lateinit var dimdev:View;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        dev1 = findViewById(R.id.include1)
        dev2 = findViewById(R.id.include2)
        dev3 = findViewById(R.id.include3)
        dev4 = findViewById(R.id.include4)
        dimdev = findViewById(R.id.includedim)

        val room_index = intent.extras!!.get("room_id")
    }
}