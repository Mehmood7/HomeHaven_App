package com.example.homehaven

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // create notification here
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        val Notifications = arrayOf("Get filters of AC cleaned","Clean filters of an AC help to reduce electricity bill by 30%" ,
        "Turn off extra lights" , "Extra lights can increase your electricity bill by 15%" ,
        "Try to replace old appliances with new one" , "Happy with the appliance that is working well? Well guess what.. It is consuming 40% more power. Try to replace appliances that are more than 10 years old" ,
        "Do not use 100-watt bulbs" , "Use energy saver bulbs or LED bulbs that can reduce electricity bill by 20%" ,
        "Use washing machine when there is full load" , "Well here is an interesting tip. Want to reduce electricity bill? Use your washing machine at the times of full load" ,
        "Change the location of your Fridge" , "Keep your Fridge away from sunlight but at an open place where air can circulate easily. And see the difference in your bill" ,
        "Don’t keep your fridge iced" , "Make sure you defrost your fridge on a regular basis or whenever requisite. An iced up fridge will make the same work harder, wasting more energy than needed" ,
        "Is your home insulated?" , "Less insulated home means more work for AC or heater. Keep your home insulated. Make sure your roof ,  walls ,  doors and windows all are insulated" ,
        "Arrange your function in daylight" , "Have a party at home? Schedule it for day time instead of night and see the difference in bill" ,
        "Fix your leaking tabs" , "Fixing your leaking tabs mean saving water. Saving water means less load on water pump and less electricity bills.")
        val rnds = (0..9).random()
        val mBuilder =
            NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_save_money)
                .setContentTitle(Notifications[2*rnds])
                    // +intent?.extras?.get("NotificationText") as String
                .setContentText("Save Energy, Save Money")
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText(Notifications[2*rnds + 1]))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder.setChannelId("com.example.homehaven")
            val channel = NotificationChannel(
                "com.example.homehaven",
                "My App",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager?.createNotificationChannel(channel)
        }

        notificationManager.notify(121, mBuilder.build())

        ////////////////////////// Toast Does Not Work Here
    }
}

