package com.edadursun.habitify.view

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.edadursun.habitify.R
import com.edadursun.habitify.databinding.ActivityAddHabitViewBinding
import com.edadursun.habitify.viewmodel.AddHabitViewModel
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class AddHabitView : AppCompatActivity() {

    private lateinit var binding: ActivityAddHabitViewBinding
    private lateinit var viewModel: AddHabitViewModel
    private var mediaPlayer: MediaPlayer? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddHabitViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Viewmodel lifecycle a bağlı şekilde oluşturulur
        viewModel = ViewModelProvider(this)[AddHabitViewModel::class.java]


        /* BACK BUTTON */
        //Geri butonuna tıklandığında viewmodele haber verilir
        binding.goBack.setOnClickListener {
            viewModel.onGoBackClicked()
        }

        //viewmodele geri dönme olayı gelirse activitiyi kapat
        viewModel.goToHomeView.observe(this) { back ->
            finish()
        }


        /* COLOR PICKER */
        //Renk seçme alanına tıklanırsa bottomsheeti açılır
        binding.itemRowColor.chooseColor.setOnClickListener {
            val sheetColor = ColorPickerBottomSheet { selectedColor ->
                // seçilen renk viewmodele gönderilir
                viewModel.onColorSelected(selectedColor)
            }

            sheetColor.show(supportFragmentManager, "ColorPicker")
        }
        // Seçilen renk değişirse ui güncellenir
        viewModel.selectedColor.observe(this) { color ->

            //Genel arka plan renkleri
            binding.main.setBackgroundColor(Color.parseColor(color))
            binding.materialCardView.setBackgroundColor(Color.parseColor(color))
            binding.scrollViewAddHabitView.setBackgroundColor(Color.parseColor(color))

            //Emojinin arka plan rengini ayarla
            val emojiBg = binding.itemRowHabitHeader.selectedEmoji.background.mutate() as GradientDrawable
            emojiBg.setColor(Color.parseColor(color))

            //Kullanıcının seçtiği rengi göster
            val userSelectedColor = binding.itemRowColor.userSelectedColor.background.mutate() as GradientDrawable
            userSelectedColor.setColor(Color.parseColor(color))

            //Seçilen günlerin arka plan rengi
            updateDayUI(
                selectedDays = viewModel.selectedDays.value ?: emptySet(),
                color = color
            )
        }


        /* EMOJİ PICKER */
        //Emoji seçme alanına tıklandığında bottomsheet açılır
        binding.itemRowEmoji.chooseEmoji.setOnClickListener {
            val sheetEmoji = EmojiPickerBottomSheet { selectedEmoji ->
                //Seçilen emoji viewmodele gönderilir
                viewModel.onEmojiSelected(selectedEmoji)
            }
            sheetEmoji.show(supportFragmentManager, "EmojiPicker")
        }

        // Seçilen emoji değiştiğinde ui günceller
        viewModel.selectedEmoji.observe(this) { emoji ->
            if (emoji == null) {
                // Emoji seçilmemişse → artı ikonunu göster
                binding.itemRowHabitHeader.selectedEmoji.setImageResource(R.drawable.main_icon)
                binding.pageIcon.setImageResource(R.drawable.icon_bg)
            } else {
                // Emoji seçildiyse → emoji'yi göster
                val bitmap = emojiToBitmap(emoji)
                binding.itemRowHabitHeader.selectedEmoji.setImageBitmap(bitmap)
                binding.pageIcon.setImageBitmap(bitmap)
                binding.itemRowEmoji.userSelectedIcon.setImageBitmap(bitmap)

            }

        }


        /* CATEGORY DROPDOWN */
        //Kategori seçmeye tıklanırsa viewmodele bildir
        binding.itemRowSelectCategory.selectHabitCategory.setOnClickListener {
            viewModel.onCategoryClicked()
        }

        //Viewmodel dropdown açılmasını isterse bottomsheet açılır
        viewModel.openCategoryDropdown.observe(this) {
            val sheet = TargetCategoryBottomSheet { selectedCategory ->
                viewModel.onCategorySelected(selectedCategory)
            }
            sheet.show(supportFragmentManager, "CategoryBottomSheet")
        }

        //Seçilen kategoriyi ui da göster
        viewModel.selectedCategory.observe(this) { category ->
            binding.itemRowSelectCategory.categoryText.text = category
            binding.itemRowTargetValue.targetType.text = "/" + category

        }


        /* REMINDERS */
        //Switch açılıp kapanma durumunu view modele bildirir
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


        /* SELECTED DAYS */
        binding.itemRowReminderDays.Mon.setOnClickListener { viewModel.onSelectedDays("Monday") }
        binding.itemRowReminderDays.Tue.setOnClickListener { viewModel.onSelectedDays("Tuesday") }
        binding.itemRowReminderDays.Wed.setOnClickListener { viewModel.onSelectedDays("Wednesday") }
        binding.itemRowReminderDays.Thu.setOnClickListener { viewModel.onSelectedDays("Thursday") }
        binding.itemRowReminderDays.Fri.setOnClickListener { viewModel.onSelectedDays("Friday") }
        binding.itemRowReminderDays.Sat.setOnClickListener { viewModel.onSelectedDays("Saturday") }
        binding.itemRowReminderDays.Sun.setOnClickListener { viewModel.onSelectedDays("Sunday") }

        viewModel.selectedDays.observe(this) { days ->
            Log.e("DAYS", "Selected days: ${days}")
            updateDayUI(
                selectedDays = days,
                color = viewModel.selectedColor.value
            )

        }


        /* TIME PICKER */
        binding.itemRowReminderTime.reminderTime.setOnClickListener {
            viewModel.onTimeClicked()
        }

        //Timepickerı aç
        viewModel.openTimePicker.observe(this) {

            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Saat Seç")
                .build()

            timePicker.show(supportFragmentManager, "TIME_PICKER")

            timePicker.addOnPositiveButtonClickListener {
                val hour = timePicker.hour
                val minute = timePicker.minute

                viewModel.onTimeSelected(hour, minute)
                Log.d("TIME", "Seçilen saat: $hour:$minute")
            }
        }

        //Seçilen saati ekrana yaz
        viewModel.selectedTime.observe(this) { time ->
            binding.itemRowReminderTime.reminderTime.text = time
        }


        /* SOUND */
        fun playSound(id: Int) {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(this, id)
            mediaPlayer?.start()
        }

        viewModel.playSoundEvent.observe(this) { soundResId ->
            playSound(soundResId)
        }
        viewModel.onSoundSelected(R.raw.sound1)




        //BUTONA TIKLAMA bilgisini viewholder a haber ver
        binding.saveHabit.setOnClickListener {
            viewModel.onInputClicked(
                binding.itemRowHabitHeader.habitTitle.text.toString().trim(),
                binding.itemRowTargetValue.targetValue.text.toString().trim(),
                binding.itemRowReminderMessage.reminderMessage.text.toString().trim()
            )

        }

        viewModel.validation.observe(this){ result ->
            binding.itemRowHabitHeader.tvHabitTitleError.visibility=View.GONE
            binding.itemRowTargetValue.tvTargetValueError.visibility= View.GONE
            binding.itemRowReminderMessage.tvReminderMessageError.visibility=View.GONE


            when(result){
                HabitValidation.HabitTitleEmpty ->{
                    binding.itemRowHabitHeader.tvHabitTitleError.text= "❗Habit title cannot be left blank."
                    binding.itemRowHabitHeader.tvHabitTitleError.visibility=View.VISIBLE
                }
                HabitValidation.TargetValueEmpty ->{
                    binding.itemRowTargetValue.tvTargetValueError.text = "❗Target value cannot be left blank "
                    binding.itemRowTargetValue.tvTargetValueError.visibility= View.VISIBLE

                }
                HabitValidation.ReminderMessageEmpty ->{
                    binding.itemRowReminderMessage.tvReminderMessageError.text = "❗Reminder message cannot be left blank."
                    binding.itemRowReminderMessage.tvReminderMessageError.visibility=View.VISIBLE
                }
                HabitValidation.Success -> {
                    // BURADA KAYDETME İŞLEMİ
                    viewModel.onSaveHabitClicked()
                }
            }
        }




    }

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

    fun updateDayUI(
        selectedDays: Set<String>,
        color: String?
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

            val drawable =
                textView.background.mutate() as GradientDrawable

            val appliedColor = if (selectedDays.contains(day)) {
                // seçili → tam opacity
                applyColorWithOpacity(color, 1f)
            } else {
                // seçili değil → %20 opacity
                applyColorWithOpacity(color, 0.2f)
            }

            drawable.setColor(appliedColor)
        }
    }

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


    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }
}