package com.edadursun.habitify.view

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.edadursun.habitify.R
import com.edadursun.habitify.databinding.ActivityHabitUpdateViewBinding
import com.edadursun.habitify.model.Habit
import com.edadursun.habitify.viewmodel.HabitUpdateViewModel
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class HabitUpdateView : AppCompatActivity() {

    private lateinit var binding: ActivityHabitUpdateViewBinding
    private lateinit var viewModel: HabitUpdateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Binding başlatılır
        binding = ActivityHabitUpdateViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ViewModel oluşturulur
        viewModel = ViewModelProvider(this)[HabitUpdateViewModel::class.java]

        // Habit başarıyla güncellenirse activity kapanır
        viewModel.habitUpdated.observe(this) {
            setResult(RESULT_OK)
            finish()
        }


        //Geri Dön
        binding.goBack.setOnClickListener {
            viewModel.onGoBackClicked()
        }

        viewModel.goToBack.observe(this) {
            finish()
        }

        //Habit Bilgilerini Alma

        // Intent üzerinden gelen habit modelini al
        val habit = intent.getSerializableExtra("habit") as? Habit

        if (habit == null) {
            Log.e("HABIT", "Habit null")
            return
        }

        // ViewModel'e habit verisini gönder
        viewModel.setHabit(habit)

        // UI'yi Habit Verileri ile Doldurma

        // Arka plan renkleri
        binding.habitUpdateView.setBackgroundColor(Color.parseColor(habit.colorHex))
        binding.materialCardView.setBackgroundColor(Color.parseColor(habit.colorHex))
        binding.scrollViewHabitUpdateView.setBackgroundColor(Color.parseColor(habit.colorHex))

        // Emoji ve başlık
        binding.pageIcon.setImageBitmap(emojiToBitmap(habit.emoji))
        binding.itemRowHabitHeader.habitTitle.setText(habit.title)
        binding.itemRowHabitHeader.selectedEmoji.setImageBitmap(emojiToBitmap(habit.emoji))

        // Emojinin arka plan rengi
        val emojiBg =
            binding.itemRowHabitHeader.selectedEmoji.background.mutate() as GradientDrawable
        emojiBg.setColor(Color.parseColor(habit.colorHex))

        // Kullanıcının seçtiği renk göstergesi
        val userSelectedColor =
            binding.itemRowColor.userSelectedColor.background.mutate() as GradientDrawable
        userSelectedColor.setColor(Color.parseColor(habit.colorHex))

        // Emoji iconu
        binding.itemRowEmoji.userSelectedIcon.setImageBitmap(emojiToBitmap(habit.emoji))

        // Target value ve kategori
        binding.itemRowTargetValue.targetValue.setText(habit.total.toString())
        binding.itemRowTargetValue.targetType.setText(habit.category)
        binding.itemRowSelectCategory.categoryText.setText(habit.category)

        // Reminder mesaj ve saat
        binding.itemRowReminderMessage.reminderMessage.setText(habit.reminderMessage)
        binding.itemRowReminderTime.reminderTime.setText(habit.reminderTime)

        // Gün Seçimleri

        // Önceden seçili günler parse edilir
        val selectedDays = habit.reminderDays.split(",").toSet()

        // Gün UI'ı güncellenir
        updateDayUI(
            selectedDays = selectedDays,
            color = habit.colorHex
        )

        // COLOR PICKER

        // Renk seçme butonu
        binding.itemRowColor.chooseColor.setOnClickListener {

            val sheetColor = ColorPickerBottomSheet { selectedColor ->

                // Seçilen renk ViewModel'e gönderilir
                viewModel.onColorSelected(selectedColor)
            }

            sheetColor.show(supportFragmentManager, "ColorPicker")
        }

        // Renk değiştiğinde UI güncellenir
        viewModel.selectedColor.observe(this) { color ->

            binding.habitUpdateView.setBackgroundColor(Color.parseColor(color))
            binding.materialCardView.setBackgroundColor(Color.parseColor(color))
            binding.scrollViewHabitUpdateView.setBackgroundColor(Color.parseColor(color))

            val emojiBg =
                binding.itemRowHabitHeader.selectedEmoji.background.mutate() as GradientDrawable
            emojiBg.setColor(Color.parseColor(color))

            val userSelectedColor =
                binding.itemRowColor.userSelectedColor.background.mutate() as GradientDrawable
            userSelectedColor.setColor(Color.parseColor(color))

            // Günlerin rengi de güncellenir
            updateDayUI(
                selectedDays = viewModel.selectedDays.value ?: emptySet(),
                color = color
            )
        }

        // EMOJI PICKER

        binding.itemRowEmoji.chooseEmoji.setOnClickListener {

            val sheetEmoji = EmojiPickerBottomSheet { selectedEmoji ->
                viewModel.onEmojiSelected(selectedEmoji)
            }

            sheetEmoji.show(supportFragmentManager, "EmojiPicker")
        }

        // Emoji değiştiğinde UI güncellenir
        viewModel.selectedEmoji.observe(this) { emoji ->

            if (emoji == null) {
                binding.itemRowHabitHeader.selectedEmoji.setImageResource(R.drawable.main_icon)
                binding.pageIcon.setImageResource(R.drawable.icon_bg)
            } else {

                val bitmap = emojiToBitmap(emoji)

                binding.itemRowHabitHeader.selectedEmoji.setImageBitmap(bitmap)
                binding.pageIcon.setImageBitmap(bitmap)
                binding.itemRowEmoji.userSelectedIcon.setImageBitmap(bitmap)
            }
        }

        // CATEGORY SEÇİMİ

        binding.itemRowSelectCategory.selectHabitCategory.setOnClickListener {
            viewModel.onCategoryClicked()
        }

        // Category bottomsheet açılır
        viewModel.openCategoryDropdown.observe(this) {

            val sheet = TargetCategoryBottomSheet { selectedCategory ->
                viewModel.onCategorySelected(selectedCategory)
            }

            sheet.show(supportFragmentManager, "CategoryBottomSheet")
        }

        // Seçilen kategori UI'a yazılır
        viewModel.selectedCategory.observe(this) { category ->
            binding.itemRowSelectCategory.categoryText.text = category
            binding.itemRowTargetValue.targetType.text = "/" + category
        }

        // TIME PICKER

        binding.itemRowReminderTime.reminderTime.setOnClickListener {
            viewModel.onTimeClicked()
        }

        viewModel.openTimePicker.observe(this) {

            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Saat Seç")
                .build()

            timePicker.show(supportFragmentManager, "TIME_PICKER")

            // Kullanıcı saat seçtiğinde
            timePicker.addOnPositiveButtonClickListener {

                val hour = timePicker.hour
                val minute = timePicker.minute

                viewModel.onTimeSelected(hour, minute)

                Log.d("TIME", "Seçilen saat: $hour:$minute")
            }
        }

        // Seçilen saat UI'a yazılır
        viewModel.selectedTime.observe(this) { time ->
            binding.itemRowReminderTime.reminderTime.text = time
        }

        // REMINDER SWITCH

        binding.itemRowReminderHeader.reminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onReminderSwitchChanged(isChecked)
        }

        viewModel.isReminderEnabled.observe(this) { isEnabled ->

            val selectedColor = viewModel.selectedColor.value

            if (isEnabled && selectedColor != null) {

                binding.itemRowReminderHeader.reminderSwitch.thumbTintList =
                    ColorStateList.valueOf(Color.parseColor("#FFFFFF"))

                binding.itemRowReminderHeader.reminderSwitch.trackTintList =
                    ColorStateList.valueOf(Color.parseColor(selectedColor))
            }
        }

        // GÜN SEÇİMİ

        val initialDays = habit.reminderDays.split(",").toSet()

        viewModel.setInitialDays(initialDays)

        updateDayUI(initialDays, habit.colorHex)

        viewModel.selectedDays.observe(this) { days ->

            val color = viewModel.selectedColor.value ?: habit.colorHex

            updateDayUI(days, color)
        }

        // Günlere tıklama
        binding.itemRowReminderDays.Mon.setOnClickListener { viewModel.onSelectedDays("Monday") }
        binding.itemRowReminderDays.Tue.setOnClickListener { viewModel.onSelectedDays("Tuesday") }
        binding.itemRowReminderDays.Wed.setOnClickListener { viewModel.onSelectedDays("Wednesday") }
        binding.itemRowReminderDays.Thu.setOnClickListener { viewModel.onSelectedDays("Thursday") }
        binding.itemRowReminderDays.Fri.setOnClickListener { viewModel.onSelectedDays("Friday") }
        binding.itemRowReminderDays.Sat.setOnClickListener { viewModel.onSelectedDays("Saturday") }
        binding.itemRowReminderDays.Sun.setOnClickListener { viewModel.onSelectedDays("Sunday") }

        /* SAVE BUTONU  */

        binding.saveHabit.setOnClickListener {

            viewModel.onInputClicked(
                binding.itemRowHabitHeader.habitTitle.text.toString().trim(),
                binding.itemRowTargetValue.targetValue.text.toString().trim(),
                binding.itemRowReminderMessage.reminderMessage.text.toString().trim()
            )
        }

        // Validation sonuçlarını dinle
        viewModel.validation.observe(this) { result ->

            binding.itemRowHabitHeader.tvHabitTitleError.visibility = View.GONE
            binding.itemRowTargetValue.tvTargetValueError.visibility = View.GONE
            binding.itemRowReminderMessage.tvReminderMessageError.visibility = View.GONE

            when (result) {

                HabitValidation.HabitTitleEmpty -> {
                    binding.itemRowHabitHeader.tvHabitTitleError.text =
                        "❗Habit title cannot be left blank."
                    binding.itemRowHabitHeader.tvHabitTitleError.visibility = View.VISIBLE
                }

                HabitValidation.TargetValueEmpty -> {
                    binding.itemRowTargetValue.tvTargetValueError.text =
                        "❗Target value cannot be left blank"
                    binding.itemRowTargetValue.tvTargetValueError.visibility = View.VISIBLE
                }

                HabitValidation.ReminderMessageEmpty -> {
                    binding.itemRowReminderMessage.tvReminderMessageError.text =
                        "❗Reminder message cannot be left blank."
                    binding.itemRowReminderMessage.tvReminderMessageError.visibility = View.VISIBLE
                }

                HabitValidation.Success -> {
                    // Validation başarılıysa kaydet
                    viewModel.onSaveHabitClicked()
                }
            }
        }
    }

    // EMOJI BITMAP -

    // Emoji karakterini Bitmap'e çevirir (ImageView'de göstermek için)
    fun emojiToBitmap(emoji: String): Bitmap {

        val paint = Paint().apply {
            textSize = 64f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        val baseline = -paint.ascent()
        val width = (paint.measureText(emoji) + 0.5f).toInt()
        val height = (baseline + paint.descent() + 0.5f).toInt()

        val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)

        canvas.drawText(emoji, width / 2f, baseline, paint)

        return image
    }

    // DAY UI UPDATE

    // Gün seçimi UI'ını günceller
    fun updateDayUI(
        selectedDays: Set<String>,
        color: String?,
        isInitial: Boolean = false
    ) {

        if (color == null) return

        val dayMap = mapOf(
            "Monday" to binding.itemRowReminderDays.Mon,
            "Tuesday" to binding.itemRowReminderDays.Tue,
            "Wednesday" to binding.itemRowReminderDays.Wed,
            "Thursday" to binding.itemRowReminderDays.Thu,
            "Friday" to binding.itemRowReminderDays.Fri,
            "Saturday" to binding.itemRowReminderDays.Sat,
            "Sunday" to binding.itemRowReminderDays.Sun
        )

        dayMap.forEach { (day, textView) ->

            val drawable = textView.background.mutate() as GradientDrawable

            val appliedColor = when {

                // İlk açılışta daha koyu renk
                selectedDays.contains(day) && isInitial ->
                    applyColorWithOpacity(color, 0.6f)

                // Seçili gün
                selectedDays.contains(day) ->
                    applyColorWithOpacity(color, 1f)

                // Seçili değil
                else ->
                    applyColorWithOpacity(color, 0.2f)
            }

            drawable.setColor(appliedColor)
        }
    }

    //  COLOR OPACITY

    // Hex renk üzerine opacity uygular
    private fun applyColorWithOpacity(colorHex: String, opacity: Float): Int {

        val baseColor = Color.parseColor(colorHex)

        val alpha = (255 * opacity).toInt()

        return Color.argb(
            alpha,
            Color.red(baseColor),
            Color.green(baseColor),
            Color.blue(baseColor)
        )
    }

}