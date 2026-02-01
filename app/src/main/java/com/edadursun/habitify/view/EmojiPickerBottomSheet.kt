package com.edadursun.habitify.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edadursun.habitify.R
import com.edadursun.habitify.ui.ColorPalette
import com.edadursun.habitify.ui.EmojiProvider
import com.edadursun.habitify.view.adapter.ColorAdapter
import com.edadursun.habitify.view.adapter.EmojiAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EmojiPickerBottomSheet(
    private val onEmojiSelected: (String) -> Unit // Kullanıcı emoji seçtiğinde dışarıya gönderir
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //Bottomsheetin layoutunu buraya inflate ederiz
        val view = inflater.inflate(R.layout.bottom_sheet_emoji_picker, container, false)

        //Emojilerin gösterileceği recyclerview
        val recyclerView = view.findViewById<RecyclerView>(R.id.emojiRecyclerView)

        //Emojileri grid şeklinde göstermek için
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 6)

        //Recyclerviewi emoji adapter bağalrız
        recyclerView.adapter = EmojiAdapter(EmojiProvider.emojiList) { emoji ->
            //Kullanıcı renk seçtiğinde seçilen rengi callback ile dışarı gönderir
            onEmojiSelected(emoji)
            dismiss()
        }

        return view

    }

}