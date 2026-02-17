package com.edadursun.habitify.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.edadursun.habitify.R
import com.edadursun.habitify.databinding.ActivityHomeViewBinding
import com.edadursun.habitify.view.adapter.HabitAdapter
import com.edadursun.habitify.viewmodel.HomeViewModel

class HomeView : AppCompatActivity() {

    private lateinit var binding:ActivityHomeViewBinding

    // Home ekranına ait ViewModel
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: HabitAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityHomeViewBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // ViewModel'i Activity scope'unda oluştur
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]


        adapter = HabitAdapter(
            onClick = { habit ->
                viewModel.onHabitDetailClicked(habit)
            })

        viewModel.goToHabitDetail.observe(this){ habitId ->
            val intent=Intent(this,HabitDetailView::class.java)
            intent.putExtra("habitId",habitId)
            startActivity(intent)

        }

        binding.habitsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.habitsRecyclerView.adapter = adapter



        viewModel.habits.observe(this) { habits ->
            if (habits.isEmpty()) {
                binding.homeInfo.visibility = View.VISIBLE
                binding.habitsRecyclerView.visibility = View.GONE
            } else {
                binding.homeInfo.visibility = View.GONE
                binding.habitsRecyclerView.visibility = View.VISIBLE
                adapter.updateList(habits)
            }
        }

        viewModel.loadHabits()



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


        // DATE
        viewModel.todayDate.observe(this) { date ->
            binding.todayDateText.text = date
        }

        viewModel.weekDays.observe(this) { days ->

            binding.dayText1.text = days[0].dayName
            binding.dayNum1.text = days[0].dayNumber

            binding.dayText2.text = days[1].dayName
            binding.dayNum2.text = days[1].dayNumber

            binding.dayText3.text = days[2].dayName
            binding.dayNum3.text = days[2].dayNumber

            // BUGÜN
            binding.dayText4.text = days[3].dayName
            binding.dayNum4.text = days[3].dayNumber

            binding.dayText5.text = days[4].dayName
            binding.dayNum5.text = days[4].dayNumber

            binding.dayText6.text = days[5].dayName
            binding.dayNum6.text = days[5].dayNumber

            binding.dayText7.text = days[6].dayName
            binding.dayNum7.text = days[6].dayNumber
        }

    }
}