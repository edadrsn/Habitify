package com.edadursun.habitify.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.edadursun.habitify.model.Habit
import com.edadursun.habitify.data.HabitRemoteDataSource
import com.edadursun.habitify.data.HabitRepository
import com.edadursun.habitify.data.UserLocaleDataSource
import com.edadursun.habitify.data.UserRepository
import com.edadursun.habitify.view.HabitValidation
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

class AddHabitViewModel(application: Application) : AndroidViewModel(application) {

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    // Home sayfasına gitme isteğini viewe bildirir
    // Neden Unit ? Herhangi bir veri gönderme işlemi yok sadece tetikleme var
    private val _goToHomeView = MutableLiveData<Unit>()
    val goToHomeView: LiveData<Unit> = _goToHomeView

    fun onGoBackClicked() {
        _goToHomeView.value = Unit
    }


    /* HABIT TITLE */
    private val _habitTitle = MutableLiveData<String>()
    val habitTitle: LiveData<String> = _habitTitle


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


    /* TARGET VALUE */
    private val _targetValue = MutableLiveData<Int>()
    val targetValue: LiveData<Int> = _targetValue


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


    /* SELECTED DAYS */
    private val _selectedDays = MutableLiveData<Set<String>>(emptySet())
    val selectedDays: LiveData<Set<String>> = _selectedDays

    init {
        _selectedColor.value = "#FBC1BF" // varsayılan renk
    }

    //Mevcut seçili günleri al
    //Eğer liste null ise boş set kabul et
    //Eğer day setin içerisindeyse çıkar değilse ekle , yeni seti _selectedDays içerisne koy
    fun onSelectedDays(day: String) {
        val currentDays = _selectedDays.value ?: emptySet()
        val updatedDays = if (currentDays.contains(day)) {
            currentDays - day
        } else {
            currentDays + day
        }
        _selectedDays.value = updatedDays
    }


    /* REMINDER MESSAGE */
    private val _reminderMessage = MutableLiveData<String>()
    val reminderMessage: LiveData<String> = _reminderMessage


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

    //Initial state:viewmodel ilk oluşturulduğunda açılır , saat alanının boş kalmaması için mevcut saati set eder
    init {
        setCurrentTime()
    }

    //Cihazın mevcut saatini alır HH:MM formatına çevirir ve selectedTime livedatasına atar
    @SuppressLint("NewApi")
    private fun setCurrentTime() {
        val now = LocalTime.now()
        val formattedTime = String.format("%02d:%02d", now.hour, now.minute)
        _selectedTime.value = formattedTime
    }

    //Kullanıcı timepicker üzerinden saat seçtiğinde çağrılır, seçilen saat viewmodel statine kaydedilir
    fun onTimeSelected(hour: Int, minute: Int) {
        _selectedTime.value = String.format("%02d:%02d", hour, minute)
    }


    /* REMINDERS */
    private val _isReminderEnabled = MutableLiveData<Boolean>(false)
    val isReminderEnabled: LiveData<Boolean> = _isReminderEnabled

    fun onReminderSwitchChanged(isChecked: Boolean) {
        _isReminderEnabled.value = isChecked
        Log.d("HABIT", "reminders value : ${_isReminderEnabled.value}")
    }


    // VERİLERİ KAYDETME
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

    private val firestore = FirebaseFirestore.getInstance()
    private val userRepository = UserRepository(UserLocaleDataSource(application))
    private val habitRepository = HabitRepository(
        remoteDataSource = HabitRemoteDataSource(firestore),
        userRepository = userRepository,
        firestore = firestore
    )


    fun onSaveHabitClicked() {

        val title = habitTitle.value ?: return
        val color = selectedColor.value ?: return
        val emoji = selectedEmoji.value ?: "🔥"
        val category = selectedCategory.value ?: "Category"
        val targetValue = targetValue.value ?: 0
        val reminderDays = selectedDays.value?.joinToString(",") ?: "Everyday"
        val reminderMessage = reminderMessage.value ?: return
        val sound = "Sound 1"
        val reminderTimeString = selectedTime.value ?: return
        val startingDay = getTodayAsString()
        val days = selectedDays.value
        val reminderEnabled = isReminderEnabled.value ?: false


        Log.d("Add Habit", "Title: $title ")
        Log.d("Add Habit", "Save clicked")
        Log.d("Add Habit", "Color: $color")
        Log.d("Add Habit", "Emoji: $emoji")
        Log.d("Add Habit", "Target Value: $targetValue")
        Log.d("Add Habit", "Category:$category")
        Log.d("Add Habit", "Time:$reminderTimeString")
        Log.d("Add Habit", "Reminder message:$reminderMessage")
        Log.d("Add Habit", "Reminder state:$reminderEnabled")
        Log.d("Add Habit", "Days:$days")


        val habit = Habit(
            title = title,
            colorHex = color,
            emoji = emoji,
            category = category,
            total = targetValue,
            reminderDays = reminderDays,
            reminderMessage = reminderMessage,
            sound = sound,
            reminderTime = reminderTimeString,
            current = 0,
            longestSeries = 0,
            missing = 0,
            isCompleted = false,
            complatedDay = emptyList(),
            startingDay = startingDay,
            lastUpdated = startingDay
        )

        habitRepository.addHabit(
            habit,
            onSuccess = {
                Log.d("Add Habit", "Habit eklendi")
            },
            onError = {
                Log.d("Add Habit", "Habit eklenemedi")
            })

        //Kayıt başarılıysa geri dön
        _goToHomeView.value = Unit

    }

    init {
        updateLastUpdatedForAllHabits()
    }

    // Bugünün tarihini gün-ay-yıl olarak al

    private fun getTodayAsString(): String {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return today.format(formatter)
    }

    //lastUpdated alanını güncelle
    fun updateLastUpdatedForAllHabits() {
        val today = getTodayAsString()
        habitRepository.updateAllHabitsLastUpdated(today)
    }

}