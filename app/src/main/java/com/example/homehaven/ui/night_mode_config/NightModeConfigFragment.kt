package com.example.homehaven.ui.night_mode_config

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.homehaven.R

class NightModeConfigFragment : Fragment() {

  private lateinit var nightModeConfigViewModel: NightModeConfigViewModel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    nightModeConfigViewModel =
    ViewModelProviders.of(this).get(NightModeConfigViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_night_mode_config, container, false)
    return root
  }
}