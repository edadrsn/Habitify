package com.edadursun.habitify.data

import com.edadursun.habitify.model.Habit
import java.util.UUID

//HABIT HANGİ USERA KAYDEDİLECEK
class HabitRepository(
    private val remoteDataSource: HabitRemoteDataSource,
    private val userRepository: UserRepository
) {
    fun addHabit(habit: Habit){
        val userId=userRepository.getOrCreateUserId()
        val habitId=UUID.randomUUID().toString()

        remoteDataSource.saveHabit(
            userId=userId,
            habitId=habitId,
            habit=habit
        )
    }
}