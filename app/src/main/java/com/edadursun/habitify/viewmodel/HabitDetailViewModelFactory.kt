package com.edadursun.habitify.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.edadursun.habitify.data.HabitRepository

// HabitDetailViewModel'i parametre ile oluşturabilmek için custom Factory sınıfı
class HabitDetailViewModelFactory(
    // ViewModel'e dışarıdan enjekte edeceğimiz repository bağımlılığı
    private val repository: HabitRepository
) : ViewModelProvider.Factory {

    // ViewModelProvider bu metodu çağırarak ViewModel instance'ı üretir
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        // İstenen ViewModel sınıfı HabitDetailViewModel ise
        if (modelClass.isAssignableFrom(HabitDetailViewModel::class.java)) {

            // Repository parametresi ile ViewModel oluşturulur
            // as T cast'i generics nedeniyle zorunludur
            return HabitDetailViewModel(repository) as T
        }

        // Eğer farklı bir ViewModel istenirse hata fırlatılır
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}