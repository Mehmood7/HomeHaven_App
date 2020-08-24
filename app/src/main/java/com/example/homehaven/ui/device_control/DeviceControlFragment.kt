package com.example.homehaven.ui.device_control

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.homehaven.*
import java.util.*

interface homeGallery{
  fun getDatastore(): dataStore ;
  fun getSharedPref(): SharedPreferences;
  fun doToast(str:String);
}

class DeviceControlFragment : Fragment() {
  private lateinit var mhomeGallery: homeGallery;
  private lateinit var galleryViewModel: DeviceControlViewModel
  private lateinit var roomList: ListView
  private lateinit var roomNames: Vector<String>
  private lateinit var db:dataStore
  private lateinit var roomObj: roomClass
  private lateinit var sharedPref: SharedPreferences

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    galleryViewModel =
    ViewModelProviders.of(this).get(DeviceControlViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_device_control, container, false)

    roomList = root.findViewById(R.id.rooms_list)

    sharedPref = mhomeGallery.getSharedPref()
    db = mhomeGallery.getDatastore()
    roomNames = db.getRoomNames()!!
    var roomsAdaptor = roomAdapter(galleryViewModel.getApplication(), roomNames)
    roomList.setAdapter(roomsAdaptor)
    roomList.setOnItemClickListener(
      object : AdapterView.OnItemClickListener {
        override fun onItemClick(
          parent: AdapterView<*>?,
          view: View,
          position: Int,
          id: Long
        ) {
          val roomIntent = Intent(activity, Room::class.java)
          roomIntent.putExtra("room_id", position)
          startActivity(roomIntent)
        }
      })


    return root
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