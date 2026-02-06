package com.edadursun.habitify.data

import java.util.UUID

// VERİ NEREDEN GELİYOR
class UserRepository(
    private val localeDataSource: UserLocaleDataSource
) {

    //Kullanıcı varsa id sini getir yoksa yeni id oluştur
    fun getOrCreateUserId(): String {
        val id = localeDataSource.getUser()

        return if (id != null) {
            id   // uygulama daha önce açıldıysa daha önce aynı id kaydedildiyse o id yi geri döndür
        } else {
            val newId = UUID.randomUUID().toString()
            localeDataSource.setUser(newId)
            newId
        }
    }
}