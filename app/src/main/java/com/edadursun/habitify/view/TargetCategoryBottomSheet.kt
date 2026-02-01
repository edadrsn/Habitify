package com.edadursun.habitify.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edadursun.habitify.R
import com.edadursun.habitify.ui.CategoryType
import com.edadursun.habitify.ui.EmojiProvider
import com.edadursun.habitify.view.adapter.CategoryAdapter
import com.edadursun.habitify.view.adapter.EmojiAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TargetCategoryBottomSheet(
    private val onCategorySelected: (String) -> Unit   //Kullanıcı kategori seçtiğinde dışarıya gönderilir
):BottomSheetDialogFragment(){

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Bottomsheetin layoutunu buraya inflate ederiz
        val view = inflater.inflate(R.layout.bottom_sheet_category_picker, container, false)

        //Kategorilerin gösterileceği recyclerview
        val recyclerView = view.findViewById<RecyclerView>(R.id.categoryRecyclerView)

        //Kategorileri grid şeklinde göstermek için
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)

        //Recyclerviewi kategori adapter bağalrız
        recyclerView.adapter = CategoryAdapter(CategoryType.categories) { category ->
            //Kullanıcı kategori seçtiğinde seçilen rengi callback ile dışarı gönderir
            onCategorySelected(category)
            dismiss()
        }

        return view
    }

}