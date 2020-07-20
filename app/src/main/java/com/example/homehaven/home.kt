package com.example.homehaven

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.*
import java.util.*

private lateinit var roomList:ListView
private lateinit var speechView:ImageView

class home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        roomList = findViewById(R.id.rooms_list)
        speechView = findViewById(R.id.voice_view)

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
                    val roomIntent = Intent(applicationContext, Room::class.java)
                    roomIntent.putExtra("room_id", position)
                    startActivity(roomIntent)
                }
            })
        speechView.setOnClickListener(View.OnClickListener {
            voiceCommand()
        })
    }

    fun doToast(str:String){
        Toast.makeText(applicationContext,str, Toast.LENGTH_SHORT).show();
    }

    fun voiceCommand(){
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        // Required extra
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(packageManager) != null)
            startActivityForResult(intent, 72);
        else
            doToast("Speech not supported on this device.")


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == 72  && data != null){
            val resString = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            for (str in resString){
                doToast(str)
            }
        }


    }
}