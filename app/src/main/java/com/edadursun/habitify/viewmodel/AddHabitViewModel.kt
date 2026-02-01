package com.edadursun.habitify.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddHabitViewModel:ViewModel() {

    //Home sayfasına gitme isteğini viewe bildirir
    private val _goToHomeView = MutableLiveData<Unit>()
    val goToHomeView:LiveData<Unit> = _goToHomeView

    fun onGoBackClicked(){
        _goToHomeView.value=Unit
    }



    private val _selectedColor=MutableLiveData<String>()
    val selectedColor:LiveData<String> = _selectedColor

    //Kullanıcının seçtiği rengi ui state olarak tutar
    fun onColorSelected(color:String){
        _selectedColor.value=color
        Log.d("SELECTED","selected color : ${_selectedColor.value}")
    }



    private val _selectedEmoji=MutableLiveData<String>()
    val selectedEmoji:LiveData<String> = _selectedEmoji

    //Kullanıcının seçtiği emojiyi ui state olarak tutar
    fun onEmojiSelected(emoji:String){
        _selectedEmoji.value=emoji
        Log.d("SELECTED","selected emoji : ${_selectedEmoji.value}")
    }

    private val _targetValue=MutableLiveData<Int>()
    val targetValue:LiveData<Int> = _targetValue

    fun onTargetValueSelected(targetValue:Int){
        _targetValue.value=targetValue
        Log.d("SELECTED","target value : ${_targetValue.value}")
    }



    // Kategori seçim bottom sheet'inin açılmasını tetikleyen event
    private val _openCategoryDropdown = MutableLiveData<Unit>()
    val openCategoryDropdown:LiveData<Unit> = _openCategoryDropdown

    fun onCategoryClicked(){
        _openCategoryDropdown.value=Unit
    }

    private val _selectedCategory=MutableLiveData<String>()
    val selectedCategory:LiveData<String> = _selectedCategory

    fun onCategorySelected(category:String){
        _selectedCategory.value=category

    }


    //Saat picker açma eventi
    private val _openTimePicker = MutableLiveData<Unit>()
    val openTimePicker:LiveData<Unit> = _openTimePicker

    fun onTimeClicked(){
        _openTimePicker.value=Unit
    }

    //Seçilen saat
    private val _selectedTime = MutableLiveData<String>()
    val selectedTime:LiveData<String> = _selectedTime

    fun onTimeSelected(hour:Int,minute:Int){
        val formattedTime=String.format("%02d:%02d", hour, minute)
        _selectedTime.value=formattedTime
        Log.d("SELECTED", "Selected time: $formattedTime")
    }



    // VERİLERİ KAYDETME
    fun onSaveHabitClicked(){
        val color=selectedColor.value
        val emoji=selectedEmoji.value
        val targetValue=targetValue.value
        val category=selectedCategory.value
        val time=selectedTime.value

        Log.d("Add Habit","Save clicked")
        Log.d("Add Habit","Color: $color")
        Log.d("Add Habit","Emoji: $emoji")
        Log.d("Add Habit","Target Value: $targetValue")
        Log.d("Add Habit","Category:$category")
        Log.d("Add Habit","Time:$time")
    }

}