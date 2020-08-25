package com.example.homehaven

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class Room : AppCompatActivity() {

    private lateinit var title:TextView
    private lateinit var dev1:View
    private lateinit var dev2:View
    private lateinit var dev3:View
    private lateinit var dev4:View
    private lateinit var dimdev:View
    private lateinit var db:dataStore
    private lateinit var roomObj:roomClass
    private lateinit var roomLayout: ConstraintLayout
    private lateinit var sharedPref:SharedPreferences
    private var room_index = 0
    private var room_state = 0
    private var email = ""
    private var token = ""
    private var night_mode = false


    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)
        room_index = intent.extras!!.get("room_id") as Int
        night_mode = intent.extras!!.get("night_mode") as Boolean

        title = findViewById(R.id.room_title)
        dev1 = findViewById(R.id.include1)
        dev2 = findViewById(R.id.include2)
        dev3 = findViewById(R.id.include3)
        dev4 = findViewById(R.id.include4)
        dimdev = findViewById(R.id.includedim)
        roomLayout = findViewById(R.id.room_constarint_LO)

        sharedPref = getSharedPreferences("prefs", Context.MODE_PRIVATE)?:return
        if (night_mode)  room_state = sharedPref.getInt("night_room${room_index}state", -1)
        else room_state = sharedPref.getInt("room${room_index}state", -1)
        if (room_state > 255 || room_state < 0) room_state = 0;

        db = dataStore(applicationContext)

        roomObj = db.getRoomObj(room_index)
        if (room_state != -1) roomObj.updateState(room_state)

        title.text = roomObj.name

        if (night_mode){
            title.setBackgroundColor(resources.getColor(R.color.night_dark))
            roomLayout.setBackgroundColor(resources.getColor(R.color.night_light))
        }

        email = sharedPref.getString("email", "")!!
        token = sharedPref.getString("token", "")!!
        val params = "email=${email}&token=${token}&room_index=${room_index}"
        getRoomState().execute(params);

        enable(false)
        setState()
        setRoom()

    }

    fun enable(en:Boolean){
        dev1.findViewById<Switch>(R.id.on_off_switch).isEnabled = en
        dev2.findViewById<Switch>(R.id.on_off_switch).isEnabled = en
        dev3.findViewById<Switch>(R.id.on_off_switch).isEnabled = en
        dev4.findViewById<Switch>(R.id.on_off_switch).isEnabled = en
        dimdev.findViewById<Switch>(R.id.on_off_switch).isEnabled = en
        dimdev.findViewById<SeekBar>(R.id.dim_seekBar).isEnabled = en
    }

    fun setState(){
        //doToast(""+roomObj.state+roomObj.device1_state+(roomObj.state and 128))
        dev1.findViewById<Switch>(R.id.on_off_switch).isChecked = roomObj.device1_state
        dev2.findViewById<Switch>(R.id.on_off_switch).isChecked = roomObj.device2_state
        dev3.findViewById<Switch>(R.id.on_off_switch).isChecked = roomObj.device3_state
        dev4.findViewById<Switch>(R.id.on_off_switch).isChecked = roomObj.device4_state
        dimdev.findViewById<Switch>(R.id.on_off_switch).isChecked = roomObj.devicedim_state
        dimdev.findViewById<SeekBar>(R.id.dim_seekBar).progress = roomObj.devicedim_level
    }

    fun addListensers(){
        dev1.findViewById<Switch>(R.id.on_off_switch).setOnCheckedChangeListener(
            CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) room_state += 128
                else room_state -=128
                roomObj.updateState(room_state)
                val params = "email=${email}&token=${token}&room_index=${room_index}&state=${room_state}"
                setRoomState().execute(params)
                with (sharedPref.edit()) {
                    if (night_mode) putInt("night_room${room_index}state", room_state)
                    else putInt("room${room_index}state", room_state)
                    commit()
                }
                //doToast(""+room_state)
            }
        )
        dev2.findViewById<Switch>(R.id.on_off_switch).setOnCheckedChangeListener(
            CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) room_state += 64
                else room_state -= 64
                roomObj.updateState(room_state)
                val params = "email=${email}&token=${token}&room_index=${room_index}&state=${room_state}"
                setRoomState().execute(params)
                with (sharedPref.edit()) {
                    if (night_mode) putInt("night_room${room_index}state", room_state)
                    else putInt("room${room_index}state", room_state)
                    commit()
                }
                //doToast(""+room_state)
            }
        )
        dev3.findViewById<Switch>(R.id.on_off_switch).setOnCheckedChangeListener(
            CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) room_state += 32
                else room_state -= 32
                roomObj.updateState(room_state)
                val params = "email=${email}&token=${token}&room_index=${room_index}&state=${room_state}"
                setRoomState().execute(params)
                with (sharedPref.edit()) {
                    if (night_mode) putInt("night_room${room_index}state", room_state)
                    else putInt("room${room_index}state", room_state)
                    commit()
                }
                //doToast(""+room_state)
            }
        )
        dev4.findViewById<Switch>(R.id.on_off_switch).setOnCheckedChangeListener(
            CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) room_state += 16
                else room_state -= 16
                roomObj.updateState(room_state)
                val params = "email=${email}&token=${token}&room_index=${room_index}&state=${room_state}"
                setRoomState().execute(params)
                with (sharedPref.edit()) {
                    if (night_mode) putInt("night_room${room_index}state", room_state)
                    else putInt("room${room_index}state", room_state)
                    commit()
                }
                //doToast(""+room_state)
            }
        )
        dimdev.findViewById<Switch>(R.id.on_off_switch).setOnCheckedChangeListener(
            CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) room_state += 8
                else room_state -= 8
                roomObj.updateState(room_state)
                val params = "email=${email}&token=${token}&room_index=${room_index}&state=${room_state}"
                setRoomState().execute(params)
                with (sharedPref.edit()) {
                    if (night_mode) putInt("night_room${room_index}state", room_state)
                    else putInt("room${room_index}state", room_state)
                    commit()
                }
                //doToast(""+room_state)
            }
        )
        dimdev.findViewById<SeekBar>(R.id.dim_seekBar).setOnSeekBarChangeListener( object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                room_state = (room_state and 248)+ seekBar.progress
                roomObj.updateState(room_state)
                val params = "email=${email}&token=${token}&room_index=${room_index}&state=${room_state}"
                setRoomState().execute(params)
                with (sharedPref.edit()) {
                    if (night_mode) putInt("night_room${room_index}state", room_state)
                    else putInt("room${room_index}state", room_state)
                    commit()
                }
                //doToast(""+room_state)
            }
        }
        )
    }

    fun setRoom(){

        val lightBitmap:Bitmap? = getBitmapFromAssets("icons/bulb.png")
        val fanBitmap:Bitmap? = getBitmapFromAssets("icons/fan.png")
        val plugBitmap:Bitmap? = getBitmapFromAssets("icons/socket.png")

        when(roomObj.device1_type){
            1->{
                dev1.findViewById<TextView>(R.id.device_name).text = "Light"
                dev1.findViewById<ImageView>(R.id.device_img).setImageBitmap(lightBitmap)
            }
            2->{
                dev1.findViewById<TextView>(R.id.device_name).text = "Fan"
                dev1.findViewById<ImageView>(R.id.device_img).setImageBitmap(fanBitmap)
            }
            3->{
                dev1.findViewById<TextView>(R.id.device_name).text = "Plug"
                dev1.findViewById<ImageView>(R.id.device_img).setImageBitmap(plugBitmap)
            }
            else -> dev1.visibility = View.GONE
        }
        when(roomObj.device2_type){
            1->{
                dev2.findViewById<TextView>(R.id.device_name).text = "Light"
                dev2.findViewById<ImageView>(R.id.device_img).setImageBitmap(lightBitmap)
            }
            2->{
                dev2.findViewById<TextView>(R.id.device_name).text = "Fan"
                dev2.findViewById<ImageView>(R.id.device_img).setImageBitmap(fanBitmap)
            }
            3->{
                dev2.findViewById<TextView>(R.id.device_name).text = "Plug"
                dev2.findViewById<ImageView>(R.id.device_img).setImageBitmap(plugBitmap)
            }
            else -> dev2.visibility = View.GONE
        }
        when(roomObj.device3_type){
            1->{
                dev3.findViewById<TextView>(R.id.device_name).text = "Light"
                dev3.findViewById<ImageView>(R.id.device_img).setImageBitmap(lightBitmap)
            }
            2->{
                dev3.findViewById<TextView>(R.id.device_name).text = "Fan"
                dev3.findViewById<ImageView>(R.id.device_img).setImageBitmap(fanBitmap)
            }
            3->{
                dev3.findViewById<TextView>(R.id.device_name).text = "Plug"
                dev3.findViewById<ImageView>(R.id.device_img).setImageBitmap(plugBitmap)
            }
            else -> dev3.visibility = View.GONE
        }
        when(roomObj.device4_type){
            1->{
                dev4.findViewById<TextView>(R.id.device_name).text = "Light"
                dev4.findViewById<ImageView>(R.id.device_img).setImageBitmap(lightBitmap)
            }
            2->{
                dev4.findViewById<TextView>(R.id.device_name).text = "Fan"
                dev4.findViewById<ImageView>(R.id.device_img).setImageBitmap(fanBitmap)
            }
            3->{
                dev4.findViewById<TextView>(R.id.device_name).text = "Plug"
                dev4.findViewById<ImageView>(R.id.device_img).setImageBitmap(plugBitmap)
            }
            else -> dev4.visibility = View.GONE
        }
        when(roomObj.devicedim_type){
            1->{
                dimdev.findViewById<TextView>(R.id.device_name).text = "Light"
                dimdev.findViewById<ImageView>(R.id.device_img).setImageBitmap(lightBitmap)
            }
            2->{
                dimdev.findViewById<TextView>(R.id.device_name).text = "Fan"
                dimdev.findViewById<ImageView>(R.id.device_img).setImageBitmap(fanBitmap)
            }
            3->{
                dimdev.findViewById<TextView>(R.id.device_name).text = "Plug"
                dimdev.findViewById<ImageView>(R.id.device_img).setImageBitmap(plugBitmap)
            }
            else -> dimdev.visibility = View.GONE
        }

    }

    fun doToast(str:String){
        Toast.makeText(applicationContext,str, Toast.LENGTH_SHORT).show();
    }

    private fun getBitmapFromAssets(fileName: String): Bitmap? {
        return try {
            BitmapFactory.decodeStream(assets.open(fileName))
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    inner class getRoomState : AsyncTask<String,String, String>() {
        override fun doInBackground(vararg params: String): String? {
            val url: URL
            val httpURLConnection: HttpURLConnection
            val outputStreamWriter: OutputStreamWriter
            val inputStreamReader: InputStreamReader
            val bufferedReader: BufferedReader
            var stringFromServer: String
            try {
                if(night_mode) url = URL("https://homehaven.website/api/getnightroomstate")
                else url = URL("https://homehaven.website/api/getroomstate")
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
                if (string.length == 3){
                    val code = string.toInt();
                    if (code==-10) doToast("State fetch Failed")
                    else if (code >= 0 && code <256 ) {
                        //doToast("State  :" + string)
                        room_state = code
                        roomObj.updateState(code)
                        setState()
                        with (sharedPref.edit()) {
                            if (night_mode) putInt("night_room${room_index}state", code)
                            else putInt("room${room_index}state", code)
                            commit()
                        }
                    }
                    else doToast("false state :"+code)

                }
                else{
                    doToast("Invalid state.")
                }
            }
            enable(true)
            addListensers()

        }

    }
    inner class setRoomState : AsyncTask<String,String, String>() {
        override fun doInBackground(vararg params: String): String? {
            val url: URL
            val httpURLConnection: HttpURLConnection
            val outputStreamWriter: OutputStreamWriter
            val inputStreamReader: InputStreamReader
            val bufferedReader: BufferedReader
            var stringFromServer: String
            try {
                if(night_mode) url = URL("https://homehaven.website/api/setnightroomstate")
                else url = URL("https://homehaven.website/api/setroomstate")
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
                    doToast("Updated")
                }
                else{
                    doToast("Update failed.")
                }
            }

        }

    }
}