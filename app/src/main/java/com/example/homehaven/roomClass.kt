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


    private var index:Int = 0
    private var name:String = ""
    private var types:String = ""
    private var state:Int = 0
    init {
        this.index = index
        this.name = name
        this.types = types
        this.state = state

        device1_type = types.get(0).toInt()-48
        device2_type = types.get(0).toInt()-48
        device3_type = types.get(0).toInt()-48
        device4_type = types.get(0).toInt()-48
        devicedim_type = types.get(0).toInt()-48

        device1_state = state and 128 == 1
        device2_state = state and 64 == 1
        device3_state = state and 32 == 1
        device4_state = state and 16 == 1
        devicedim_state = state and 8 == 1
        devicedim_level = state and 7

    }



}