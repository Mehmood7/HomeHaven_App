package com.example.homehaven.ui.usage_and_prediction

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.homehaven.R
import com.example.homehaven.ui.device_control.homeGallery
import kotlinx.android.synthetic.main.fragment_usage_and_prediction.*
import pl.pawelkleczkowski.customgauge.CustomGauge
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.text.DecimalFormat
import kotlin.math.ceil

class UsageAndPredictionFragment : Fragment() {

  private lateinit var mhomeGallery: homeGallery;
  private lateinit var usageAndPredictionViewModel: UsageAndPredictionViewModel
  private lateinit var consumpMeter: CustomGauge
  private lateinit var consumpText: TextView
  private lateinit var billPredictionTV: TextView
  private lateinit var billCompareTV: TextView
  private lateinit var previousBillTV: TextView
  private lateinit var powerCaompareTV: TextView
  private lateinit var refreshButton: Button
  private lateinit var sharedPref: SharedPreferences

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    usageAndPredictionViewModel =
    ViewModelProviders.of(this).get(UsageAndPredictionViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_usage_and_prediction, container, false)


    sharedPref = mhomeGallery.getSharedPref()
    consumpMeter = root.findViewById(R.id.gauge1)
    consumpText = root.findViewById(R.id.consumpTV)
    billCompareTV = root.findViewById(R.id.bill_compare_TV)
    powerCaompareTV = root.findViewById(R.id.power_compare_TV)
    billPredictionTV = root.findViewById(R.id.bill_predition_TV)
    powerCaompareTV = root.findViewById(R.id.power_compare_TV)
    refreshButton = root.findViewById(R.id.refresh_button)
    previousBillTV = root.findViewById(R.id.previous_bill_TV)


    val email = sharedPref.getString("email", "")
    val token = sharedPref.getString("token", "")
    val creds = "email=${email}&token=${token}"

    refreshButton.setOnClickListener {
      refreshButton.isEnabled = false
      getConsumptionData().execute(creds);
    }
    refreshButton.isEnabled = false
    getConsumptionData().execute(creds);

    return root
  }

  fun setConsump(k:Int){
    consumpMeter.pointSize = (k*270)/1000
    consumpMeter.invalidate()
    consumpText.text = "" + k
  }


  inner class getConsumptionData : AsyncTask<String, String, String>() {
    override fun doInBackground(vararg params: String): String? {
      val url: URL
      val httpURLConnection: HttpURLConnection
      val outputStreamWriter: OutputStreamWriter
      val inputStreamReader: InputStreamReader
      val bufferedReader: BufferedReader
      var stringFromServer: String
      try {
        url = URL("https://homehaven.website/api/getconsumpdata")
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
      refreshButton.isEnabled = true
      if (string == null) doToast("Failed to fetch Consumption Data") else {
        val code = string.substring(0,2);
        when (code) {
          "ok" -> {
            var data = string.substringAfter(':').split(':');
            val watts = data[0].toInt()
            val bill = data[1].toFloat()
            val previous_bill = data[2].toFloat()
            val today_consumption = data[3].toFloat()
            val yesterday_consumption = data[4].toFloat()

            setConsump(watts)
            billPredictionTV.text = ceil(bill).toString()
            previousBillTV.text = previous_bill.toString()
            if(today_consumption > yesterday_consumption){
              val percent = (today_consumption/yesterday_consumption - 1)*100
              powerCaompareTV.text = roundTwoDecimals(percent.toDouble()) + "% increase than yesterday."
              powerCaompareTV.setTextColor(Color.RED);
            }
            else
            {
              val percent = (yesterday_consumption/today_consumption - 1)*100
              powerCaompareTV.text = roundTwoDecimals(percent.toDouble()) + "% decrease than yesterday."
              powerCaompareTV.setTextColor(Color.BLUE);
            }

            if(bill > previous_bill){
              val percent = (today_consumption/yesterday_consumption - 1)*100
              billCompareTV.text = roundTwoDecimals(percent.toDouble()) + "% increase than previous month."
              billCompareTV.setTextColor(Color.RED);
            }
            else
            {
              val percent = (previous_bill/bill - 1)*100
              billCompareTV.text = roundTwoDecimals(percent.toDouble()) + "% decrease than previous month."
              billCompareTV.setTextColor(Color.BLUE);
            }
          }
          else -> doToast("Unknown Consumption data  \n" + string)

        }
      }

    }
  }
  fun doToast(str:String){
    Toast.makeText(context,str, Toast.LENGTH_SHORT).show();
  }
  fun roundTwoDecimals(d:Double):String
  {
    val twoDForm = DecimalFormat("#.##");
    return twoDForm.format(d)
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
}