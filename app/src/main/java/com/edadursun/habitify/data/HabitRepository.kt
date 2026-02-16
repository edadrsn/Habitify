package com.edadursun.habitify.data

import android.util.Log
import com.edadursun.habitify.model.Habit
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import java.util.UUID

//HABIT HANGİ USERA KAYDEDİLECEK
class HabitRepository(
    private val remoteDataSource: HabitRemoteDataSource,
    private val userRepository: UserRepository,
    private val firestore:FirebaseFirestore
) {
    //Habiti ilgili usera kaydet
    fun addHabit(
        habit: Habit,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ){
        val userId=userRepository.getOrCreateUserId()

        remoteDataSource.saveHabit(
            userId=userId,
            habit=habit,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    //Habitin lastUpdated alanını bugünün tarihi ile güncelle
    fun updateAllHabitsLastUpdated(today:String){
        val userId=userRepository.getOrCreateUserId()
        firestore
            .collection("users")
            .document(userId)
            .collection("habits")
            .get()
            .addOnSuccessListener {snapshot ->
                snapshot.documents.forEach { doc ->
                   val lastUpdated=doc.getString("lastUpdated") ?: ""

                    if(lastUpdated != today){
                        doc.reference.update("lastUpdated",today)
                    }
                }
            }
    }


    //User idye göre Habitleri getir
    fun getHabits(onResult: (List<Habit>) -> Unit){
        val userId=userRepository.getOrCreateUserId()
        remoteDataSource.getUserHabits(
            userId = userId,
            onResult = onResult,
            onError = {
                Log.e("HABIT", it.message ?: "Error")
                onResult(emptyList())
            }
        )
    }


    //1 tane Habit getir
    fun getHabit(
        habitId:String,
        onResult:(Habit?) -> Unit
    ){
        val userId=userRepository.getOrCreateUserId()
        firestore
            .collection("users")
            .document(userId)
            .collection("habits")
            .document(habitId)
            .get()
            .addOnSuccessListener { doc ->
                val habit = doc.toObject(Habit::class.java)
                onResult(habit)
            }

    }
}