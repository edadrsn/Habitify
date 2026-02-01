package com.edadursun.habitify.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edadursun.habitify.R
import com.edadursun.habitify.ui.ColorPalette
import com.edadursun.habitify.view.adapter.ColorAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

//Renk seçme ui ını ayrı tekrar kullanılabilir ve ekrana bağlı olmayan bir bileşen haline getirmek için yaptım
class ColorPickerBottomSheet(
    private val onColorSelected: (String) -> Unit //Kullanıcı bir renk seçtiğinde çalışır ve seçilen rengi dışarıya(activity/fragment) gönderir
) : BottomSheetDialogFragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //Bottomsheetin layoutunu buraya inflate ederiz
        val view = inflater.inflate(R.layout.bottom_sheet_color_picker, container, false)

        //Renklerin gösterileceği recyclerview
        val recyclerView = view.findViewById<RecyclerView>(R.id.colorRecyclerView)

        //Renkleri grid şeklinde göstermek için
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 6)

        //Recyclerviewi color adapter bağlarız
        recyclerView.adapter = ColorAdapter(ColorPalette.colors) { color ->
            //Kullanıcı renk seçtiğinde seçilen rengi callback ile dışarı gönderir
            onColorSelected(color)
            dismiss()
        }

        return view
    }

}