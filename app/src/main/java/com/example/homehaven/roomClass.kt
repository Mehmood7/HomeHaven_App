package com.example.homehaven


class roomClass(index:Int, name:String , types:String, state:Int){
    var device1_type:Int = 0
    var device2_type:Int = 0
    var device3_type:Int = 0
    var device4_type:Int = 0
    var devicedim_type:Int = 0
    var device1_state:Boolean = false
    var device2_state:Boolean = false
    var device3_state:Boolean = false
    var device4_state:Boolean = false
    var devicedim_state:Boolean = false
    var devicedim_level:Int = 0
    val TYPE_LIGHT = 1
    val TYPE_FAN = 2
    val TYPE_SOCKET = 3



    private var index:Int = 0
     var name:String = ""
     var types:String = ""
     var state:Int = 0
    init {
        this.index = index
        this.name = name
        this.types = types
        this.state = state

        device1_type = types.get(0).toInt()-48
        device2_type = types.get(1).toInt()-48
        device3_type = types.get(2).toInt()-48
        device4_type = types.get(3).toInt()-48
        devicedim_type = types.get(4).toInt()-48

        device1_state = (state and 128) != 0
        device2_state = (state and 64) != 0
        device3_state = (state and 32) != 0
        device4_state = (state and 16) != 0
        devicedim_state = (state and 8) != 0
        devicedim_level = state and 7

    }

    fun updateState(state: Int){
        this.state = state
        device1_state = (state and 128) != 0
        device2_state = (state and 64) != 0
        device3_state = (state and 32) != 0
        device4_state = (state and 16) != 0
        devicedim_state = (state and 8) != 0
        devicedim_level = state and 7
    }

    fun allTurn(type:Int, on:Boolean){
        if (device1_type == type) device1_state = on
        if (device2_type == type) device2_state = on
        if (device3_type == type) device3_state = on
        if (device4_type == type) device4_state = on
        if (devicedim_type == type) devicedim_state = on
        compressState()
    }

    private fun compressState(){
        var _state = state and 7
        if (device1_state) _state += 128
        if (device2_state) _state += 64
        if (device3_state) _state += 32
        if (device4_state) _state += 16
        if (devicedim_state) _state += 8
        state = _state
    }

}