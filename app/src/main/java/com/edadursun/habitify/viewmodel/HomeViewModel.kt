package com.edadursun.habitify.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    //Unit = veri taşımıyoruz, sadece "bir olay oldu" bilgisini veriyoruz, ViewModel, View'e "AddHabit sayfasına git" event'i üretir
    private val _goToAddHabit = MutableLiveData<Unit>()

    // View (Activity), bu LiveData'yı observe eder Dışarıdan sadece okunabilir (encapsulation)
    val goToAddHabit: LiveData<Unit> = _goToAddHabit


    // Ayarlar sayfasına gitmek için ayrı bir event Çünkü her event farklı bir aksiyon tetikler
    private val _goToSettings = MutableLiveData<Unit>()
    val goToSettings: LiveData<Unit> = _goToSettings


    // Create Habit (+) butonuna basıldığında çağrılır ViewModel "AddHabit ekranına git" olayını tetikler
    fun onAddHabitClicked() {
        _goToAddHabit.value = Unit
    }

    // Ayarlar ikonuna basıldığında çağrılır ViewModel "Settings ekranına git" olayını tetikler
    fun onSettingsClicked() {
        _goToSettings.value = Unit
    }
}
