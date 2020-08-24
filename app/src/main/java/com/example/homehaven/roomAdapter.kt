package com.example.homehaven

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.util.*

class roomAdapter(con: Context, rooms:Vector<String>, txtColor: Int): BaseAdapter() {

    var mContext: Context? = null
    var mNameColor: Int = 0
    var adaptorList: List<String>? = null
    init{
        mContext = con
        adaptorList = rooms
        mNameColor = txtColor
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var conView = convertView
        if (convertView == null) conView =
            LayoutInflater.from(mContext).inflate(R.layout.room_name_item, null)
        val room_name_str: String = getItem(position) as String
        (conView!!.findViewById<View>(R.id.room_name_tv) as TextView).setText(room_name_str)
        (conView!!.findViewById<View>(R.id.room_name_tv) as TextView).setTextColor(mNameColor)
        return conView!!
    }

    override fun getItem(position: Int): Any {
        return adaptorList!!.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        if(adaptorList !== null)
            return adaptorList!!.size
        return 0
    }
}