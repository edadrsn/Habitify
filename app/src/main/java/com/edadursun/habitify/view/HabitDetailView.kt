package com.edadursun.habitify.view

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.edadursun.habitify.R
import com.edadursun.habitify.data.HabitRemoteDataSource
import com.edadursun.habitify.data.HabitRepository
import com.edadursun.habitify.data.UserLocaleDataSource
import com.edadursun.habitify.data.UserRepository
import com.edadursun.habitify.databinding.ActivityHabitDetailViewBinding
import com.edadursun.habitify.view.adapter.HabitAdapter
import com.edadursun.habitify.viewmodel.HabitDetailViewModel
import com.edadursun.habitify.viewmodel.HabitDetailViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestore.*

class HabitDetailView : AppCompatActivity() {

    private lateinit var binding: ActivityHabitDetailViewBinding
    private lateinit var viewModel: HabitDetailViewModel
    private var total: Int = 0

    // Güncelleme sayfasından geri döndüğümüzde habiti yeniden yüklemek için launcher
    private val updateHabitLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Intent üzerinden habitId alınır ve ViewModel ile tekrar yüklenir
                val habitId = intent.getStringExtra("habitId") ?: return@registerForActivityResult
                viewModel.loadHabit(habitId)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHabitDetailViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent'ten habitId alınır
        val habitId = intent.getStringExtra("habitId") ?: ""

        val firestore = FirebaseFirestore.getInstance()
        val userRepository = UserRepository(UserLocaleDataSource(applicationContext))
        val habitRepository = HabitRepository(
            HabitRemoteDataSource(firestore),
            userRepository,
            firestore
        )

        val factory = HabitDetailViewModelFactory(habitRepository)
        viewModel = ViewModelProvider(this, factory).get(HabitDetailViewModel::class.java)

        // Habiti yükle
        viewModel.loadHabit(habitId)

        // Habit LiveData'sını gözlemle ve UI'i güncelle
        viewModel.habit.observe(this) { habit ->
            binding.habitTitle.text = habit.title
            binding.habitEmoji.text = habit.emoji
            binding.circularProgress.max = 100
            binding.circularProgress.progress = 0
            binding.habitTargetValue.text = "/" + habit.total.toString()
            binding.circularProgress.setIndicatorColor(Color.parseColor(habit.colorHex))
            val colorInt = Color.parseColor(habit.colorHex)
            binding.completedHabit.backgroundTintList = ColorStateList.valueOf(colorInt)
            total = habit.total
        }

        // Geri dön butonu tıklama
        binding.goBack.setOnClickListener {
            viewModel.onGoBackClicked()
        }

        // ViewModel üzerinden geri gitme gözlemle
        viewModel.goToBack.observe(this) { back ->
            finish() // Activity'i kapat
        }

        // İstatistik sayfasına gitme
        binding.goHabitStatistic.setOnClickListener {
            viewModel.onGoHabitStatistic()
        }

        viewModel.goToHabitStatistic.observe(this){statistic ->
            startActivity(Intent(this,HabitStatisticView::class.java))
        }

        // Güncelleme sayfasına gitme
        binding.goHabitUpdate.setOnClickListener {
            val habit = viewModel.habit.value ?: return@setOnClickListener
            viewModel.onGoHabitUpdate(habit)
        }

        viewModel.goToHabitUpdate.observe(this){ habit ->
            val intent = Intent(this, HabitUpdateView::class.java)
            intent.putExtra("habit",habit)
            updateHabitLauncher.launch(intent) // Güncelleme sayfasını başlat
        }

        // PROGRESS BAR işlemleri
        binding.increaseHabit.setOnClickListener {
            viewModel.increaseValue(habitId) // Değeri artır
        }

        binding.decreaseHabit.setOnClickListener {
            viewModel.decreaseValue(habitId) // Değeri azalt
        }

        binding.completedHabit.setOnClickListener {
            viewModel.completeHabit(habitId) // Habiti tamamla
        }

        // Progress bar'ı gözlemle ve güncelle
        viewModel.currentValue.observe(this) { current ->
            val total = viewModel.habit.value?.total ?: 1
            val percent = (current * 100) / total // Yüzdelik hesaplama

            binding.circularProgress.max = 100
            binding.circularProgress.progress = percent
            binding.progressValue.text = "$current / $total" // Progress text
        }
    }

}