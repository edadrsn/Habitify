package com.edadursun.habitify.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.edadursun.habitify.R
import com.edadursun.habitify.databinding.ActivitySettingViewBinding
import com.edadursun.habitify.viewmodel.SettingViewModel

class SettingView : AppCompatActivity() {

    private lateinit var binding: ActivitySettingViewBinding
    private lateinit var viewModel:SettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivitySettingViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // STATUS BAR PADDING'İ HEADER'A EKLE
        ViewCompat.setOnApplyWindowInsetsListener(binding.materialCardView) { view, insets ->
            val statusBarHeight =
                insets.getInsets(WindowInsetsCompat.Type.statusBars()).top

            view.setPadding(
                view.paddingLeft,
                statusBarHeight,
                view.paddingRight,
                view.paddingBottom
            )
            insets
        }



        viewModel=ViewModelProvider(this)[SettingViewModel::class.java]

        binding.goBack.setOnClickListener {
            viewModel.onGoToBack()
        }

        viewModel.goToBack.observe(this){ back ->
            finish()
        }
    }
}