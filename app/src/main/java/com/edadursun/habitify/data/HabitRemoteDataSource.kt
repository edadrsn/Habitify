package com.edadursun.habitify.data

import com.edadursun.habitify.model.Habit
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects

// BU SINIF HABITIN NASIL KAYDEDİLECEĞİNİ BİLİR - Habit verisi firebase e nasıl yazılır ve nasıl okunur
class HabitRemoteDataSource(private val firestore:FirebaseFirestore) {

    //Habiti kaydet
    fun saveHabit(
        userId:String,
        habit:Habit,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ){
        val docRef=firestore
            .collection("users")
            .document(userId)
            .collection("habits")
            .document()

        val habitWithId = habit.copy(id = docRef.id)

        docRef.set(habitWithId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }


    //Kullanıcının habitlerini getir
    fun getUserHabits(
        userId: String?,
        onResult: (List<Habit>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        if (userId.isNullOrBlank()) {
            onResult(emptyList())
            return
        }

        firestore.collection("users")
            .document(userId)
            .collection("habits")
            .addSnapshotListener{ snapshot, error ->

                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                //dokümanı habite dönüştürür
                val habits = snapshot?.toObjects(Habit::class.java) ?: emptyList()
                onResult(habits)
            }
    }
}