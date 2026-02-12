package com.edadursun.habitify.model

import com.google.firebase.Timestamp

data class Habit(
    val title:String="",
    val colorHex:String="",
    val emoji:String="",
    val category:String="",
    val reminderDays:String="",
    val reminderMessage:String="",
    val sound:String="",
    val reminderTime:Timestamp=Timestamp.now(),
    val total:Int = 0,
    val complatedDay:List<String> = emptyList(),
    val current:Int=0,
    val longestSeries:Int=0,
    val missing:Int=0,
    val isCompleted:Boolean = false,
    val startingDay:String = "",
    val lastUpdated:String?=""
)
