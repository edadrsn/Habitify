package com.edadursun.habitify.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.edadursun.habitify.data.HabitRemoteDataSource
import com.edadursun.habitify.data.HabitRepository
import com.edadursun.habitify.model.DayItem
import com.edadursun.habitify.data.UserLocaleDataSource
import com.edadursun.habitify.data.UserRepository
import com.edadursun.habitify.model.Habit
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class  HomeViewModel(application:Application) : AndroidViewModel(application) {

    private val firestore= FirebaseFirestore.getInstance()
    private val userRepository = UserRepository(UserLocaleDataSource(application))
    private val habitRepository = HabitRepository(
        remoteDataSource = HabitRemoteDataSource(firestore),
        userRepository = userRepository,
        firestore = firestore
    )

    private val _habits=MutableLiveData<List<Habit>>()
    val habits:LiveData<List<Habit>> = _habits

    // HABITLERI YÜKLE
    fun loadHabits(){
       habitRepository.getHabits { habitList ->
           _habits.postValue(habitList)
       }
    }



    //Unit = veri taşımıyoruz, sadece "bir olay oldu" bilgisini veriyoruz, ViewModel, View'e "AddHabit sayfasına git" event'i üretir
    private val _goToAddHabit = MutableLiveData<Unit>()

    // View (Activity), bu LiveData'yı observe eder Dışarıdan sadece okunabilir (encapsulation)
    val goToAddHabit: LiveData<Unit> = _goToAddHabit

    // Create Habit (+) butonuna basıldığında çağrılır ViewModel "AddHabit ekranına git" olayını tetikler
    fun onAddHabitClicked() {
        _goToAddHabit.value = Unit
    }


    // Ayarlar sayfasına gitmek için ayrı bir event Çünkü her event farklı bir aksiyon tetikler
    private val _goToSettings = MutableLiveData<Unit>()
    val goToSettings: LiveData<Unit> = _goToSettings

    // Ayarlar ikonuna basıldığında çağrılır ViewModel "Settings ekranına git" olayını tetikler
    fun onSettingsClicked() {
        _goToSettings.value = Unit
    }

    // Habit detayına git
    private val _goToHabitDetail=MutableLiveData<String>()
    val goToHabitDetail : LiveData<String> = _goToHabitDetail

    fun onHabitDetailClicked(habit:Habit){
        _goToHabitDetail.value=habit.id
    }



    // DATE
    // Güncel tarihi tutar
    private val _todayDate=MutableLiveData<String>()
    val todayDate:LiveData<String> = _todayDate

    //Bugünün 3 gün öncesi + bugünün kendisi + 3 gün sonrası olmak üzere toplam 7 gün tutar
    private val _weekDays=MutableLiveData<List<DayItem>>()
    val weekDays:LiveData<List<DayItem>> = _weekDays

    init{
        setTodayDate() // viewmodel ilk oluşturulduğunda bugünün tarihini ayarla
        setWeekDaysAroundToday() //bugünden itibaren 7 gün hesapla ve listeyi doldur
    }


    fun setTodayDate(){
        // cihazın bugünkü tarihini al
        val sdf = SimpleDateFormat("d , MMMM", Locale.ENGLISH)
        _todayDate.value = sdf.format(Date())
    }

    fun setWeekDaysAroundToday() {
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("EEE", Locale.ENGLISH)

        val list = mutableListOf<DayItem>()

        for (i in -3..3) {
            val temp = calendar.clone() as Calendar
            temp.add(Calendar.DAY_OF_YEAR, i)

            list.add(
                DayItem(
                    dayName = formatter.format(temp.time).uppercase(),
                    dayNumber = temp.get(Calendar.DAY_OF_MONTH).toString()
                )
            )
        }

        _weekDays.value = list
    }

}
