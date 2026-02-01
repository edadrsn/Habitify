package com.edadursun.habitify.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingViewModel:ViewModel() {

    private val _goToBack=MutableLiveData<Unit>()
    val goToBack:LiveData<Unit> = _goToBack

    fun onGoToBack(){
        _goToBack.value=Unit
    }
}