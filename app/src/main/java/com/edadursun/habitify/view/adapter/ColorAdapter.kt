package com.edadursun.habitify.view.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edadursun.habitify.R

//Renkleri recyclerviewda dinamik ve tekrar kullanılabilir şekilde göstermek ve kullanıcının seçtiği rengi viewmodele bildirmek amaçlandı
//Veriyi gösterir , Tıklamayı yakalar , Sonucu dışarı verir
class ColorAdapter(
    private val colors : List<String>,  // Gösterilecek renk listesi
    private val onClick : (String) -> Unit  // Renk tıklandığında çalışacak callback Adapter ViewModel'ı bilmez, sadece seçilen rengi dışarı verir
):RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    // Her bir renk item'ının ViewHolder'ı
    inner class ColorViewHolder(val view: View): RecyclerView.ViewHolder(view){
        // View ile rengi birbirine bağladığımız yer
        fun bind(color:String){
            val drawable = view.background as GradientDrawable
            drawable.setColor(Color.parseColor(color))
            view.setOnClickListener { onClick(color) } // Kullanıcı bu renge tıklarsa seçilen rengi callback ile dışarı gönder
        }
    }

    // item_color_circle XML'i burada inflate edilir
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.item_color_circle,parent,false)
       return ColorViewHolder(view)
    }

    // RecyclerView'da kaç tane item gösterilecek
    override fun getItemCount()= colors.size

    // Belirli pozisyondaki rengi ViewHolder'a bağlar
    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(colors[position])
    }

}