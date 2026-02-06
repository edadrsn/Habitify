package com.edadursun.habitify.view

import android.content.Context
import android.util.Log

//CİHAZA USER IDYİ KAYDEDER VE OKUR
class UserLocaleDataSource(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    //Shared preferences keyi
    companion object {
        private const val KEY_USER_ID = "USER_ID"
    }

    //User id yi getirir
    fun getUser(): String? {
        val userId = sharedPreferences.getString(KEY_USER_ID, null)
        Log.d("USER_ID", "USER ID VAR: $userId ")
        return userId
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