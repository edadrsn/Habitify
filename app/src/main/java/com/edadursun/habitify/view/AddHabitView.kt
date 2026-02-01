package com.edadursun.habitify.view

import android.app.TimePickerDialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.edadursun.habitify.R
import com.edadursun.habitify.databinding.ActivityAddHabitViewBinding
import com.edadursun.habitify.viewmodel.AddHabitViewModel
import java.util.Calendar

class AddHabitView : AppCompatActivity() {

    private lateinit var binding:ActivityAddHabitViewBinding
    private lateinit var viewModel: AddHabitViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityAddHabitViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Viewmodel lifecycle a bağlı şekilde oluşturulur
        viewModel=ViewModelProvider(this)[AddHabitViewModel::class.java]

        /* BACK BUTTON */
        //Geri butonuna tıklandığında viewmodele haber verilir
        binding.goBack.setOnClickListener {
            viewModel.onGoBackClicked()
        }

        //viewmodele geri dönme olayı gelirse activitiyi kapat
        viewModel.goToHomeView.observe(this){back ->
            finish()
        }


        /* COLOR PICKER */
        //Renk seçme alanına tıklanırsa bottomsheeti açılır
        binding.itemRowColor.chooseColor.setOnClickListener {
            val sheetColor=ColorPickerBottomSheet{selectedColor ->
                // seçilennrenk viewmodele gönderilir
                viewModel.onColorSelected(selectedColor)
            }

            sheetColor.show(supportFragmentManager,"ColorPicker")
        }
        // Seçilen renk değişirse ui güncellenir
        viewModel.selectedColor.observe(this){color ->

            //Genel arka plan renkleri
            binding.main.setBackgroundColor(Color.parseColor(color))
            binding.materialCardView.setBackgroundColor(Color.parseColor(color))
            binding.scrollViewAddHabitView.setBackgroundColor(Color.parseColor(color))
            //binding.saveHabit.setBackgroundColor(Color.parseColor(color))

            //Emojinin arka plan rengini ayarla
            val emojiBg = binding.itemRowHabitHeader.selectedEmoji.background as GradientDrawable
            emojiBg.setColor(Color.parseColor(color))

            //Kullanıcının seçtiği rengi göster
            val userSelectedColor=binding.itemRowColor.userSelectedColor.background as GradientDrawable
            userSelectedColor.setColor(Color.parseColor(color))
        }


        /* EMOJİ PICKER */
        //Emoji seçme alanına tıklandığında bottomsheet açılır
        binding.itemRowEmoji.chooseEmoji.setOnClickListener {
            val sheetEmoji=EmojiPickerBottomSheet{selectedEmoji ->
                //Seçilen emoji viewmodele gönderilir
                viewModel.onEmojiSelected(selectedEmoji)
            }
            sheetEmoji.show(supportFragmentManager,"EmojiPicker")
        }

        // Seçilen emoji değiştiğinde ui günceller
        viewModel.selectedEmoji.observe(this){emoji ->
            if (emoji == null) {
                // Emoji seçilmemişse → artı ikonunu göster
                binding.itemRowHabitHeader.selectedEmoji.setImageResource(R.drawable.icons_plus)
                binding.pageIcon.setImageResource(R.drawable.icon_bg)
            } else {
                // Emoji seçildiyse → emoji'yi göster
                binding.itemRowHabitHeader.selectedEmoji.setImageDrawable(null)
                binding.itemRowHabitHeader.selectedEmoji.setImageBitmap(emojiToBitmap(emoji))
                binding.pageIcon.setImageDrawable(null)
                binding.pageIcon.setImageBitmap(emojiToBitmap(emoji))
                binding.itemRowEmoji.userSelectedIcon.setImageDrawable(null)
                binding.itemRowEmoji.userSelectedIcon.setImageBitmap(emojiToBitmap(emoji))
            }

        }



        /* CATEGORY DROPDOWN */
        //Kategori seçmeye tıklanırsa viewmodele bildir
        binding.itemRowSelectCategory.selectHabitCategory.setOnClickListener {
            viewModel.onCategoryClicked()
        }

        //Viewmodel dropdown açılmasını isterse bottomsheet açılır
        viewModel.openCategoryDropdown.observe(this){
            val sheet=TargetCategoryBottomSheet{ selectedCategory ->
               viewModel.onCategorySelected(selectedCategory)
            }
            sheet.show(supportFragmentManager,"CategoryBottomSheet")
        }

        //Seçilen kategoriyi ui da göster
        viewModel.selectedCategory.observe(this){ category ->
            binding.itemRowSelectCategory.categoryText.text=category
            binding.itemRowTargetValue.targetType.text=category

        }


        /* TIME PICKER */
        binding.itemRowReminderTime.reminderTime.setOnClickListener {
            viewModel.onTimeClicked()
        }

        //Timepickerı aç
        viewModel.openTimePicker.observe(this){
            val calendar=Calendar.getInstance()
            val hour=calendar.get(Calendar.HOUR_OF_DAY)
            val minute=calendar.get(Calendar.MINUTE)

            val timePicker=TimePickerDialog(this,
                {_,selectedHour,selectedMinute ->
                    viewModel.onTimeSelected(selectedHour,selectedMinute)
                },
                hour,
                minute,
                true
            )
            timePicker.show()
        }

        //Seçilen saati ekrana yaz
        viewModel.selectedTime.observe(this){time ->
            binding.itemRowReminderTime.reminderTime.text=time
        }


        //Butona tıklama bilgisini viewholder a haber ver
        binding.saveHabit.setOnClickListener {
            /* TARGET VALUE */
            val targetValueText=binding.itemRowTargetValue.targetValue.text.toString().trim()
            if(targetValueText.isEmpty()){
                Log.e("INPUT","Target value boş")
                return@setOnClickListener
            }

            val targetValue=targetValueText.toIntOrNull()
            if(targetValue==null){
                Log.e("INPUT","Girilen değer sayı değil")
                return@setOnClickListener
            }

            viewModel.onTargetValueSelected(targetValue)


            viewModel.onSaveHabitClicked()
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



}