 package com.example.homehaven

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

 class MainActivity : AppCompatActivity() {

     private lateinit var loginbtn:Button
     private lateinit var emailtxt:EditText
     private lateinit var passwtxt:EditText
     private lateinit var emaillbl:TextView
     private lateinit var passwlbl:TextView
     private lateinit var sharedPref:SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.supportActionBar!!.hide()

        sharedPref = getSharedPreferences("prefs",Context.MODE_PRIVATE)?:return


        loginbtn = findViewById(R.id.login_btn)
        emailtxt = findViewById(R.id.email_et)
        passwtxt = findViewById(R.id.passw_et)
        emaillbl = findViewById(R.id.nav_mail_tv)
        passwlbl = findViewById(R.id.textView2)

        val email = sharedPref.getString("email", "")
        val token = sharedPref.getString("token", "")
        emailtxt.setText(email)

        if (email != "" && token != "") {
            val creds = "email=${email}&token=${token}"
            tokenLoginRequest().execute(creds);
            hide()
        }

        loginbtn.setOnClickListener {
            val creds = "email=${emailtxt.text}&password=${passwtxt.text}"
            with (sharedPref.edit()) {
                putString("email", "${emailtxt.text}")
                commit()
            }
            passwordLoginRequest().execute(creds);
        }

    }
     fun doToast(str:String){
         Toast.makeText(applicationContext,str,Toast.LENGTH_SHORT).show();
     }

     fun goToHome(){
         val homeScreen = Intent(
             this,
             drawerActivity::class.java
         )
         homeScreen.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
         startActivity(homeScreen)
     }

     fun hide(){
         emailtxt.visibility = View.INVISIBLE
         passwtxt.visibility = View.INVISIBLE
         loginbtn.visibility = View.INVISIBLE
         emaillbl.visibility = View.INVISIBLE
         passwlbl.visibility = View.INVISIBLE
     }

     fun show(){
         emailtxt.visibility = View.VISIBLE
         passwtxt.visibility = View.VISIBLE
         loginbtn.visibility = View.VISIBLE
         emaillbl.visibility = View.VISIBLE
         passwlbl.visibility = View.VISIBLE
     }

     inner class passwordLoginRequest : AsyncTask<String,String, String>() {
         override fun doInBackground(vararg params: String): String? {
             val url: URL
             val httpURLConnection: HttpURLConnection
             val outputStreamWriter: OutputStreamWriter
             val inputStreamReader: InputStreamReader
             val bufferedReader: BufferedReader
             var stringFromServer: String
             try {
                 url = URL("https://homehaven.website/api/applogin")
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
                 val code = string.substring(0,2);
                 when (code) {
                     "id" -> doToast("Login Failed")
                     "ok" -> {
                         doToast("Login Successful")
                         val token = string.substring(2,14)
                         val room_count = string.get(14).toInt()-48
                         var rooms = string.substringAfter(':').split(':');
                         val name = rooms.last()
                         with(sharedPref.edit()) {
                             putString("token", token)
                             putString("name", name)
                             commit()
                         }
                         rooms = rooms.dropLast(1)
                         val db = dataStore(applicationContext);
                         db.clearRoom()
                         for (room in rooms){
                             val name = room.substring(9)
                             val index = room.get(0).toInt()-48
                             val types = room.substring(1,6)
                             val state = room.substring(6,9).toInt()
                             db.addRoom(index, state, name, types)
                         }
                         goToHome()
                     }
                     else -> doToast("Unknown Error :" + string)

                 }
             }

         }
     }

     inner class tokenLoginRequest : AsyncTask<String,String, String>() {
         override fun doInBackground(vararg params: String): String? {
             val url: URL
             val httpURLConnection: HttpURLConnection
             val outputStreamWriter: OutputStreamWriter
             val inputStreamReader: InputStreamReader
             val bufferedReader: BufferedReader
             var stringFromServer: String
             try {
                 url = URL("https://homehaven.website/api/verifytoken")
                 httpURLConnection = url.openConnection() as HttpURLConnection
                 httpURLConnection.requestMethod = "POST"
                 httpURLConnection.setRequestProperty(
                     "Content-Type",
                     "application/x-www-form-urlencoded"
                 )
                 httpURLConnection.connectTimeout = 1000
                 httpURLConnection.readTimeout = 1000
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
             if (string == null) {
                 doToast("Login Failed")

                 val failedCount = sharedPref.getInt("failedcount", 0)
                 with (sharedPref.edit()) {
                     putInt("failedcount", failedCount+1)
                     commit()
                 }

                 if (failedCount < 5)
                     goToHome()
                 else
                     doToast("Please try again")
             } else {
                 when (string) {
                     "invalid" -> {
                         doToast("Please Login")
                         show()
                     }
                     "ok" -> {
                         doToast("Login Successful")
                         goToHome()
                     }
                     else -> {
                         doToast("Unknown Error :" + string)
                         show()
                     }

                 }
             }

         }
     }

 }


