package com.edadursun.habitify.data

import android.util.Log
import com.edadursun.habitify.model.Habit
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

//Veri katmanı firebase e neyi nasıl yazacağını bilir ne zaman yazacağını bilmez
class HabitRepository(
    private val remoteDataSource: HabitRemoteDataSource,
    private val userRepository: UserRepository,
    private val firestore: FirebaseFirestore
) {

    // Habiti ilgili usera kaydet
    fun addHabit(
        habit: Habit,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val userId = userRepository.getOrCreateUserId()
        remoteDataSource.saveHabit(
            userId = userId,
            habit = habit,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    // Habitlerin lastUpdated alanını bugünün tarihi ile güncelle
    fun updateAllHabitsLastUpdated(today: String) {
        val userId = userRepository.getUserId() ?: return
        firestore
            .collection("users")
            .document(userId)
            .collection("habits")
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot.documents.forEach { doc ->
                    val lastUpdated = doc.getString("lastUpdated") ?: ""
                    if (lastUpdated != today) {
                        doc.reference.update("lastUpdated", today)
                    }
                }
            }
    }

    // User id'ye göre habitleri getir
    fun getHabits(onResult: (List<Habit>) -> Unit) {
        val userId = userRepository.getUserId() ?: return
        remoteDataSource.getUserHabits(
            userId = userId,
            onResult = onResult,
            onError = {
                Log.e("HABIT", it.message ?: "Error")
                onResult(emptyList())
            }
        )
    }

    // 1 tane habit getir
    fun getHabit(
        habitId: String,
        onResult: (Habit?) -> Unit
    ) {
        val userId = userRepository.getUserId() ?: return
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


    //Habit fieldlarını güncelleme
    fun updateHabitField(habitId: String, field: String, value: Any) {
        val userId = userRepository.getUserId() ?: return
        firestore
            .collection("users")
            .document(userId)
            .collection("habits")
            .document(habitId)
            .update(field, value)
    }
    
}