package com.edadursun.habitify.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.edadursun.habitify.data.HabitRemoteDataSource
import com.edadursun.habitify.data.HabitRepository
import com.edadursun.habitify.data.UserLocaleDataSource
import com.edadursun.habitify.data.UserRepository
import com.edadursun.habitify.model.Habit
import com.edadursun.habitify.view.HabitValidation
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class HabitUpdateViewModel(application: Application) : AndroidViewModel(application) {

    private val firestore = FirebaseFirestore.getInstance()
    private val userRepository = UserRepository(UserLocaleDataSource(application))
    private val habitRepository = HabitRepository(
        remoteDataSource = HabitRemoteDataSource(firestore),
        userRepository = userRepository,
        firestore = firestore
    )

    //Geri dönme isteğini viewe bildirir , tetikleme yapar
    //Unit kullanıyoruz çünkü veri gönderme işlemi yok
    private val _goToBack = MutableLiveData<Unit>()
    val goToBack: LiveData<Unit> = _goToBack

    fun onGoBackClicked() {
        _goToBack.value = Unit
    }


    /* HABIT TITLE */
    private val _habitTitle = MutableLiveData<String>()
    val habitTitle: LiveData<String> = _habitTitle

    /* REMINDER MESSAGE */
    private val _reminderMessage = MutableLiveData<String>()
    val reminderMessage: LiveData<String> = _reminderMessage

    /* TARGET VALUE */
    private val _targetValue = MutableLiveData<Int>()
    val targetValue: LiveData<Int> = _targetValue


    // Title, message ve target value alanlarında boş geçilemesin
    private val _validation = MutableLiveData<HabitValidation>()
    val validation: LiveData<HabitValidation> = _validation

    fun onInputClicked(
        habitTitle: String,
        targetValue: String,
        reminderMessage: String
    ) {
        _habitTitle.value = habitTitle
        _targetValue.value = targetValue.toIntOrNull() ?: 0
        _reminderMessage.value = reminderMessage

        when {
            habitTitle.isBlank() -> {
                _validation.value = HabitValidation.HabitTitleEmpty
            }

            targetValue.isBlank() -> {
                _validation.value = HabitValidation.TargetValueEmpty
            }

            reminderMessage.isBlank() -> {
                _validation.value = HabitValidation.ReminderMessageEmpty
            }

            else -> {
                _validation.value = HabitValidation.Success
            }
        }
    }

    /* COLOR */
    private val _selectedColor = MutableLiveData<String>()
    val selectedColor: LiveData<String> = _selectedColor

    //Kullanıcının seçtiği rengi ui state olarak tutar
    fun onColorSelected(color: String) {
        _selectedColor.value = color
        Log.d("HABIT", "selected color : ${_selectedColor.value}")
    }


    /* EMOJI */
    private val _selectedEmoji = MutableLiveData<String>()
    val selectedEmoji: LiveData<String> = _selectedEmoji

    //Kullanıcının seçtiği emojiyi ui state olarak tutar
    fun onEmojiSelected(emoji: String) {
        _selectedEmoji.value = emoji
        Log.d("HABIT", "selected emoji : ${_selectedEmoji.value}")
    }


    /* TARGET CATEGORY */
    // Kategori seçim bottom sheet'inin açılmasını tetikleyen event
    private val _openCategoryDropdown = MutableLiveData<Unit>()
    val openCategoryDropdown: LiveData<Unit> = _openCategoryDropdown

    fun onCategoryClicked() {
        _openCategoryDropdown.value = Unit
    }

    private val _selectedCategory = MutableLiveData<String>()
    val selectedCategory: LiveData<String> = _selectedCategory

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
        Log.d("HABIT", "target category : ${_selectedCategory.value}")
    }


    /* TIME */
    // TimePicker açılması gerektiğini viewa bildiren event, true olduğunda pickerı açar
    private val _openTimePicker = MutableLiveData<Boolean>()
    val openTimePicker: LiveData<Boolean> = _openTimePicker

    //Kullanıcı saat alanına tıkladığında view a timepicker açması gerektiğini bildirir
    fun onTimeClicked() {
        _openTimePicker.value = true
    }

    //Kullanıcının seçtiği saat bilgisi
    private val _selectedTime = MutableLiveData<String>()
    val selectedTime: LiveData<String> = _selectedTime

    //Kullanıcı timepicker üzerinden saat seçtiğinde çağrılır, seçilen saat viewmodel statine kaydedilir
    fun onTimeSelected(hour: Int, minute: Int) {
        _selectedTime.value = String.format("%02d:%02d", hour, minute)
    }


    /* SELECTED DAYS */
    private val _selectedDays = MutableLiveData<Set<String>>(emptySet())
    val selectedDays: LiveData<Set<String>> = _selectedDays

    // Gün seçme / kaldırma
    fun onSelectedDays(day: String) {
        val currentDays = _selectedDays.value ?: emptySet()
        val updatedDays = if (currentDays.contains(day)) {
            currentDays - day
        } else {
            currentDays + day
        }
        _selectedDays.value = updatedDays
    }

    // Başlangıçta mevcut günleri set et
    fun setInitialDays(days: Set<String>) {
        _selectedDays.value = days
    }


    /* REMINDERS */
    private val _isReminderEnabled = MutableLiveData<Boolean>(false)
    val isReminderEnabled: LiveData<Boolean> = _isReminderEnabled

    fun onReminderSwitchChanged(isChecked: Boolean) {
        _isReminderEnabled.value = isChecked
        Log.d("HABIT", "reminders value : ${_isReminderEnabled.value}")
    }


    private var currentHabit: Habit? = null
    fun setHabit(habit: Habit) {
        currentHabit = habit
    }

    private val _habitUpdated = MutableLiveData<Unit>()
    val habitUpdated: LiveData<Unit> = _habitUpdated

    fun onSaveHabitClicked() {
        val oldHabit = currentHabit ?: return
        val newTotal=targetValue.value ?: oldHabit.total

        val updateHabit = oldHabit.copy(
            title = habitTitle.value ?: oldHabit.title,
            colorHex = selectedColor.value ?: oldHabit.colorHex,
            emoji = selectedEmoji.value ?: oldHabit.emoji,
            category = selectedCategory.value ?: oldHabit.category,
            reminderDays = selectedDays.value?.joinToString(",") ?: oldHabit.reminderDays,
            reminderMessage = reminderMessage.value ?: oldHabit.reminderMessage,
            reminderTime = selectedTime.value ?: oldHabit.reminderTime,
            total = targetValue.value ?: oldHabit.total,
            current = newCurrent(oldHabit.current, oldHabit.total, newTotal)
        )

        habitRepository.updateHabit(
            habit = updateHabit,
            onSuccess = {
                _habitUpdated.postValue(Unit)
            },
            onError = {
                Log.e("HABIT", "Update failed")
            })
    }


    //Yeni current hesaplama
    fun newCurrent(
        oldCurrent: Int,
        oldTotal: Int,
        newTotal: Int
    ): Int {
        if (oldTotal == 0) return 0

        val progressRatio=oldCurrent.toFloat() / oldTotal
        val newCurrent=(progressRatio * newTotal).toInt()

        return newCurrent.coerceAtMost(newTotal)
    }

}