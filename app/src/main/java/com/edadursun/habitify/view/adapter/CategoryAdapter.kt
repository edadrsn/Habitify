package com.edadursun.habitify.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edadursun.habitify.R

class CategoryAdapter(
    private val categories: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    // Her bir renk item'ının ViewHolder'ı
    inner class CategoryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val text=view.findViewById<TextView>(R.id.categoryText)
        //View ile kategoriyi birbirine bağlıyoruz
        fun bind(category: String) {
            text.text=category
            view.setOnClickListener { onClick(category) }
        }
    }

    //ite_category_xml i burada inflate edicez
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_bg, parent, false)
        return CategoryViewHolder(view)
    }

    //Kullanıcıya gösterilecek olan kategorilerin sayısı
    override fun getItemCount() = categories.size

    //Kategoriyi viewholdera bağlıyoruz
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }
}