package com.edadursun.habitify.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import java.io.Serializable

// Veriyi düzenli taşımamızı sağlar
// Bir şeyin örneğini(instance) üretmek istediğimizde data class kullanırız
data class Habit(
    var id:String="",
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

):Serializable
