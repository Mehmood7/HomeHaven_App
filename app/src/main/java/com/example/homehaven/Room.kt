package com.example.homehaven

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import java.io.IOException

class Room : AppCompatActivity() {

    private lateinit var title:TextView;
    private lateinit var dev1:View;
    private lateinit var dev2:View;
    private lateinit var dev3:View;
    private lateinit var dev4:View;
    private lateinit var dimdev:View;
    private lateinit var db:dataStore;
    private lateinit var roomObj:roomClass;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        title = findViewById(R.id.room_title)
        dev1 = findViewById(R.id.include1)
        dev2 = findViewById(R.id.include2)
        dev3 = findViewById(R.id.include3)
        dev4 = findViewById(R.id.include4)
        dimdev = findViewById(R.id.includedim)

        db = dataStore(applicationContext);
        val room_index = intent.extras!!.get("room_id") as Int
        roomObj = db.getRoomObj(room_index)

        title.text = roomObj.name

        val lightBitmap:Bitmap? = getBitmapFromAssets("icons/bulb.png")
        val fanBitmap:Bitmap? = getBitmapFromAssets("icons/fan.png")
        val plugBitmap:Bitmap? = getBitmapFromAssets("icons/plug.png")

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
}