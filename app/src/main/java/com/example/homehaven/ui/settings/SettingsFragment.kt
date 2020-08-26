package com.example.homehaven.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.homehaven.R

class SettingsFragment : Fragment() {

  private lateinit var settingsViewModel: SettingsViewModel
  private lateinit var nightModeSwitch: Switch
  private lateinit var autoModeSwitch: Switch
  private lateinit var securityModeSwitch: Switch
  private lateinit var offlineModeSwitch: Switch
  private lateinit var TipsSwitch: Switch

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
    TipsSwitch = root.findViewById(R.id.setting_Tips_switch)
    
    
    return root
  }
}