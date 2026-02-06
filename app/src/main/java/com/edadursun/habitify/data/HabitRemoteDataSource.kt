package com.edadursun.habitify.data

import com.edadursun.habitify.model.Habit
import com.google.firebase.firestore.FirebaseFirestore

// BU SINIF HABITIN NASIL KAYDEDİLECEĞİNİ BİLİR
class HabitRemoteDataSource(private val firestore:FirebaseFirestore) {

    //Habiti kaydet
    fun saveHabit(
        userId:String,
        habitId:String,
        habit:Habit
    ){
        firestore
            .collection("users")
            .document(userId)
            .collection("habits")
            .document(habitId)
            .set(habit)
    }
}