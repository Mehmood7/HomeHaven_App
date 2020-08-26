package com.example.homehaven
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.homehaven.ui.device_control.homeGallery
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class drawerActivity : AppCompatActivity(), homeGallery {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var db:dataStore
    private lateinit var roomNames:Vector<String>
    private lateinit var roomObj:roomClass
    private lateinit var sharedPref: SharedPreferences
    private lateinit var dpView: ImageView
    private lateinit var nameView: TextView
    private lateinit var mailView: TextView
    private lateinit var signOut: MenuItem
    private var updateStr = ""

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.title != null) doToast(item.title.toString())
//        return super.onOptionsItemSelected(item)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            //        .setAction("Action", null).show()
            voiceCommand()

        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val hView =  navView.getHeaderView(0)
        dpView = hView.findViewById(R.id.nav_dp_iv)
        nameView = hView.findViewById(R.id.nav_name_tv)
        mailView = hView.findViewById(R.id.nav_mail_tv)
        val menuNav:Menu = navView.getMenu();
        signOut = menuNav.findItem(R.id.nav_sign_out)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_usage_and_prediction, R.id.nav_device_control, R.id.nav_night_mode_config, R.id.nav_settings), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        sharedPref = getSharedPreferences("prefs", Context.MODE_PRIVATE)?:return
        db = dataStore(applicationContext)

        roomNames = db.getRoomNames()!!

        val email = sharedPref.getString("email", "")
        val name = sharedPref.getString("name", "")

        val imageUri = "https://homehaven.website/api/getimage?email=" + email
        Picasso.get().isLoggingEnabled = true
        Picasso.get().load(imageUri).resize(150, 150)
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .transform(PicassoCircleTransformation())
            .centerCrop().into(dpView)

        mailView.text = email
        nameView.text = name

        signOut.setOnMenuItemClickListener {
            with (sharedPref.edit()) {
                putString("token", "")
                commit()
            }
            val i = Intent(this, MainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            true
        }

    }

    override fun getDatastore(): dataStore {
        return db;
    }

    override fun getSharedPref(): SharedPreferences {
        return sharedPref;
    }

    override fun doToast(str:String){
        Toast.makeText(applicationContext,str, Toast.LENGTH_SHORT).show();
    }
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.drawer, menu)
//        return true
//    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
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
                url = URL("https://homehaven.website/api/setroomstate")
                httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.setRequestProperty(
                    "Content-Type",
                    "application/x-www-form-urlencoded"
                )
                httpURLConnection.connectTimeout = 4000
                httpURLConnection.readTimeout = 3000
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