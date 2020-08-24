package com.example.homehaven

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*


private lateinit var roomList:ListView
private lateinit var speechView:ImageView
private lateinit var roomNames:Vector<String>
private lateinit var db:dataStore
private lateinit var roomObj:roomClass
private lateinit var sharedPref: SharedPreferences
private var updateStr = ""

class home : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        roomList = findViewById(R.id.rooms_list)
        speechView = findViewById(R.id.voice_view)


        sharedPref = getSharedPreferences("prefs", Context.MODE_PRIVATE)?:return
        db = dataStore(applicationContext)

        roomNames = db.getRoomNames()!!
        var roomsAdaptor = roomAdapter(this, roomNames, Color.BLACK)
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
                        updateStr = strRoomname+" "+strDeviceName+" Turned "+strOnOff

                        var room_state = sharedPref.getInt("room${roomIndex}state", -1)
                        roomObj = db.getRoomObj(roomIndex)
                        if (roomIndex != -1) roomObj.updateState(room_state)
                        when(strDeviceName){
                            "light" ->{
                                roomObj.allTurn(roomObj.TYPE_LIGHT ,on)
                            }
                            "fan" ->{
                                roomObj.allTurn(roomObj.TYPE_FAN ,on)
                            }
                            "socket" ->{
                                roomObj.allTurn(roomObj.TYPE_SOCKET ,on)
                            }
                        }
                        room_state = roomObj.state

                        val email = sharedPref.getString("email", "")!!
                        val token = sharedPref.getString("token", "")!!
                        val params = "email=${email}&token=${token}&room_index=${roomIndex}&state=${room_state}"
                        setRoomState().execute(params)
                        with (sharedPref.edit()) {
                            putInt("room${roomIndex}state", room_state)
                            commit()
                        }

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


    inner class setRoomState : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg params: String): String? {
            val url: URL
            val httpURLConnection: HttpURLConnection
            val outputStreamWriter: OutputStreamWriter
            val inputStreamReader: InputStreamReader
            val bufferedReader: BufferedReader
            var stringFromServer: String
            try {
                url = URL("https://iothh.000webhostapp.com/api/setroomstate")
                httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.setRequestProperty(
                    "Content-Type",
                    "application/x-www-form-urlencoded"
                )
                outputStreamWriter = OutputStreamWriter(httpURLConnection.outputStream)
                outputStreamWriter.write(params[0])
                outputStreamWriter.flush()
                outputStreamWriter.close()
                inputStreamReader = InputStreamReader(httpURLConnection.inputStream)
                bufferedReader = BufferedReader(inputStreamReader)
                stringFromServer = bufferedReader.readLine()
                inputStreamReader.close()
                bufferedReader.close()
                return stringFromServer
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null

        }


        override fun onPostExecute(string: String?) {
            if (string == null) doToast("No Response") else {
                if (string == "ok"){
                    doToast(updateStr)
                }
                else{
                    doToast("Update failed.")
                }
            }

        }

    }


}