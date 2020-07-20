package com.example.homehaven

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*


private lateinit var roomList:ListView
private lateinit var speechView:ImageView
private lateinit var roomNames:Vector<String>

class home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        roomList = findViewById(R.id.rooms_list)
        speechView = findViewById(R.id.voice_view)

        var db = dataStore(this)
        roomNames = db.getRoomNames()!!
        var roomsAdaptor = roomAdapter(this, roomNames)
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
            var roomIndex = -1
            var strRoomname = ""
            var strOnOff = ""
            var strDeviceName = ""
            for ((i, room) in roomNames.withIndex()){
                val roomName = room.replace('-',' ')
                var j = 0
                for (str in resString){
                    if (str.contains(roomName, true)) {
                        roomIndex = i
                        strRoomname = roomName
                    }
                    //doToast(str)
                    j++
                }
            }

            if(roomIndex != -1){
                //doToast(roomNames.get(roomIndex))
                var j = 0
                var light = false
                var fan = false
                var socket = false

                for (str in resString){
                    if (str.contains("light", true)) {
                        light = true
                        strDeviceName = "light"
                    }
                    if (str.contains("fan", true)) {
                        fan = true
                        strDeviceName = "fan"
                    }
                    if (str.contains("socket", true)) {
                        socket = true
                        strDeviceName = "socket"
                    }
                    //doToast(str)
                    j++
                }

                // ensures only one is true
                var confused = !((light xor socket xor fan) xor (light && fan && socket))

                if(!confused){
                    var on = false
                    var off = false
                    for (str in resString){
                        if (str.plus(" ").contains(" on ", true)) {
                            on = true
                            strOnOff = "on"
                        }
                        if (str.plus(" ").contains(" off ", true)) {
                            off = true
                            strOnOff = "off"
                        }
                        //doToast(str)
                        j++
                    }
                    confused = !(on xor off)

                    if (!confused){
                        doToast(strRoomname+" "+strDeviceName+" "+strOnOff)
                    }
                    else
                        doToast("Sorry unable to understand.(err1))")
                }
                else
                    doToast("Sorry unable to understand.(err0)")
            }
            else doToast("Room not found")
        }


    }
}