package com.edadursun.habitify.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.edadursun.habitify.R
import com.edadursun.habitify.databinding.ActivityHomeViewBinding
import com.edadursun.habitify.viewmodel.HomeViewModel


class HomeView : AppCompatActivity() {

    // ViewBinding: XML'deki view'lara güvenli erişim
    private lateinit var binding: ActivityHomeViewBinding

    // Home ekranına ait ViewModel
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ViewModel'i Activity scope'unda oluştur
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Create Habit butonuna basılınca ViewModel'e sadece "olay oldu" bilgisini gönderiyoruz
        binding.createHabitBtn.setOnClickListener {
            viewModel.onAddHabitClicked()
        }

        // AddHabit sayfasına gitme event'ini dinle Event gelince navigation işlemi burada yapılır
        viewModel.goToAddHabit.observe(this) {
            startActivity(Intent(this, AddHabitView::class.java))
        }

        // Settings ikonuna basılınca
        binding.goSettingView.setOnClickListener {
            viewModel.onSettingsClicked()
        }

        // Settings sayfasına gitme event'ini dinle
        viewModel.goToSettings.observe(this) {
            startActivity(Intent(this, SettingView::class.java))
        }
    }
}
