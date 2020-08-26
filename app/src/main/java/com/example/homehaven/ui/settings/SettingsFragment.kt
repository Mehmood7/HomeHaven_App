package com.example.homehaven.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.homehaven.R
import com.example.homehaven.ui.device_control.homeGallery
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class SettingsFragment : Fragment() {

  private lateinit var mhomeGallery: homeGallery
  private lateinit var settingsViewModel: SettingsViewModel
  private lateinit var nightModeSwitch: Switch
  private lateinit var autoModeSwitch: Switch
  private lateinit var securityModeSwitch: Switch
  private lateinit var offlineModeSwitch: Switch
  private lateinit var tipsSwitch: Switch
  private lateinit var sharedPref: SharedPreferences
  private var night_mode_enabled = false
  private var auto_mode_enabled = false
  private var offline_mode_enabled = false
  private var security_mode_enabled = false
  private var tips_enabled = false

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    settingsViewModel =
    ViewModelProviders.of(this).get(SettingsViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_settings, container, false)


    nightModeSwitch = root.findViewById(R.id.setting_Ni_Mode_switch)
    autoModeSwitch = root.findViewById(R.id.setting_Au_Mode_switch)
    securityModeSwitch = root.findViewById(R.id.setting_Se_Mode_switch)
    offlineModeSwitch = root.findViewById(R.id.setting_Of_Mode_switch)
    tipsSwitch = root.findViewById(R.id.setting_Tips_switch)

    sharedPref = mhomeGallery.getSharedPref()

    readPref()

    val email = sharedPref.getString("email", "")
    val token = sharedPref.getString("token", "")
    val params = "email=${email}&token=${token}"
    getSettings().execute(params);
    enable(false)
    return root
  }
  fun enable(en: Boolean){
    nightModeSwitch.isEnabled = en
    autoModeSwitch.isEnabled = en
    securityModeSwitch.isEnabled = en
    offlineModeSwitch.isEnabled = en
    tipsSwitch.isEnabled = en
  }

  fun readPref(){
    night_mode_enabled = sharedPref.getBoolean("night_enabled",false)
    auto_mode_enabled = sharedPref.getBoolean("auto_enabled",false)
    security_mode_enabled = sharedPref.getBoolean("security_enabled",false)
    tips_enabled = sharedPref.getBoolean("tips_enabled",false)
    offline_mode_enabled = sharedPref.getBoolean("offline_enabled",false)
  }

  fun setListeners(){

    nightModeSwitch.setOnClickListener {
      night_mode_enabled = !night_mode_enabled
      sharedPref.edit().putBoolean("night_enabled",night_mode_enabled).apply()
      postSettings()
    }

    autoModeSwitch.setOnClickListener {
      auto_mode_enabled = !auto_mode_enabled
      sharedPref.edit().putBoolean("auto_enabled",auto_mode_enabled).apply()
      postSettings()
    }

    securityModeSwitch.setOnClickListener {
      security_mode_enabled = !security_mode_enabled
      sharedPref.edit().putBoolean("security_enabled",security_mode_enabled).apply()
      postSettings()
    }

    offlineModeSwitch.setOnClickListener {
      offline_mode_enabled = !offline_mode_enabled
      sharedPref.edit().putBoolean("offline_enabled",offline_mode_enabled).apply()
      doToast("Settings Updated")
    }

    tipsSwitch.setOnClickListener {
      tips_enabled = !tips_enabled
      sharedPref.edit().putBoolean("tips_enabled",tips_enabled).apply()
      doToast("Settings Updated")
    }

  }

  fun setSwitches(){
    nightModeSwitch.isChecked = night_mode_enabled
    autoModeSwitch.isChecked = auto_mode_enabled
    securityModeSwitch.isChecked = security_mode_enabled
    offlineModeSwitch.isChecked = offline_mode_enabled
    tipsSwitch.isChecked = tips_enabled
  }
  override fun onAttach(context: Context) {
    super.onAttach(context)
    if (context is homeGallery){
      mhomeGallery = context;
    }
    else{
      throw RuntimeException( "not of type");
    }
  }

  override
  fun onDetach() {
    super.onDetach()
  }

  fun postSettings(){

    val email = sharedPref.getString("email", "")
    val token = sharedPref.getString("token", "")
    val nStr =night_mode_enabled.toString()
    val sStr =security_mode_enabled.toString()
    val aStr =auto_mode_enabled.toString()
    val params = "email=${email}&token=${token}&night_state=${nStr}&auto_state=${aStr}&security_state=${sStr}"
    setSettings().execute(params)
  }

  inner class setSettings : AsyncTask<String, String, String>() {
    override fun doInBackground(vararg params: String): String? {
      val url: URL
      val httpURLConnection: HttpURLConnection
      val outputStreamWriter: OutputStreamWriter
      val inputStreamReader: InputStreamReader
      val bufferedReader: BufferedReader
      var stringFromServer: String
      try {
        url = URL("https://homehaven.website/api/setSettings")
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
          doToast("Settings Updated")
        }
        else{
          doToast("Settings Update failed.")
        }
      }

    }

  }

  fun doToast(str:String){
    Toast.makeText(context,str, Toast.LENGTH_SHORT).show();
  }


  inner class getSettings : AsyncTask<String,String, String>() {
    override fun doInBackground(vararg params: String): String? {
      val url: URL
      val httpURLConnection: HttpURLConnection
      val outputStreamWriter: OutputStreamWriter
      val inputStreamReader: InputStreamReader
      val bufferedReader: BufferedReader
      var stringFromServer: String
      try {
        url = URL("https://homehaven.website/api/getSettings")
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
        if (string.length == 3){
          val code = string.toInt();
          if (code >= 0 && code <112 ) {
            night_mode_enabled = string.get(0) == '1'
            auto_mode_enabled = string.get(1) == '1'
            security_mode_enabled = string.get(2) == '1'
            with (sharedPref.edit()) {
              putBoolean("security_enabled",security_mode_enabled)
              putBoolean("night_enabled",night_mode_enabled)
              putBoolean("auto_enabled",auto_mode_enabled)
              apply()
            }
          }
          else doToast("False Settings :"+code)

        }
        else{
          doToast("Settings fetch failed.")
        }
      }
      setSwitches()
      enable(true)
      setListeners()
    }

  }

}

