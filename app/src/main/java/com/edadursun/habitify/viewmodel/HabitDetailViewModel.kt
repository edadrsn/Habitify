package com.edadursun.habitify.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.edadursun.habitify.data.HabitRepository
import com.edadursun.habitify.model.Habit
import com.google.firebase.firestore.FieldValue
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HabitDetailViewModel(private val habitRepository: HabitRepository) : ViewModel() {


    // Geri dönme eventi için LiveData
    private val _goToBack = MutableLiveData<Unit>()
    val goToBack: LiveData<Unit> = _goToBack

    // Geri butonuna basıldığında tetiklenir
    fun onGoBackClicked() {
        _goToBack.value = Unit
    }

    // İstatistik sayfasına gitme eventi
    private val _goToHabitStatistic = MutableLiveData<Unit>()
    val goToHabitStatistic: LiveData<Unit> = _goToHabitStatistic

    // İstatistik sayfasına yönlendirme tetiklenir
    fun onGoHabitStatistic() {
        _goToHabitStatistic.value = Unit
    }

    // Habit güncelleme sayfasına gitmek için seçilen habit gönderilir
    private val _goToHabitUpdate = MutableLiveData<Habit>()
    val goToHabitUpdate: LiveData<Habit> = _goToHabitUpdate

    // Güncelleme sayfasına yönlendirme
    fun onGoHabitUpdate(habit: Habit) {
        _goToHabitUpdate.value = habit
    }

    // ---------------- HABIT DATA ----------------

    // Seçilen habit bilgisini tutar
    private val _habit = MutableLiveData<Habit>()
    val habit: LiveData<Habit> = _habit

    // Habitleri repository üzerinden yükler
    fun loadHabit(habitId: String) {
        habitRepository.getHabit(habitId) { habit ->
            habit?.let {
                // Habit bilgisi UI'a gönderilir
                _habit.postValue(it)

                // Mevcut progress değeri UI'a gönderilir
                _currentValue.postValue(it.current)
            }
        }
    }

    // ---------------- PROGRESS STATE ----------------

    // Habit tamamlandı mı bilgisini tutar
    private val _isCompleted = MutableLiveData(false)
    val isCompleted: LiveData<Boolean> = _isCompleted

    // Mevcut ilerleme değeri
    private val _currentValue = MutableLiveData(0)
    val currentValue: LiveData<Int> = _currentValue

    // ---------------- PROGRESS ACTIONS ----------------

    // Habit değerini artırma işlemi
    fun increaseValue(habitId: String) {

        // Mevcut habit alınır, null ise işlem yapma
        val habit = habit.value ?: return

        // Mevcut ilerleme değeri
        val current = _currentValue.value ?: 0

        // Artış miktarı = total değerinin %10'u (minimum 1)
        val step = maxOf(1, (habit.total * 0.1).toInt())

        // Yeni değer hesaplanır fakat total değerini geçmez
        val newValue = (current + step).coerceAtMost(habit.total)

        // UI güncelle
        _currentValue.value = newValue

        // Firestore'da current alanı güncellenir
        habitRepository.updateHabitField(habitId, "current", newValue)

        // Eğer habit hedefe ulaştıysa tamamlandı olarak işaretle
        if (newValue == habit.total) {

            // Bugünün tarihi alınır
            val dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

            // Tamamlanan gün listesine eklenir
            habitRepository.updateHabitField(
                habitId,
                "complatedDay",
                FieldValue.arrayUnion(dateString)
            )

            // completed alanı true yapılır
            habitRepository.updateHabitField(habitId, "completed", true)
        }
    }

    // Habit değerini azaltma işlemi
    fun decreaseValue(habitId: String) {

        val habit = habit.value ?: return
        val current = _currentValue.value ?: 0

        // Azaltma miktarı = total değerinin %10'u (minimum 1)
        val step = maxOf(1, (habit.total * 0.1).toInt())

        // Yeni değer hesaplanır fakat 0'ın altına düşmez
        val newValue = (current - step).coerceAtLeast(0)

        // UI güncellenir
        _currentValue.value = newValue

        // Firestore'da current değeri güncellenir
        habitRepository.updateHabitField(habitId, "current", newValue)

        // Eğer habit artık tamamlanmış değilse completed false yapılır
        if (newValue != habit.total) {

            val dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

            // Gün bilgisi eklenir
            habitRepository.updateHabitField(
                habitId,
                "complatedDay",
                FieldValue.arrayUnion(dateString)
            )

            // completed false yapılır
            habitRepository.updateHabitField(habitId, "completed", false)
        }
    }

    // Habiti direkt tamamla butonu
    fun completeHabit(habitId: String) {

        val habit = _habit.value ?: return

        // Progress değeri direkt total yapılır
        _currentValue.value = habit.total

        // Firestore güncellenir
        habitRepository.updateHabitField(habitId, "current", habit.total)
        habitRepository.updateHabitField(habitId, "completed", true)

        // Bugünün tarihi alınır
        val dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        // Tamamlanan gün listesine eklenir
        habitRepository.updateHabitField(habitId, "complatedDay", FieldValue.arrayUnion(dateString))
    }
}