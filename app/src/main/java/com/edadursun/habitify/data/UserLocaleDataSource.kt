package com.edadursun.habitify.data

import android.content.Context
import android.util.Log
import com.edadursun.habitify.viewmodel.HabitDetailViewModel

//CİHAZA USER IDYİ KAYDEDER VE OKUR
class UserLocaleDataSource(private val context:Context) {

    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    //Shared preferences keyi
    companion object {
        private const val KEY_USER_ID = "USER_ID"
    }

    //User id yi getirir
    fun getUser(): String? {
        return try{
        val userId = sharedPreferences.getString(KEY_USER_ID, null)
        Log.d("USER_ID", "USER ID VAR: $userId ")
        userId
        } catch (e:Exception){
            sharedPreferences.edit().clear().apply()
            null
        }
    }

    //User id yi kaydet
    fun setUser(userId: String) {
        sharedPreferences
            .edit()
            .putString(KEY_USER_ID, userId)
            .apply()

        Log.d("USER_ID", "USER ID YENİ OLUŞTURULDU: $userId")
    }
}