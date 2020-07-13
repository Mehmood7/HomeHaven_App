 package com.example.homehaven

import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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

 class MainActivity : AppCompatActivity() {

     private lateinit var loginbtn:Button
     private lateinit var emailtxt:EditText
     private lateinit var passwtxt:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.supportActionBar!!.hide()


        loginbtn = findViewById(R.id.login_btn)
        emailtxt = findViewById(R.id.email_et)
        passwtxt = findViewById(R.id.passw_et)

        loginbtn.setOnClickListener {
            val creds = "email=${emailtxt.text}&password=${passwtxt.text}"
            loginRequest().execute(creds);
        }

    }
     inner class loginRequest : AsyncTask<String,String, String>() {
         override fun doInBackground(vararg params: String): String? {
             val url: URL
             val httpURLConnection: HttpURLConnection
             val outputStreamWriter: OutputStreamWriter
             val inputStreamReader: InputStreamReader
             val bufferedReader: BufferedReader
             var stringFromServer: String
             try {
                 url = URL("https://iothh.000webhostapp.com/api/gettoken")
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
                 val code = string.first() + "" +string.last();
                 when (code) {
                     "id" -> doToast("Login Failed")
                     "ok" -> doToast("Login Successful")
                     else -> doToast("Unknown Error :" + string)

                 }
             }

         }
         fun doToast(str:String){
             Toast.makeText(applicationContext,str,Toast.LENGTH_SHORT).show();
         }

     }

 }


